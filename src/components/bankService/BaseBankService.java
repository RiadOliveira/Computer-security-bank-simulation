package components.bankService;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import connections.SocketThread;
import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import dtos.auth.AuthenticatedDTO;
import interfaces.ThrowingConsumer;

public abstract class BaseBankService extends SocketThread {
  protected final Map<
    RemoteOperation, ThrowingConsumer<DTO, AuthenticatedDTO, Exception>
  > operationHandlers = new HashMap<>();

  public BaseBankService(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);

    operationHandlers.put(RemoteOperation.CREATE_ACCOUNT, this::createAccount);
    operationHandlers.put(RemoteOperation.GET_ACCOUNT_DATA, this::redirectToDatabase);
    operationHandlers.put(RemoteOperation.WITHDRAW, this::withdraw);
    operationHandlers.put(RemoteOperation.DEPOSIT, this::deposit);
    operationHandlers.put(RemoteOperation.WIRE_TRANSFER, this::wireTransfer);
    operationHandlers.put(RemoteOperation.GET_BALANCE, this::getBalance);
    operationHandlers.put(RemoteOperation.GET_SAVINGS_PROJECTIONS, this::redirectToDatabase);
    operationHandlers.put(RemoteOperation.GET_FIXED_INCOME_PROJECTIONS, this::redirectToDatabase);
    operationHandlers.put(RemoteOperation.UPDATE_FIXED_INCOME, this::updateFixedIncome);
    operationHandlers.put(RemoteOperation.BACKDOOR_ACCESS, this::redirectToDatabase);
  }

  protected abstract DTO redirectToDatabase(DTO dto) throws Exception;
  protected abstract DTO createAccount(AuthenticatedDTO authenticatedDTO) throws Exception;
  protected abstract DTO withdraw(AuthenticatedDTO authenticatedDTO) throws Exception;
  protected abstract DTO deposit(AuthenticatedDTO authenticatedDTO) throws Exception;
  protected abstract DTO wireTransfer(AuthenticatedDTO authenticatedDTO) throws Exception;
  protected abstract DTO getBalance(AuthenticatedDTO authenticatedDTO) throws Exception;
  protected abstract DTO updateFixedIncome(AuthenticatedDTO authenticatedDTO) throws Exception;
}
