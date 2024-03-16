package process.server;

import java.io.EOFException;
import java.net.Socket;

import javax.crypto.SecretKey;

import dtos.DTO;
import dtos.account.BankAccount;
import dtos.account.ClientData;
import dtos.auth.AuthData;
import dtos.auth.AuthResponse;
import dtos.generic.ExceptionDTO;
import dtos.generic.MessageDTO;
import dtos.generic.ValueDTO;
import dtos.operation.IncomeProjectionDTO;
import dtos.operation.WireTransferDTO;
import error.AppException;
import error.SecureException;
import process.AppCommand;
import process.AppThread;
import security.crypto.CryptoProcessor;
import utils.ConsolePrinter;
import utils.ObjectConverter;
import utils.PasswordHasher;
import utils.ValueFormatter;

public class ServerThread extends AppThread {
  protected BankAccount clientAccount = null;
  
  public ServerThread(Socket clientSocket) {
    super(clientSocket, true);
  }

  @Override
  public void execute() {
    boolean clientDisconnected = false;

    while(!clientDisconnected) {
      try {
        handleReceivedDTO(receiveDTO());
      } catch (Exception exception) {
        clientDisconnected = exception instanceof EOFException;
        if(clientDisconnected) {
          ConsolePrinter.printlnError(
            "Conexão com o cliente perdida, finalizando thread...\n"
          );
          return;
        }

        handleErrorDTOSending(exception);
      }
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
      throw new AppException("Conta especificada não encontrada!");
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
    sendDTO(clientAccount);
  }

  @Override
  protected void handleWithdraw(DTO dto) throws Exception {
    ValueDTO parsedDTO = ObjectConverter.convert(dto);

    double withdrawValue = parsedDTO.getValue();
    if(withdrawValue <= 0.0) {
      throw new AppException("Valor de saque inválido!");
    }

    double accountBalance = clientAccount.getBalance();
    if(withdrawValue > accountBalance) {
      throw new AppException(
        "Valor de saque maior que saldo disponível!"
      );
    }

    clientAccount.updateBalance(-withdrawValue);

    String withdrawFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(withdrawValue);
    String updatedBalanceFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(clientAccount.getBalance());

    MessageDTO messageDTO = new MessageDTO(
      "Valor de " + withdrawFormattedValue +
      " retirado com sucesso, seu saldo atual é " +
      updatedBalanceFormattedValue + '.'
    );
    sendDTO(messageDTO);
  }

  @Override
  protected void handleDeposit(DTO dto) throws Exception {
    ValueDTO parsedDTO = ObjectConverter.convert(dto);

    double depositValue = parsedDTO.getValue();
    if(depositValue <= 0.0) {
      throw new AppException("Valor de depósito inválido!");
    }

    clientAccount.updateBalance(depositValue);

    String depositFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(depositValue);
    String updatedBalanceFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(clientAccount.getBalance());

    MessageDTO messageDTO = new MessageDTO(
      "Valor de " + depositFormattedValue +
      " depositado com sucesso, seu saldo atual é " +
      updatedBalanceFormattedValue + '.'
    );
    sendDTO(messageDTO);
  }

  @Override
  protected void handleWireTransfer(DTO dto) throws Exception {
    WireTransferDTO parsedDTO = ObjectConverter.convert(dto);

    double transferValue = parsedDTO.getValue();
    if(transferValue <= 0.0) {
      throw new AppException(
        "Valor de transferência inválido!"
      );
    }

    double accountBalance = clientAccount.getBalance();
    if(transferValue > accountBalance) {
      throw new AppException(
        "Valor de transferência maior que saldo disponível!"
      );
    }

    String agencyToTransfer = parsedDTO.getTargetAgency();
    String accountNumberToTransfer = parsedDTO.getTargetAccountNumber();

    boolean equalAgency = agencyToTransfer.equals(clientAccount.getAgency());
    boolean equalAccountNumber = accountNumberToTransfer.equals(
      clientAccount.getAccountNumber()
    );
    if(equalAgency && equalAccountNumber) {
      throw new AppException(
        "Não é permitido fazer uma transferência para si mesmo!"
      );
    }

    BankAccount findedAccountToTransfer = ServerProcess.
      findAccountByAgencyAndNumber(
        agencyToTransfer, accountNumberToTransfer
      );
    if(findedAccountToTransfer == null) {
      throw new AppException(
        "Conta de transferência não encontrada!"
      );
    }

    clientAccount.updateBalance(-transferValue);
    findedAccountToTransfer.updateBalance(transferValue);

    String transferFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(transferValue);
    String updatedBalanceFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(clientAccount.getBalance());

    MessageDTO messageDTO = new MessageDTO(
      "Valor de " + transferFormattedValue + 
      " transferido com sucesso para " +
      findedAccountToTransfer.getClientData().getName() +
      ", seu saldo atual é " + updatedBalanceFormattedValue +
      '.'
    );
    sendDTO(messageDTO);
  }

  @Override
  protected void handleGetBalance(DTO dto) throws Exception {
    String balanceFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(clientAccount.getBalance());

    MessageDTO messageDTO = new MessageDTO(
      "Seu saldo atual é de " + balanceFormattedValue + '.'
    );
    sendDTO(messageDTO);
  }

  @Override
  protected void handleGetSavingsProjections(DTO dto) throws Exception {
    IncomeProjectionDTO incomeProjectionDTO = new IncomeProjectionDTO(
      clientAccount.getBalance(),
      ServerProcess.SAVINGS_YIELD_PERCENTAGE,
      ServerProcess.MONTHS_FOR_PROJECTIONS
    );
    sendDTO(incomeProjectionDTO);
  }

  @Override
  protected void handleGetFixedIncomeProjections(DTO dto) throws Exception {
    IncomeProjectionDTO incomeProjectionDTO = new IncomeProjectionDTO(
      clientAccount.getFixedIncome(),
      ServerProcess.FIXED_INCOME_YIELD_PERCENTAGE,
      ServerProcess.MONTHS_FOR_PROJECTIONS
    );
    sendDTO(incomeProjectionDTO);
  }

  @Override
  protected void handleUpdateFixedIncome(DTO dto) throws Exception {
    ValueDTO parsedDTO = ObjectConverter.convert(dto);

    double fixedIncomeUpdateValue = parsedDTO.getValue();
    if(fixedIncomeUpdateValue == 0.0) {
      throw new AppException(
        "Valor de atualização de renda fixa inválido!"
      );
    }

    double accountFixedIncome = clientAccount.getFixedIncome();
    if(accountFixedIncome + fixedIncomeUpdateValue < 0.0) {
      throw new AppException(
        "Valor de atualização de renda fixa está retirando" +
        " um valor maior que o disponível na renda fixa!"
      );
    }

    double accountBalance = clientAccount.getBalance();
    if(accountBalance - fixedIncomeUpdateValue < 0.0) {
      throw new AppException(
        "Valor de atualização de renda fixa está adicionando" +
        " um valor maior que o disponível no saldo da conta!"
      );
    }

    clientAccount.updateFixedIncome(fixedIncomeUpdateValue);

    String fixedIncomeUpdateFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(fixedIncomeUpdateValue);
    String updatedFixedIncomeFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(clientAccount.getFixedIncome());
    String updatedBalanceFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(clientAccount.getBalance());

    MessageDTO messageDTO = new MessageDTO(
      "A renda fixa foi atualizada com o valor de " +
      fixedIncomeUpdateFormattedValue +
      ", sua renda fixa atual é " +
      updatedFixedIncomeFormattedValue +
      ", e seu saldo atual é " +
      updatedBalanceFormattedValue + '.'
    );
    sendDTO(messageDTO);
  }
}
