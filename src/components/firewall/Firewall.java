package components.firewall;

import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;

public class Firewall extends BaseFirewall {
  public Firewall(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
  }

  @Override
  protected void execute() throws Exception {
  }

  @Override
  protected void handleExecutionException(Exception exception) {
  }
}
