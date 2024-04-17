package components.bankService.database;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import connections.SocketThread;
import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import interfaces.ThrowingConsumerTwoParameters;

import java.util.UUID;

public abstract class BaseBankDatabase extends SocketThread {
  protected final Map<RemoteOperation, ThrowingConsumerTwoParameters<DTO, DTO, UUID, Exception>> operationHandlers = new HashMap<>();

  protected final double SAVINGS_YIELD_PERCENTAGE = 0.005;
  protected final double FIXED_INCOME_YIELD_PERCENTAGE = 0.015;
  protected final int[] MONTHS_FOR_PROJECTIONS = new int[] { 3, 6, 12 };

  public BaseBankDatabase(
      Map<SocketComponent, List<SocketData>> connectedSockets,
      SocketComponent socketClientComponent) {
    super(connectedSockets, socketClientComponent);
    operationHandlers.put(
        RemoteOperation.CREATE_ACCOUNT, this::createAccount);
    operationHandlers.put(
        RemoteOperation.GET_ACCOUNT_DATA, this::getAccountData);
    operationHandlers.put(
        RemoteOperation.WITHDRAW, this::withdraw);
    operationHandlers.put(
        RemoteOperation.DEPOSIT, this::deposit);
    operationHandlers.put(
        RemoteOperation.WIRE_TRANSFER, this::wireTransfer);
    operationHandlers.put(
        RemoteOperation.GET_BALANCE, this::getBalance);
    operationHandlers.put(
        RemoteOperation.GET_SAVINGS_PROJECTIONS, this::getSavingsProjections);
    operationHandlers.put(
        RemoteOperation.GET_FIXED_INCOME_PROJECTIONS, this::getFixedIncomeProjections);
    operationHandlers.put(
        RemoteOperation.UPDATE_FIXED_INCOME, this::updateFixedIncome);
  }

  protected abstract DTO createAccount(DTO dto, UUID userId) throws Exception;

  protected abstract DTO getAccountData(DTO dto, UUID userId) throws Exception;

  protected abstract DTO withdraw(DTO dto, UUID userId) throws Exception;

  protected abstract DTO deposit(DTO dto, UUID userId) throws Exception;

  protected abstract DTO wireTransfer(DTO dto, UUID userId) throws Exception;

  protected abstract DTO getBalance(DTO dto, UUID userId) throws Exception;

  protected abstract DTO getSavingsProjections(DTO dto, UUID userId) throws Exception;

  protected abstract DTO getFixedIncomeProjections(DTO dto, UUID userId)
      throws Exception;

  protected abstract DTO updateFixedIncome(DTO dto, UUID userId) throws Exception;

}
