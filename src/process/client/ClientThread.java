package process.client;

import java.net.Socket;

import dtos.DTO;
import dtos.account.ClientData;
import error.AppException;
import process.AppCommand;
import process.AppThread;
import utils.ConsolePrinter;

public class ClientThread extends AppThread {
  public ClientThread(Socket serverSocket) {
    super(serverSocket, false);
  }

  @Override
  public void execute() {
    try {
      ConsolePrinter.printClientCommandPanel();
      int commandIndex = ClientProcess.scanner.nextInt() - 1;
      ClientProcess.scanner.nextLine();

      AppCommand[] allCommands = AppCommand.values();
      boolean clearConsoleCommand = commandIndex == allCommands.length;

      if(clearConsoleCommand) ConsolePrinter.clearConsole();
      else handleAppCommandInput(allCommands[commandIndex]);
    } catch (Exception exception) {
      ConsolePrinter.println(
        exception instanceof AppException ?
        exception.getMessage() : "Comando inserido inválido!"
      );
    } finally {
      ConsolePrinter.println("");
      execute();
    }
  }

  private void handleAppCommandInput(AppCommand command) throws Exception {
    commandHandlers.get(command).accept(null);
    receiveDTO();
    
    ConsolePrinter.print("\nPressione Enter para continuar...");
    ClientProcess.scanner.nextLine();
  }

  @Override
  protected void handleCreateAccount(DTO dto) throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{"Nome", "CPF", "Endereço", "Telefone", "Senha"},
      ClientProcess.scanner
    );

    ClientData clientData = new ClientData(
      inputsReceived[0], inputsReceived[1],
      inputsReceived[2], inputsReceived[3],
      inputsReceived[4]
    );
    clientData.setCommand(AppCommand.CREATE_ACCOUNT);
    sendDTO(clientData);
  }

  @Override
  protected void handleAuthenticate(DTO dto) throws Exception {
  }

  @Override
  protected void handleGetAccountData(DTO dto) throws Exception {
  }

  @Override
  protected void handleWithdraw(DTO dto) throws Exception {
  }

  @Override
  protected void handleDeposit(DTO dto) throws Exception {
  }

  @Override
  protected void handleWireTransfer(DTO dto) throws Exception {
  }

  @Override
  protected void handleGetBalance(DTO dto) throws Exception {
  }

  @Override
  protected void handleGetSavingsProjections(DTO dto) throws Exception {
  }

  @Override
  protected void handleGetFixedIncomeProjections(DTO dto) throws Exception {
  }

  @Override
  protected void handleUpdateFixedIncome(DTO dto) throws Exception {
  }
}
