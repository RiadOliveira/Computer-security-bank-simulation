package process.server;

import java.net.Socket;

import javax.crypto.SecretKey;

import dtos.DTO;
import dtos.account.BankAccount;
import dtos.account.ClientData;
import dtos.auth.AuthData;
import dtos.auth.AuthResponse;
import dtos.generic.ExceptionDTO;
import error.AppException;
import error.SecureException;
import process.AppCommand;
import process.AppThread;
import security.CryptoProcessor;
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
      throw new SecureException(
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
    AuthData parsedDTO = ObjectConverter.convert(dto);

    BankAccount findedAccount = ServerProcess.findAccountByAgencyAndNumber(
      parsedDTO.getAgency(), parsedDTO.getAccountNumber()
    );
    if(findedAccount == null) {
      throw new AppException("Dados de autenticação inválidos!");
    }

    boolean correctPassword = PasswordHasher.passwordsAreEqual(
      findedAccount.getClientData().getPassword(), parsedDTO.getPassword()
    );
    if(!correctPassword) {
      throw new AppException("Dados de autenticação inválidos");
    }

    SecretKey generatedKey = CryptoProcessor.generateKey();
    AuthResponse authResponse = new AuthResponse(
      generatedKey, findedAccount.getClientData()
    );
    sendDTO(authResponse);
    
    authKey = generatedKey;
    clientAccount = findedAccount;
  }

  @Override
  protected void handleGetAccountData(DTO dto) throws Exception {
    ObjectConverter.convert(dto);
    sendDTO(clientAccount);
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
