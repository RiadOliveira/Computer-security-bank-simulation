package components.attackerClient;

import java.util.List;
import java.util.Map;

import connections.SocketThread;
import connections.components.SocketComponent;
import connections.data.SocketData;

public abstract class BaseAttackerClient extends SocketThread {
  public BaseAttackerClient(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
  }
}
