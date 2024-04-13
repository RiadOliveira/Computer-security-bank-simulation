package components.firewall;

import java.util.List;
import java.util.Map;

import connections.SocketThread;
import connections.components.SocketComponent;
import connections.data.SocketData;

public abstract class BaseFirewallService extends SocketThread {
  public BaseFirewallService(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
  }
}
