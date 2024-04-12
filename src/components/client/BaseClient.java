package components.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import dtos.AppCommand;
import dtos.DTO;
import interfaces.ThrowingRunnable;
import socket.SocketThread;
import socket.components.SocketComponent;
import socket.data.SocketData;

public abstract class BaseClient extends SocketThread {
  protected static final Scanner scanner = new Scanner(System.in);
  protected final Map<
    AppCommand, ThrowingRunnable<DTO, Exception>
  > commandHandlers = new HashMap<>();

  public BaseClient(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);

    commandHandlers.put(
      AppCommand.CREATE_ACCOUNT, this::handleCreateAccount
    );
    commandHandlers.put(
      AppCommand.AUTHENTICATE, this::handleAuthenticate
    );
    commandHandlers.put(
      AppCommand.GET_ACCOUNT_DATA, this::handleGetAccountData
    );
    commandHandlers.put(
      AppCommand.WITHDRAW, this::handleWithdraw
    );
    commandHandlers.put(
      AppCommand.DEPOSIT, this::handleDeposit
    );
    commandHandlers.put(
      AppCommand.WIRE_TRANSFER, this::handleWireTransfer
    );
    commandHandlers.put(
      AppCommand.GET_BALANCE, this::handleGetBalance
    );
    commandHandlers.put(
      AppCommand.GET_SAVINGS_PROJECTIONS, this::handleGetSavingsProjections
    );
    commandHandlers.put(
      AppCommand.GET_FIXED_INCOME_PROJECTIONS, this::handleGetFixedIncomeProjections
    );
    commandHandlers.put(
      AppCommand.UPDATE_FIXED_INCOME, this::handleUpdateFixedIncome
    );
  }

  protected abstract DTO handleCreateAccount() throws Exception;
  protected abstract DTO handleAuthenticate() throws Exception;
  protected abstract DTO handleGetAccountData() throws Exception;
  protected abstract DTO handleWithdraw() throws Exception;
  protected abstract DTO handleDeposit() throws Exception;
  protected abstract DTO handleWireTransfer() throws Exception;
  protected abstract DTO handleGetBalance() throws Exception;
  protected abstract DTO handleGetSavingsProjections() throws Exception;
  protected abstract DTO handleGetFixedIncomeProjections() throws Exception;
  protected abstract DTO handleUpdateFixedIncome() throws Exception;
}
