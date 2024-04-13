package components.authenticationService;

import java.util.List;
import java.util.Map;

import connections.SocketThread;
import connections.components.SocketComponent;
import connections.data.SocketData;

public abstract class BaseAuthenticationService extends SocketThread {
  public BaseAuthenticationService(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
  }
  
}
