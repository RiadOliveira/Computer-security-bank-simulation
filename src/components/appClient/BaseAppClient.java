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
      RemoteOperation.CREATE_ACCOUNT, this::createAccount
    );
    operationHandlers.put(
      RemoteOperation.AUTHENTICATE, this::authenticate
    );
    operationHandlers.put(
      RemoteOperation.GET_ACCOUNT_DATA, this::getAccountData
    );
    operationHandlers.put(
      RemoteOperation.WITHDRAW, this::withdraw
    );
    operationHandlers.put(
      RemoteOperation.DEPOSIT, this::deposit
    );
    operationHandlers.put(
      RemoteOperation.WIRE_TRANSFER, this::wireTransfer
    );
    operationHandlers.put(
      RemoteOperation.GET_BALANCE, this::getBalance
    );
    operationHandlers.put(
      RemoteOperation.GET_SAVINGS_PROJECTIONS, this::getSavingsProjections
    );
    operationHandlers.put(
      RemoteOperation.GET_FIXED_INCOME_PROJECTIONS, this::getFixedIncomeProjections
    );
    operationHandlers.put(
      RemoteOperation.UPDATE_FIXED_INCOME, this::updateFixedIncome
    );
    operationHandlers.put(
      RemoteOperation.LOGOUT, this::logout
    );
  }

  protected abstract DTO createAccount() throws Exception;
  protected abstract DTO authenticate() throws Exception;
  protected abstract DTO getAccountData() throws Exception;
  protected abstract DTO withdraw() throws Exception;
  protected abstract DTO deposit() throws Exception;
  protected abstract DTO wireTransfer() throws Exception;
  protected abstract DTO getBalance() throws Exception;
  protected abstract DTO getSavingsProjections() throws Exception;
  protected abstract DTO getFixedIncomeProjections() throws Exception;
  protected abstract DTO updateFixedIncome() throws Exception;
  protected abstract DTO logout() throws Exception;

  protected DTO parseDTOToSend(DTO dtoToSend) {
    if(token == null) return dtoToSend;
    
    return new AuthenticatedDTO(token, dtoToSend).setOperation(
      dtoToSend.getOperation()
    );
  }

  protected void handleAuthResponse(AuthResponse authResponse) {
    token = authResponse.getToken();
  }

  protected void handleLogoutResponse() {
    token = null;
  }
}
