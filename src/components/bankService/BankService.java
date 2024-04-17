package components.bankService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import dtos.auth.AuthenticatedDTO;
import dtos.bankOperation.WireTransferDTO;
import dtos.generic.ExceptionDTO;
import dtos.generic.MessageDTO;
import dtos.generic.ValueDTO;
import dtos.user.BankAccount;
import utils.ObjectConverter;
import utils.ValueFormatter;

public class BankService extends BaseBankService {
  public BankService(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
  }

  @Override
  protected void execute() throws Exception {
    AuthenticatedDTO receivedDTO = ObjectConverter.convert(
      receiveSecureDTO(SocketComponent.GATEWAY)
    );
    RemoteOperation operation = receivedDTO.getOperation();

    DTO responseDTO = operationHandlers.get(operation).run(receivedDTO);
    sendSecureDTO(SocketComponent.GATEWAY, responseDTO);
  }

  @Override
  protected void handleExecutionException(Exception exception) {
    executeDefaultExceptionHandling(exception);
  }

  @Override
  protected DTO redirectToDatabase(
    DTO dtoToRedirect
  ) throws Exception {
    sendSecureDTO(SocketComponent.BANK_DATABASE, dtoToRedirect);
    return receiveSecureDTO(SocketComponent.BANK_DATABASE);
  }

  @Override
  protected DTO createAccount(
    AuthenticatedDTO authenticatedDTO
  ) throws Exception {
    UUID userId = authenticatedDTO.getUserId();
    DTO newAccount = new AuthenticatedDTO(
      userId, new BankAccount(userId)
    ).setOperation(authenticatedDTO.getOperation());

    return redirectToDatabase(newAccount);
  }

  @Override
  protected DTO withdraw(
    AuthenticatedDTO authenticatedDTO
  ) throws Exception {
    ValueDTO parsedDTO = ObjectConverter.convert(
      authenticatedDTO.getDTO()
    );

    double withdrawValue = parsedDTO.getValue();
    if(withdrawValue <= 0.0) {
      return new ExceptionDTO("Valor de saque inválido!");
    }

    double accountBalance = getBalanceFromDatabase(
      authenticatedDTO.getUserId()
    );
    if(withdrawValue > accountBalance) {
      return new ExceptionDTO(
        "Valor de saque maior que saldo disponível!"
      );
    }

    var updatedBalanceDTO = (ValueDTO) redirectToDatabase(
      authenticatedDTO
    );
    String withdrawFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(withdrawValue);
    String updatedBalanceFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(updatedBalanceDTO.getValue());

    return new MessageDTO(
      "Valor de " + withdrawFormattedValue +
      " retirado com sucesso, seu saldo atual é " +
      updatedBalanceFormattedValue + '.'
    );
  }

  @Override
  protected DTO deposit(
    AuthenticatedDTO authenticatedDTO
  ) throws Exception {
    ValueDTO parsedDTO = ObjectConverter.convert(
      authenticatedDTO.getDTO()
    );

    double depositValue = parsedDTO.getValue();
    if(depositValue <= 0.0) {
      return new ExceptionDTO("Valor de depósito inválido!");
    }

    var updatedBalanceDTO = (ValueDTO) redirectToDatabase(
      authenticatedDTO
    );
    String depositFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(depositValue);
    String updatedBalanceFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(updatedBalanceDTO.getValue());

    return new MessageDTO(
      "Valor de " + depositFormattedValue +
      " depositado com sucesso, seu saldo atual é " +
      updatedBalanceFormattedValue + '.'
    );
  }

