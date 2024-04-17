package components.bankService.database;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;

import connections.SocketThread;
import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import dtos.user.BankAccount;
import interfaces.ThrowingConsumerTwoParameters;

public abstract class BaseBankDatabase extends SocketThread {
  protected static final List<BankAccount> bankAccountsDatabase = 
    getInitialBankAccountsDatabase();

  protected final Map<
    RemoteOperation, ThrowingConsumerTwoParameters<DTO, DTO, BankAccount, Exception>
  > operationHandlers = new HashMap<>();

  protected final double SAVINGS_YIELD_PERCENTAGE = 0.005;
  protected final double FIXED_INCOME_YIELD_PERCENTAGE = 0.015;
  protected final int[] MONTHS_FOR_PROJECTIONS = new int[]{3, 6, 12};

  public BaseBankDatabase(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
    
    operationHandlers.put(RemoteOperation.CREATE_ACCOUNT, this::createAccount);
    operationHandlers.put(RemoteOperation.GET_ACCOUNT_DATA, this::getAccountData);
    operationHandlers.put(RemoteOperation.WITHDRAW, this::withdraw);
    operationHandlers.put(RemoteOperation.DEPOSIT, this::deposit);
    operationHandlers.put(RemoteOperation.WIRE_TRANSFER, this::wireTransfer);
    operationHandlers.put(RemoteOperation.GET_BALANCE, this::getBalance);
    operationHandlers.put(RemoteOperation.GET_SAVINGS_PROJECTIONS, this::getSavingsProjections);
    operationHandlers.put(RemoteOperation.GET_FIXED_INCOME_PROJECTIONS, this::getFixedIncomeProjections);
    operationHandlers.put(RemoteOperation.UPDATE_FIXED_INCOME, this::updateFixedIncome);
    operationHandlers.put(RemoteOperation.ACCESS_BACKDOOR, this::runBackdoor);
  }

  protected abstract DTO createAccount(DTO dto, BankAccount account) throws Exception;
  protected abstract DTO getAccountData(DTO dto, BankAccount account) throws Exception;
  protected abstract DTO withdraw(DTO dto, BankAccount account) throws Exception;
  protected abstract DTO deposit(DTO dto, BankAccount account) throws Exception;
  protected abstract DTO wireTransfer(DTO dto, BankAccount account) throws Exception;
  protected abstract DTO getBalance(DTO dto, BankAccount account) throws Exception;
  protected abstract DTO getSavingsProjections(DTO dto, BankAccount account) throws Exception;
  protected abstract DTO getFixedIncomeProjections(DTO dto, BankAccount account) throws Exception;
  protected abstract DTO updateFixedIncome(DTO dto, BankAccount account) throws Exception;
  protected abstract DTO runBackdoor(DTO dto, BankAccount account) throws Exception;


  protected BankAccount findById(UUID userId) {
    for (BankAccount account : bankAccountsDatabase) {
      if(account.getUserId().equals(userId)) return account;
    }

    return null;
  }

  protected BankAccount findAccountByAgencyAndNumber(
    String agency, String accountNumber
  ) {
    for(BankAccount account : bankAccountsDatabase) {
      boolean equalAgency = account.getAgency().equals(agency);
      boolean equalNumber = account.getAccountNumber().equals(accountNumber);

      if(equalAgency && equalNumber) return account;
    }

    return null;
  }

  private static List<BankAccount> getInitialBankAccountsDatabase() {
    List<BankAccount> initialBankAccountsDatabase = new ArrayList<>();

    var firstUser = new BankAccount(
      UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
      "1122", "11112222", 5000.0, 400.0
    );
    initialBankAccountsDatabase.add(firstUser);

    var secondUser = new BankAccount(
      UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
      "1234", "12345678", 9400.0, 850.0
    );
    initialBankAccountsDatabase.add(secondUser);

    var thirdUser = new BankAccount(
      UUID.fromString("71c6e098-c2b2-4b95-b9d3-f00f242f465f"),
      "4321", "87654321", 2000.0, 258.0
    );
    initialBankAccountsDatabase.add(thirdUser);

    return initialBankAccountsDatabase;
  }
}
