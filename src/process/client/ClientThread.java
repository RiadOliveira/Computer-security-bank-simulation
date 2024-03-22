package process.client;

import java.io.EOFException;
import java.net.Socket;

import javax.crypto.SecretKey;

import dtos.DTO;
import dtos.account.AuthData;
import dtos.account.ClientData;
import dtos.generic.CommandDTO;
import dtos.generic.ValueDTO;
import dtos.operation.WireTransferDTO;
import error.AppException;
import process.AppCommand;
import process.AppProcess;
import process.AppThread;
import process.ClientAttackType;
import security.crypto.AsymmetricKeyPair;
import security.crypto.AsymmetricKeyPairGenerator;
import security.crypto.CryptoProcessor;
import utils.ConsolePrinter;

public class ClientThread extends AppThread {
  public ClientThread(Socket serverSocket) {
    super(serverSocket, false);
  }

  @Override
  public void execute() {
    handleAttackerKeysChanging();
    boolean serverDisconnected = false;
    
    while(!serverDisconnected) {
      try {
        handleExecution();
      } catch (Exception exception) {
        serverDisconnected = exception instanceof EOFException;
        if(serverDisconnected) {
          ConsolePrinter.printlnError(
            "Conexão com o servidor perdida, finalizando cliente..."
          );
          return;
        }
  
        handleExecutionException(exception);
      } finally {
        ConsolePrinter.println("");
      }
    }
  }

  private void handleAttackerKeysChanging() {
    ClientAttackType attackType = ClientProcess.getAttackType();
    boolean notAttacker = attackType.equals(ClientAttackType.NONE);
    if(notAttacker) return;

    SecretKey attackerSymmetricKey = CryptoProcessor.generateKey();
    AsymmetricKeyPair attackerAsymmetricKeyPair = 
      AsymmetricKeyPairGenerator.generate();

    switch(attackType) {
      case ENCRYPTION_KEY: {
        symmetricKeys.setEncryptionKey(attackerSymmetricKey);
        break;
      }
      case HASH_KEY: {
        symmetricKeys.setHashKey(attackerSymmetricKey);
        break;
      }
      case CONNECTED_PUBLIC_KEY: {
        connectedComponentPublicKey = attackerAsymmetricKeyPair.getPublicKey();
        break;
      }
      case PERSONAL_PRIVATE_KEY: {
        AppProcess.getAsymmetricKeyPair().setPrivateKey(
          attackerAsymmetricKeyPair.getPrivateKey()
        );
        break;
      }
      default: break;
    }
  }

  private void handleExecution() throws Exception {
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

  private void handleExecutionException(Exception exception) {
    boolean isAppException = exception instanceof AppException;

    if(!isAppException) ConsolePrinter.println("");
    ConsolePrinter.printlnError(
      isAppException ? exception.getMessage() : "Comando inserido inválido!"
    );
    ConsolePrinter.println("");
    ConsolePrinter.displayAndWaitForEnterPressing(ClientProcess.scanner);
  }

  private void handleAppCommandInput(AppCommand command) throws Exception {
    commandHandlers.get(command).accept(null);
    receiveSecureDTO();
    ConsolePrinter.displayAndWaitForEnterPressing(ClientProcess.scanner);
  }

  @Override
  protected void handleCreateAccount(DTO _d) throws Exception {
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
    sendSecureDTO(clientData);
  }

  @Override
  protected void handleAuthenticate(DTO _d) throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{"Agência", "Número da conta", "Senha"},
      ClientProcess.scanner
    );

    AuthData authData = new AuthData(
      inputsReceived[0], inputsReceived[1], inputsReceived[2]
    );
    authData.setCommand(AppCommand.AUTHENTICATE);
    sendSecureDTO(authData);
  }

  @Override
  protected void handleGetAccountData(DTO _d) throws Exception {
    sendSecureDTO(new CommandDTO(AppCommand.GET_ACCOUNT_DATA));
  }

  @Override
  protected void handleWithdraw(DTO _d) throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{"Valor de saque"},
      ClientProcess.scanner
    );

    double withdrawValue = Double.parseDouble(inputsReceived[0]);
    ValueDTO withdrawData = new ValueDTO(withdrawValue);
    withdrawData.setCommand(AppCommand.WITHDRAW);

    sendSecureDTO(withdrawData);
  }

  @Override
  protected void handleDeposit(DTO _d) throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{"Valor de depósito"},
      ClientProcess.scanner
    );

    double depositValue = Double.parseDouble(inputsReceived[0]);
    ValueDTO depositData = new ValueDTO(depositValue);
    depositData.setCommand(AppCommand.DEPOSIT);

    sendSecureDTO(depositData);
  }

  @Override
  protected void handleWireTransfer(DTO _d) throws Exception {
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

    sendSecureDTO(wireTransferData);
  }

  @Override
  protected void handleGetBalance(DTO _d) throws Exception {
    sendSecureDTO(new CommandDTO(AppCommand.GET_BALANCE));
  }

  @Override
  protected void handleGetSavingsProjections(DTO _d) throws Exception {
    sendSecureDTO(new CommandDTO(AppCommand.GET_SAVINGS_PROJECTIONS));
  }

  @Override
  protected void handleGetFixedIncomeProjections(DTO _d) throws Exception {
    sendSecureDTO(new CommandDTO(AppCommand.GET_FIXED_INCOME_PROJECTIONS));
  }

  @Override
  protected void handleUpdateFixedIncome(DTO _d) throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{"Valor de atualização da renda fixa"},
      ClientProcess.scanner
    );

    double fixedIncomeUpdateValue = Double.parseDouble(inputsReceived[0]);
    ValueDTO fixedIncomeUpdateData = new ValueDTO(fixedIncomeUpdateValue);
    fixedIncomeUpdateData.setCommand(AppCommand.UPDATE_FIXED_INCOME);

    sendSecureDTO(fixedIncomeUpdateData);
  }
}
