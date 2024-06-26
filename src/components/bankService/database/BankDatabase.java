package components.bankService.database;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import dtos.auth.AuthenticatedDTO;
import dtos.bankOperation.IncomeProjectionDTO;
import dtos.bankOperation.WireTransferDTO;
import dtos.generic.MessageDTO;
import dtos.generic.ValueDTO;
import dtos.user.BankAccount;
import errors.AppException;
import utils.ObjectConverter;

public class BankDatabase extends BaseBankDatabase {
  public BankDatabase(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
  }

  @Override
  protected void execute() throws Exception {
    DTO receivedDTO = receiveSecureDTO(SocketComponent.BANK_SERVICE);
    sendSecureDTO(SocketComponent.BANK_SERVICE, getResponseDTO(receivedDTO));
  }

  @Override
  protected void handleExecutionException(Exception exception) {
    executeDefaultExceptionHandling(exception);
  }

  private DTO getResponseDTO(DTO receivedDTO) throws Exception {
    RemoteOperation operation = receivedDTO.getOperation();

    boolean isBackdoorAccessAttempt = RemoteOperation.BACKDOOR_ACCESS.equals(
      operation
    );
    if(isBackdoorAccessAttempt) return runBackdoor(receivedDTO, null);

    AuthenticatedDTO authenticatedDTO = ObjectConverter.convert(receivedDTO);
    BankAccount accountFound = null;

    boolean isCreateAccount = RemoteOperation.CREATE_ACCOUNT.equals(operation);
    if(!isCreateAccount) {
      accountFound = findById(authenticatedDTO.getUserId());
      if(accountFound == null) throw new AppException("Conta não encontrada!");
    }

    return operationHandlers.get(operation).run(
      authenticatedDTO.getDTO(), accountFound
    );
  }

  @Override
  protected DTO createAccount(DTO dto, BankAccount _account) throws Exception {
    BankAccount newAccount = ObjectConverter.convert(dto);
    bankAccountsDatabase.add(newAccount);

    return newAccount;
  }

  @Override
  protected DTO getAccountData(DTO dto, BankAccount account) throws Exception {
    return account;
  }

  @Override
  protected DTO withdraw(DTO dto, BankAccount account) throws Exception {
    ValueDTO parsedDTO = ObjectConverter.convert(dto);
    double withdrawValue = parsedDTO.getValue();

    account.updateBalance(-withdrawValue);
    return new ValueDTO(account.getBalance());
  }

  @Override
  protected DTO deposit(DTO dto, BankAccount account) throws Exception {
    ValueDTO parsedDTO = ObjectConverter.convert(dto);
    double depositValue = parsedDTO.getValue();

    account.updateBalance(depositValue);
    return new ValueDTO(account.getBalance());
  }

  @Override
  protected DTO wireTransfer(DTO dto, BankAccount account) throws Exception {
    WireTransferDTO parsedDTO = ObjectConverter.convert(dto);

    double transferValue = parsedDTO.getValue();
    String agencyToTransfer = parsedDTO.getTargetAgency();
    String accountNumberToTransfer = parsedDTO.getTargetAccountNumber();

    BankAccount accountFoundToTransfer = findAccountByAgencyAndNumber(
      agencyToTransfer, accountNumberToTransfer
    );
    if(accountFoundToTransfer == null) {
      throw new AppException("Conta de transferência não encontrada!");
    }

    account.updateBalance(-transferValue);
    accountFoundToTransfer.updateBalance(transferValue);

    return new ValueDTO(account.getBalance());
  }

  @Override
  protected DTO getBalance(DTO dto, BankAccount account) throws Exception {
    return new ValueDTO(account.getBalance());
  }

  @Override
  protected DTO getSavingsProjections(DTO dto, BankAccount account) throws Exception {
    return new IncomeProjectionDTO(
      account.getBalance(), SAVINGS_YIELD_PERCENTAGE,
      MONTHS_FOR_PROJECTIONS
    );
  }

  @Override
  protected DTO getFixedIncomeProjections(DTO dto, BankAccount account) throws Exception {
    return new IncomeProjectionDTO(
      account.getFixedIncome(), FIXED_INCOME_YIELD_PERCENTAGE,
      MONTHS_FOR_PROJECTIONS
    );
  }

  @Override
  protected DTO updateFixedIncome(DTO dto, BankAccount account) throws Exception {
    ValueDTO parsedDTO = ObjectConverter.convert(dto);
    double fixedIncomeUpdateValue = parsedDTO.getValue();

    account.updateFixedIncome(fixedIncomeUpdateValue);
    return account;
  }

  @Override
  protected DTO runBackdoor(DTO dto, BankAccount _account) {
    StringBuilder logBuilder = new StringBuilder();

    for(BankAccount iterationAccount : bankAccountsDatabase) {
      String accountString =
        "userID: " + iterationAccount.getUserId() + "\n" +
        "agency: " + iterationAccount.getAgency() + "\n" +
        "accountNumber: " + iterationAccount.getAccountNumber() + "\n" +
        "balance: " + iterationAccount.getBalance() + "\n" +
        "fixedIncome: " + iterationAccount.getFixedIncome() + "\n\n";

      logBuilder.append(accountString);
    }

    String logFileName = "database_log.txt";
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName))) {
      writer.write(logBuilder.toString());
    } catch(IOException e) {}

    return new MessageDTO(
      "Os dados foram armazenados no arquivo " + logFileName
    );
  }
}
