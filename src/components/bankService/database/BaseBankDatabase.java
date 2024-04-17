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
  protected List<BankAccount> accountsDatabase = new ArrayList<>();

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

  protected BankAccount findById(UUID userId) {
    for (BankAccount account : accountsDatabase) {
      if(account.getUserId().equals(userId)) return account;
    }

    return null;
  }

  public BankAccount findAccountByAgencyAndNumber(
    String agency, String accountNumber
  ) {
    for(BankAccount account : accountsDatabase) {
      boolean equalAgency = account.getAgency().equals(agency);
      boolean equalNumber = account.getAccountNumber().equals(accountNumber);

      if(equalAgency && equalNumber) return account;
    }

    return null;
  }
}
