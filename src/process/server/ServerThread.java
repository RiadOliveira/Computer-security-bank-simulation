package process.server;

import java.net.Socket;

import dtos.DTO;
import dtos.account.BankAccount;
import dtos.account.ClientData;
import dtos.generic.ExceptionDTO;
import error.AppException;
import process.AppCommand;
import process.AppThread;
import utils.ConsolePrinter;
import utils.ObjectConverter;
import utils.PasswordHasher;

public class ServerThread extends AppThread {
  protected BankAccount clientAccount = null;
  
  public ServerThread(Socket clientSocket) {
    super(clientSocket, true);
  }

  @Override
  public void execute() {
    try {
      handleReceivedDTO(receiveDTO());
      execute();
    } catch (Exception exception) {
      handleErrorDTOSending(exception);
    } finally {
      ConsolePrinter.println(""); 
    }
  }

  private void handleReceivedDTO(DTO receivedDTO) throws Exception {
    AppCommand command = receivedDTO.getCommand();
    if(commandRequiresAuth(command) && authKey == null) {
      throw new SecurityException(
        "O usuário precisa estar autenticado para executar esta ação!"
      );
    }

    commandHandlers.get(command).accept(receivedDTO);
  }

  private void handleErrorDTOSending(Exception exception) {
    try {
      String errorMessage = exception instanceof AppException ?
        exception.getMessage() : "Falha ao realizar operação!";
  
      ExceptionDTO exceptionDTO = new ExceptionDTO(errorMessage);
      sendDTO(exceptionDTO);
      execute();
    } catch (Exception e) {
      ConsolePrinter.println("Falha ao se comunicar com o cliente!");
    }
  }

  private boolean commandRequiresAuth(AppCommand command) {
    boolean isCreateAccount = command.equals(AppCommand.CREATE_ACCOUNT);
    boolean isAuthenticate = command.equals(AppCommand.AUTHENTICATE);

    return !isCreateAccount && !isAuthenticate;
  }

  @Override
  protected void handleCreateAccount(DTO dto) throws Exception {
    ClientData parsedDTO = ObjectConverter.convert(dto);

    boolean cpfAlreadyExists = ServerProcess.existsAccountWithCpf(
      parsedDTO.getCpf()
    );
    if(cpfAlreadyExists) {
      throw new AppException("Uma conta com esse cpf já existe!");
    }

    String hashedPassword = PasswordHasher.hashAndEncode(
      parsedDTO.getPassword()
    );
    parsedDTO.setPassword(hashedPassword);

    BankAccount newAccount = new BankAccount(parsedDTO);
    ServerProcess.addDatabaseAccount(newAccount);
    sendDTO(newAccount);
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
