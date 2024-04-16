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

  protected final Map<RemoteOperation, ThrowingConsumer<DTO, DTO, Exception>> operationHandlers = new HashMap<>();

  public BaseAuthenticationService(
      Map<SocketComponent, List<SocketData>> connectedSockets,
      SocketComponent socketClientComponent) {
    super(connectedSockets, socketClientComponent);

    operationHandlers.put(
        RemoteOperation.CREATE_ACCOUNT, this::createAccount);
    operationHandlers.put(
        RemoteOperation.AUTHENTICATE, this::authenticate);
  }

  protected abstract DTO createAccount(DTO user) throws Exception;

  protected abstract DTO authenticate(DTO authData) throws Exception;

  private List<UserData> getInitialUserDatabase() {
    List<UserData> initialUserDatabase = new ArrayList<>();

    var user1 = new UserData("First person", "111.111.222-22",
        "Address of first person",
        "(11)9.1111-2222", "11112222");
    user1.generateAndSetId();

    var user2 = new UserData("Second person", "123.456.789-00",
        "Address of second person",
        "(12)9.3456-7890", "12345678");
    user2.generateAndSetId();

    var user3 = new UserData("Third person", "987.654.321-00",
        "Address of third person",
        "(98)9.7654-3210", "87654321");
    user3.generateAndSetId();

    initialUserDatabase.add(user1);
    initialUserDatabase.add(user2);
    initialUserDatabase.add(user3);

    return initialUserDatabase;
  }
}
