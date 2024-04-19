package components.authenticationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import connections.SocketThread;
import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import dtos.user.UserData;
import interfaces.ThrowingConsumer;

public abstract class BaseAuthenticationService extends SocketThread {
  protected static final List<UserData> userDatabase = getInitialUserDatabase();

  protected final Map<
    RemoteOperation, ThrowingConsumer<DTO, DTO, Exception>
  > operationHandlers = new HashMap<>();

  public BaseAuthenticationService(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);

    operationHandlers.put(RemoteOperation.CREATE_ACCOUNT, this::createAccount);
    operationHandlers.put(RemoteOperation.AUTHENTICATION, this::authenticate);
  }

  protected abstract DTO createAccount(DTO user) throws Exception;
  protected abstract DTO authenticate(DTO authData) throws Exception;

  private static List<UserData> getInitialUserDatabase() {
    List<UserData> initialUserDatabase = new ArrayList<>();

    var firstUser = new UserData(
      UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
      "First person", "111.111.222-22", "Address of first person",
      "(11)9.1111-2222", "11112222"
    );
    initialUserDatabase.add(firstUser);

    var secondUser = new UserData(
      UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
      "Second person", "123.456.789-00", "Address of second person",
      "(12)9.3456-7890", "12345678"
    );
    initialUserDatabase.add(secondUser);

    var thirdUser = new UserData(
      UUID.fromString("71c6e098-c2b2-4b95-b9d3-f00f242f465f"),
      "Third person", "987.654.321-00", "Address of third person",
      "(98)9.7654-3210", "87654321"
    );
    initialUserDatabase.add(thirdUser);

    return initialUserDatabase;
  }
}
