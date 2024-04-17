package components.bankService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import dtos.auth.AuthenticatedDTO;
import dtos.bankOperation.IncomeProjectionDTO;
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
    DTO receivedDTO = receiveSecureDTO(SocketComponent.GATEWAY);
    RemoteOperation operation = receivedDTO.getOperation();

    DTO responseDTO = operationHandlers.get(operation).run(receivedDTO);
    sendSecureDTO(SocketComponent.GATEWAY, responseDTO);
  }

  @Override
  protected void handleExecutionException(Exception exception) {
    executeDefaultExceptionHandling(exception);
  }

  @Override
  protected DTO createAccount(DTO dto) throws Exception {
    AuthenticatedDTO authenticatedDTO = ObjectConverter.convert(dto);
    UUID userId = authenticatedDTO.getUserId();

    DTO newAccount = new AuthenticatedDTO(
      userId, new BankAccount(userId)
    ).setOperation(dto.getOperation());
    sendSecureDTO(SocketComponent.BANK_DATABASE, newAccount);

    return receiveSecureDTO(SocketComponent.BANK_DATABASE);
  }

  @Override
  protected DTO getAccountData(DTO dto) throws Exception {
    AuthenticatedDTO authenticatedDTO = ObjectConverter.convert(dto);
    sendSecureDTO(SocketComponent.BANK_DATABASE, authenticatedDTO);

    BankAccount receivedBankAccount = ObjectConverter.convert(
      receiveSecureDTO(SocketComponent.BANK_DATABASE)
    );
    return receivedBankAccount;
  }

  @Override
  protected DTO withdraw(DTO dto) throws Exception {
    throw new UnsupportedOperationException("Unimplemented method 'withdraw'");
  }

  @Override
  protected DTO deposit(DTO dto) throws Exception {
    throw new UnsupportedOperationException("Unimplemented method 'deposit'");
  }

  @Override
  protected DTO wireTransfer(DTO dto) throws Exception {
    throw new UnsupportedOperationException("Unimplemented method 'wireTransfer'");
  }

  @Override
  protected DTO getBalance(DTO dto) throws Exception {
    AuthenticatedDTO authenticatedDTO = ObjectConverter.convert(dto);
    sendSecureDTO(SocketComponent.BANK_DATABASE, authenticatedDTO);

    ValueDTO receivedBalance = ObjectConverter.convert(
      receiveSecureDTO(SocketComponent.BANK_DATABASE)
    );
    String formattedBalanceValue = ValueFormatter.formatToBrazilianCurrency(
      receivedBalance.getValue()
    );

    return new MessageDTO("Seu saldo atual é de " + formattedBalanceValue + ".");
  }

  @Override
  protected DTO getSavingsProjections(DTO dto) throws Exception {
    AuthenticatedDTO authenticatedDTO = ObjectConverter.convert(dto);
    sendSecureDTO(SocketComponent.BANK_DATABASE, authenticatedDTO);

    IncomeProjectionDTO receivedIncomeProjection = ObjectConverter.convert(
      receiveSecureDTO(SocketComponent.BANK_DATABASE)
    );
    return receivedIncomeProjection;
  }

  @Override
  protected DTO getFixedIncomeProjections(DTO dto) throws Exception {
    AuthenticatedDTO authenticatedDTO = ObjectConverter.convert(dto);
    sendSecureDTO(SocketComponent.BANK_DATABASE, authenticatedDTO);

    IncomeProjectionDTO receivedIncomeProjection = ObjectConverter.convert(
      receiveSecureDTO(SocketComponent.BANK_DATABASE)
    );
    return receivedIncomeProjection;
  }

  @Override
  protected DTO updateFixedIncome(DTO dto) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateFixedIncome'");
  }
}
