package components.bankService;

import java.util.List;
import java.util.Map;
import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import dtos.auth.AuthenticatedDTO;
import dtos.generic.MessageDTO;
import dtos.generic.ValueDTO;
import dtos.operation.IncomeProjectionDTO;
import dtos.user.BankAccount;
import errors.AppException;
import utils.ObjectConverter;
import utils.ValueFormatter;

public class BankService extends BaseBankService {
  public BankService(
      Map<SocketComponent, List<SocketData>> connectedSockets,
      SocketComponent socketClientComponent) {
    super(connectedSockets, socketClientComponent);
  }

  @Override
  protected void execute() throws Exception {
    DTO receivedDTO = receiveSecureDTO(SocketComponent.BANK_DATABASE);
    RemoteOperation operation = receivedDTO.getOperation();

    DTO responseDTO = operationHandlers.get(operation).run(
        receivedDTO);
    sendSecureDTO(SocketComponent.BANK_DATABASE, responseDTO);
  }

  @Override
  protected void handleExecutionException(Exception exception) {
    executeDefaultExceptionHandling(exception);
  }

  @Override
  protected DTO createAccount(DTO dto) throws Exception {
    AuthenticatedDTO authenticatedDTO = ObjectConverter.convert(dto);

    BankAccount newAccount = new BankAccount(authenticatedDTO.getUserId());
    newAccount.setOperation(dto.getOperation());

    sendSecureDTO(SocketComponent.BANK_DATABASE, newAccount);
    DTO receivedDTO = receiveSecureDTO(SocketComponent.BANK_DATABASE);

    BankAccount parsedReceivedDTO = ObjectConverter.convert(receivedDTO);

    return new MessageDTO("Agência: " + parsedReceivedDTO.getAgency() + " Número: "
        + parsedReceivedDTO.getAccountNumber() + " cadastrada com sucesso.");
  }

  @Override
  protected DTO getAccountData(DTO dto) throws Exception {
    AuthenticatedDTO authenticatedDTO = ObjectConverter.convert(dto);
    sendSecureDTO(SocketComponent.BANK_DATABASE, authenticatedDTO);

    BankAccount receivedBankAccount = ObjectConverter.convert(receiveSecureDTO(SocketComponent.BANK_DATABASE));
    return receivedBankAccount;
  }

  @Override
  protected DTO withdraw(DTO dto) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'withdraw'");
  }

  @Override
  protected DTO deposit(DTO dto) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deposit'");
  }

  @Override
  protected DTO wireTransfer(DTO dto) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'wireTransfer'");
  }

  @Override
  protected DTO getBalance(DTO dto) throws Exception {
    AuthenticatedDTO authenticatedDTO = ObjectConverter.convert(dto);
    sendSecureDTO(SocketComponent.BANK_DATABASE, authenticatedDTO);

    ValueDTO receivedBalance = ObjectConverter.convert(receiveSecureDTO(SocketComponent.BANK_DATABASE));
    String formattedBalanceValue = ValueFormatter.formatToBrazilianCurrency(receivedBalance.getValue());

    return new MessageDTO("Seu saldo atual é de " + formattedBalanceValue + ".");
  }

  @Override
  protected DTO getSavingsProjections(DTO dto) throws Exception {
    AuthenticatedDTO authenticatedDTO = ObjectConverter.convert(dto);
    sendSecureDTO(SocketComponent.BANK_DATABASE, authenticatedDTO);

    IncomeProjectionDTO receivedIncomeProjection = ObjectConverter.convert(receiveSecureDTO(SocketComponent.BANK_DATABASE));
    return receivedIncomeProjection;
  }

  @Override
  protected DTO getFixedIncomeProjections(DTO dto) throws Exception {
    AuthenticatedDTO authenticatedDTO = ObjectConverter.convert(dto);
    sendSecureDTO(SocketComponent.BANK_DATABASE, authenticatedDTO);

    IncomeProjectionDTO receivedIncomeProjection = ObjectConverter.convert(receiveSecureDTO(SocketComponent.BANK_DATABASE));
    return receivedIncomeProjection;
  }

  @Override
  protected DTO updateFixedIncome(DTO dto) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateFixedIncome'");
  }

}
