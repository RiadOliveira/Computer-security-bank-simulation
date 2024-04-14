package components.appClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import connections.SocketThread;
import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.RemoteOperation;
import dtos.DTO;
import dtos.auth.AuthResponse;
import dtos.auth.AuthenticatedDTO;
import interfaces.ThrowingRunnable;

public abstract class BaseAppClient extends SocketThread {
  protected static final Scanner scanner = new Scanner(System.in);
  protected final Map<
    RemoteOperation, ThrowingRunnable<DTO, Exception>
  > operationHandlers = new HashMap<>();

  private String token = null;

  public BaseAppClient(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);

    operationHandlers.put(
      RemoteOperation.CREATE_ACCOUNT, this::handleCreateAccount
    );
    operationHandlers.put(
      RemoteOperation.AUTHENTICATE, this::handleAuthenticate
    );
    operationHandlers.put(
      RemoteOperation.GET_ACCOUNT_DATA, this::handleGetAccountData
    );
    operationHandlers.put(
      RemoteOperation.WITHDRAW, this::handleWithdraw
    );
    operationHandlers.put(
      RemoteOperation.DEPOSIT, this::handleDeposit
    );
    operationHandlers.put(
      RemoteOperation.WIRE_TRANSFER, this::handleWireTransfer
    );
    operationHandlers.put(
      RemoteOperation.GET_BALANCE, this::handleGetBalance
    );
    operationHandlers.put(
      RemoteOperation.GET_SAVINGS_PROJECTIONS, this::handleGetSavingsProjections
    );
    operationHandlers.put(
      RemoteOperation.GET_FIXED_INCOME_PROJECTIONS, this::handleGetFixedIncomeProjections
    );
    operationHandlers.put(
      RemoteOperation.UPDATE_FIXED_INCOME, this::handleUpdateFixedIncome
    );
    operationHandlers.put(
      RemoteOperation.LOGOUT, this::handleLogout
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
  protected abstract DTO handleLogout() throws Exception;

  protected DTO parseDTOToSend(DTO dtoToSend) {
    if(token == null) return dtoToSend;
    return new AuthenticatedDTO(token, dtoToSend);
  }

  protected void handleAuthResponse(AuthResponse authResponse) {
    token = authResponse.getToken();
  }

  protected void handleLogoutResponse() {
    token = null;
  }
}
