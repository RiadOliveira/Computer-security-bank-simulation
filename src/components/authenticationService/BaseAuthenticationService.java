package components.authenticationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connections.SocketThread;
import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import dtos.user.UserData;
import interfaces.ThrowingConsumer;
import utils.Hasher;

public abstract class BaseAuthenticationService extends SocketThread {
  protected final List<UserData> userDatabase = getInitialUserDatabase();

  protected final Map<
    RemoteOperation, ThrowingConsumer<DTO, DTO, Exception>
  > operationHandlers = new HashMap<>();

  public BaseAuthenticationService(
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
  }

  protected abstract DTO createAccount(DTO user) throws Exception;
  protected abstract DTO authenticate(DTO authData) throws Exception;

  private List<UserData> getInitialUserDatabase() {
    List<UserData> initialUserDatabase = new ArrayList<>();

    initialUserDatabase.add(new UserData("Lucas Paulino", "123.456.789-10", "Rua dos fulanos, 123", "84 99123-4567",
        Hasher.hashAndEncode("lucas123")));

    return initialUserDatabase;
  }
}
