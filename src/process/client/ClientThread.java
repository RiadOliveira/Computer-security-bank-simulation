package process.client;

import java.io.EOFException;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import dtos.AppCommand;
import dtos.DTO;
import dtos.account.AuthData;
import dtos.account.ClientData;
import dtos.generic.CommandDTO;
import dtos.generic.ValueDTO;
import dtos.operation.WireTransferDTO;
import error.AppException;
import process.AppProcess;
import process.ClientAttackType;
import security.crypto.AsymmetricKeyPair;
import security.crypto.AsymmetricKeyPairGenerator;
import security.crypto.CryptoProcessor;
import socket.SocketThread;
import socket.components.SocketComponent;
import socket.data.SocketData;
import utils.ConsolePrinter;

public class ClientThread extends SocketThread {
  public ClientThread(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
  }

  @Override
  public void execute() throws Exception {
    ConsolePrinter.printClientCommandPanel();
    int commandIndex = Integer.parseInt(
      ClientProcess.scanner.nextLine()
    ) - 1;
    ConsolePrinter.println("");

    AppCommand[] allCommands = AppCommand.values();
    boolean clearConsoleCommand = commandIndex == allCommands.length;

    if(clearConsoleCommand) ConsolePrinter.clearConsole();
    else handleAppCommandInput(allCommands[commandIndex]);
  }

  protected void handleExecutionException(Exception exception) {
    boolean isAppException = exception instanceof AppException;

    if(!isAppException) ConsolePrinter.println("");
    ConsolePrinter.printlnError(
      isAppException ? exception.getMessage() : "Comando inserido inválido!"
    );
    ConsolePrinter.println("");
    ConsolePrinter.displayAndWaitForEnterPressing(ClientProcess.scanner);
  }

  private void handleAppCommandInput(AppCommand command) throws Exception {
    DTO dtoToSend = commandHandlers.get(command).accept(null);
    sendSecureDTO(SocketComponent.STORE_SERVICE, dtoToSend);

    receiveSecureDTO(SocketComponent.STORE_SERVICE);
    ConsolePrinter.displayAndWaitForEnterPressing(ClientProcess.scanner);
    ConsolePrinter.println("");
  }

  @Override
  protected DTO handleCreateAccount(DTO _d) throws Exception {
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
    return clientData;
  }

  @Override
  protected DTO handleAuthenticate(DTO _d) throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{"Agência", "Número da conta", "Senha"},
      ClientProcess.scanner
    );

    AuthData authData = new AuthData(
      inputsReceived[0], inputsReceived[1], inputsReceived[2]
    );
    authData.setCommand(AppCommand.AUTHENTICATE);
    return authData;
  }

  @Override
  protected DTO handleGetAccountData(DTO _d) throws Exception {
    return new CommandDTO(AppCommand.GET_ACCOUNT_DATA);
  }

  @Override
  protected DTO handleWithdraw(DTO _d) throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{"Valor de saque"},
      ClientProcess.scanner
    );

    double withdrawValue = Double.parseDouble(inputsReceived[0]);
    ValueDTO withdrawData = new ValueDTO(withdrawValue);
    withdrawData.setCommand(AppCommand.WITHDRAW);

    return withdrawData;
  }

  @Override
  protected DTO handleDeposit(DTO _d) throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{"Valor de depósito"},
      ClientProcess.scanner
    );

    double depositValue = Double.parseDouble(inputsReceived[0]);
    ValueDTO depositData = new ValueDTO(depositValue);
    depositData.setCommand(AppCommand.DEPOSIT);

    return depositData;
  }

  @Override
  protected DTO handleWireTransfer(DTO _d) throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{
        "Agência alvo", "Número da conta alvo",
        "Valor da transferência"
      },
      ClientProcess.scanner
    );

    double transferValue = Double.parseDouble(inputsReceived[2]);
    WireTransferDTO wireTransferData = new WireTransferDTO(
      transferValue, inputsReceived[0], inputsReceived[1]
    );
    wireTransferData.setCommand(AppCommand.WIRE_TRANSFER);

    return wireTransferData;
  }

  @Override
  protected DTO handleGetBalance(DTO _d) throws Exception {
    return new CommandDTO(AppCommand.GET_BALANCE);
  }

  @Override
  protected DTO handleGetSavingsProjections(DTO _d) throws Exception {
    return new CommandDTO(AppCommand.GET_SAVINGS_PROJECTIONS);
  }

  @Override
  protected DTO handleGetFixedIncomeProjections(DTO _d) throws Exception {
    return new CommandDTO(AppCommand.GET_FIXED_INCOME_PROJECTIONS);
  }

  @Override
  protected DTO handleUpdateFixedIncome(DTO _d) throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{"Valor de atualização da renda fixa"},
      ClientProcess.scanner
    );

    double fixedIncomeUpdateValue = Double.parseDouble(inputsReceived[0]);
    ValueDTO fixedIncomeUpdateData = new ValueDTO(fixedIncomeUpdateValue);
    fixedIncomeUpdateData.setCommand(AppCommand.UPDATE_FIXED_INCOME);

    return fixedIncomeUpdateData;
  }
}
