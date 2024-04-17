package components.bankService.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import dtos.auth.AuthenticatedDTO;
import dtos.generic.ValueDTO;
import dtos.operation.IncomeProjectionDTO;
import dtos.user.BankAccount;
import errors.AppException;
import utils.ObjectConverter;

public class BankDatabase extends BaseBankDatabase {
  private List<BankAccount> accountsDatabase = new ArrayList<>();

  public BankDatabase(
      Map<SocketComponent, List<SocketData>> connectedSockets,
      SocketComponent socketClientComponent) {
    super(connectedSockets, socketClientComponent);
  }

  @Override
  protected void execute() throws Exception {
    DTO receivedDTO = receiveSecureDTO(SocketComponent.BANK_SERVICE);
    AuthenticatedDTO authenticatedDTO = ObjectConverter.convert(receivedDTO);

    RemoteOperation operation = authenticatedDTO.getOperation();

    DTO responseDTO = operationHandlers.get(operation).run(
        authenticatedDTO.getDTO(), authenticatedDTO.getUserId());
    sendSecureDTO(SocketComponent.BANK_SERVICE, responseDTO);
  }

  @Override
  protected void handleExecutionException(Exception exception) {
    executeDefaultExceptionHandling(exception);
  }

  @Override
  protected DTO createAccount(DTO dto, UUID userId) throws Exception {
    BankAccount newAccount = ObjectConverter.convert(dto);
    accountsDatabase.add(newAccount);

    return newAccount;
  }

  @Override
  protected DTO getAccountData(DTO dto, UUID userId) throws Exception {
    BankAccount foundedAccount = findById(userId.toString());

    if (foundedAccount == null)
      throw new AppException("Conta não encontrada!");

    return foundedAccount;
  }

  @Override
  protected DTO withdraw(DTO dto, UUID userId) throws Exception {
    return null;
  }

  @Override
  protected DTO deposit(DTO dto, UUID userId) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deposit'");
  }

  @Override
  protected DTO wireTransfer(DTO dto, UUID userId) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'wireTransfer'");
  }

  @Override
  protected DTO getBalance(DTO dto, UUID userId) throws Exception {
    BankAccount foundedAccount = findById(userId.toString());

    if (foundedAccount == null)
      throw new AppException("Conta não encontrada!");

    return new ValueDTO(foundedAccount.getBalance());
  }

  @Override
  protected DTO getSavingsProjections(DTO dto, UUID userId) throws Exception {
    BankAccount foundedAccount = findById(userId.toString());

    if (foundedAccount == null)
      throw new AppException("Conta não encontrada!");

    IncomeProjectionDTO incomeProjection = new IncomeProjectionDTO(foundedAccount.getBalance(),
        this.SAVINGS_YIELD_PERCENTAGE, this.MONTHS_FOR_PROJECTIONS);

    return incomeProjection;
  }

  @Override
  protected DTO getFixedIncomeProjections(DTO dto, UUID userId) throws Exception {
    BankAccount foundedAccount = findById(userId.toString());

    if (foundedAccount == null)
      throw new AppException("Conta não encontrada!");

    IncomeProjectionDTO incomeProjection = new IncomeProjectionDTO(foundedAccount.getFixedIncome(),
        this.FIXED_INCOME_YIELD_PERCENTAGE, this.MONTHS_FOR_PROJECTIONS);

    return incomeProjection;
  }

  @Override
  protected DTO updateFixedIncome(DTO dto, UUID userId) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateFixedIncome'");
  }

  private BankAccount findById(String userId) {
    for (BankAccount account : accountsDatabase) {
      if (userId.equals(account.getUserId().toString()))
        return account;
    }

    return null;
  }
}
