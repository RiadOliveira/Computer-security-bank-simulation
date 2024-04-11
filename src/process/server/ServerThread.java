package process.server;

import java.io.EOFException;
import java.net.Socket;

import dtos.DTO;
import dtos.account.AuthData;
import dtos.account.BankAccount;
import dtos.account.ClientData;
import dtos.generic.ExceptionDTO;
import dtos.generic.MessageDTO;
import dtos.generic.ValueDTO;
import dtos.operation.IncomeProjectionDTO;
import dtos.operation.WireTransferDTO;
import error.AppException;
import error.SecurityException;
import process.AppCommand;
import socket.SocketThread;
import socket.components.SocketComponent;
import utils.ConsolePrinter;
import utils.ObjectConverter;
import utils.PasswordHasher;
import utils.ValueFormatter;

public class ServerThread extends SocketThread {
  protected BankAccount clientAccount = null;

  @Override
  protected void execute() throws Exception {
    handleReceivedDTO(receiveSecureDTO(SocketComponent.STORE_SERVICE));
  }

  private void handleReceivedDTO(DTO receivedDTO) throws Exception {
    AppCommand command = receivedDTO.getCommand();
    if(commandRequiresAuth(command) && clientAccount == null) {
      throw new SecurityException(
        "O usuário precisa estar autenticado para executar esta ação!"
      );
    }

    DTO dtoToSend = commandHandlers.get(command).accept(receivedDTO);
    sendSecureDTO(dtoToSend, SocketComponent.STORE_SERVICE);
  }

  protected void handleExecutionException(Exception exception) {
    try {
      String errorMessage = exception instanceof AppException ?
        exception.getMessage() : "Falha ao realizar operação!";
  
      ExceptionDTO exceptionDTO = new ExceptionDTO(errorMessage);
      sendSecureDTO(exceptionDTO, SocketComponent.STORE_SERVICE);
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
  protected DTO handleCreateAccount(DTO dto) throws Exception {
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
    return newAccount;
  }

  @Override
  protected DTO handleAuthenticate(DTO dto) throws Exception {
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

    clientAccount = findedAccount;
    return clientAccount.getClientData();
  }

  @Override
  protected DTO handleGetAccountData(DTO dto) throws Exception {
    return clientAccount;
  }

  @Override
  protected DTO handleWithdraw(DTO dto) throws Exception {
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

    return new MessageDTO(
      "Valor de " + withdrawFormattedValue +
      " retirado com sucesso, seu saldo atual é " +
      updatedBalanceFormattedValue + '.'
    );
  }

  @Override
  protected DTO handleDeposit(DTO dto) throws Exception {
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

    return new MessageDTO(
      "Valor de " + depositFormattedValue +
      " depositado com sucesso, seu saldo atual é " +
      updatedBalanceFormattedValue + '.'
    );
  }

  @Override
  protected DTO handleWireTransfer(DTO dto) throws Exception {
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

    return new MessageDTO(
      "Valor de " + transferFormattedValue + 
      " transferido com sucesso para " +
      findedAccountToTransfer.getClientData().getName() +
      ", seu saldo atual é " + updatedBalanceFormattedValue +
      '.'
    );
  }

  @Override
  protected DTO handleGetBalance(DTO dto) throws Exception {
    String balanceFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(clientAccount.getBalance());

    return new MessageDTO(
      "Seu saldo atual é de " + balanceFormattedValue + '.'
    );
  }

  @Override
  protected DTO handleGetSavingsProjections(DTO dto) throws Exception {
    return new IncomeProjectionDTO(
      clientAccount.getBalance(),
      ServerProcess.SAVINGS_YIELD_PERCENTAGE,
      ServerProcess.MONTHS_FOR_PROJECTIONS
    );
  }

  @Override
  protected DTO handleGetFixedIncomeProjections(DTO dto) throws Exception {
    return new IncomeProjectionDTO(
      clientAccount.getFixedIncome(),
      ServerProcess.FIXED_INCOME_YIELD_PERCENTAGE,
      ServerProcess.MONTHS_FOR_PROJECTIONS
    );
  }

  @Override
  protected DTO handleUpdateFixedIncome(DTO dto) throws Exception {
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

    return new MessageDTO(
      "A renda fixa foi atualizada com o valor de " +
      fixedIncomeUpdateFormattedValue +
      ", sua renda fixa atual é " +
      updatedFixedIncomeFormattedValue +
      ", e seu saldo atual é " +
      updatedBalanceFormattedValue + '.'
    );
  }
}