  @Override
  protected DTO wireTransfer(
    AuthenticatedDTO authenticatedDTO
  ) throws Exception {
    WireTransferDTO parsedDTO = ObjectConverter.convert(
      authenticatedDTO.getDTO()
    );

    double transferValue = parsedDTO.getValue();
    if(transferValue <= 0.0) {
      return new ExceptionDTO("Valor de transferência inválido!");
    }

    double accountBalance = getBalanceFromDatabase(
      authenticatedDTO.getUserId()
    );
    if(transferValue > accountBalance) {
      return new ExceptionDTO(
        "Valor de transferência maior que saldo disponível!"
      );
    }

    var userAccount = (BankAccount) redirectToDatabase(
      new AuthenticatedDTO(authenticatedDTO.getUserId()).
      setOperation(RemoteOperation.GET_ACCOUNT_DATA)
    );
    boolean equalAgency = parsedDTO.getTargetAgency().equals(
      userAccount.getAgency()
    );
    boolean equalAccountNumber = parsedDTO.getTargetAccountNumber().equals(
      userAccount.getAccountNumber()
    );
    if(equalAgency && equalAccountNumber) {
      return new ExceptionDTO(
        "Não é permitido fazer uma transferência para si mesmo!"
      );
    }

    var updatedBalanceDTO = (ValueDTO) redirectToDatabase(
      authenticatedDTO
    );
    String transferFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(transferValue);
    String updatedBalanceFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(updatedBalanceDTO.getValue());

    return new MessageDTO(
      "Valor de " + transferFormattedValue + 
      " transferido com sucesso, seu saldo atual é " +
      updatedBalanceFormattedValue + '.'
    );
  }

  @Override
  protected DTO getBalance(
    AuthenticatedDTO authenticatedDTO
  ) throws Exception {
    double balanceValue = getBalanceFromDatabase(authenticatedDTO.getUserId());

    String formattedBalanceValue = ValueFormatter.formatToBrazilianCurrency(
      balanceValue
    );
    return new MessageDTO("Seu saldo atual é de " + formattedBalanceValue + ".");
  }

  @Override
  protected DTO updateFixedIncome(
    AuthenticatedDTO authenticatedDTO
  ) throws Exception {
    ValueDTO parsedDTO = ObjectConverter.convert(
      authenticatedDTO.getDTO()
    );

    double fixedIncomeUpdateValue = parsedDTO.getValue();
    if(fixedIncomeUpdateValue == 0.0) {
      return new ExceptionDTO(
        "Valor de atualização de renda fixa inválido!"
      );
    }

    var userAccount = (BankAccount) redirectToDatabase(
      new AuthenticatedDTO(authenticatedDTO.getUserId()).
      setOperation(RemoteOperation.GET_ACCOUNT_DATA)
    );

    double accountFixedIncome = userAccount.getFixedIncome();
    if(accountFixedIncome + fixedIncomeUpdateValue < 0.0) {
      return new ExceptionDTO(
        "Valor de atualização de renda fixa está retirando" +
        " um valor maior que o disponível na renda fixa!"
      );
    }

    double accountBalance = userAccount.getBalance();
    if(accountBalance - fixedIncomeUpdateValue < 0.0) {
      return new ExceptionDTO(
        "Valor de atualização de renda fixa está adicionando" +
        " um valor maior que o disponível no saldo da conta!"
      );
    }

    var updatedUserAccount = (BankAccount) redirectToDatabase(
      authenticatedDTO
    );
    String fixedIncomeUpdateFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(fixedIncomeUpdateValue);
    String updatedFixedIncomeFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(updatedUserAccount.getFixedIncome());
    String updatedBalanceFormattedValue = ValueFormatter.
      formatToBrazilianCurrency(updatedUserAccount.getBalance());

    return new MessageDTO(
      "A renda fixa foi atualizada com o valor de " +
      fixedIncomeUpdateFormattedValue +
      ", sua renda fixa atual é " +
      updatedFixedIncomeFormattedValue +
      ", e seu saldo atual é " +
      updatedBalanceFormattedValue + '.'
    );
  }

  private double getBalanceFromDatabase(UUID userId) throws Exception {
    DTO dtoToSend =  (new AuthenticatedDTO(userId)).
      setOperation(RemoteOperation.GET_BALANCE);

    ValueDTO balanceDTO = ObjectConverter.convert(
      redirectToDatabase(dtoToSend)
    );
    return balanceDTO.getValue();
  }
}
