package simulation;

import connections.components.SocketComponent;
import connections.data.SocketConnectionData;
import connections.data.SocketServerData;
import utils.ConnectionUtils;

public class SimulationUtils {
  public static final int BANK_SERVICE_INSTANCES_QUANTITY = 3;

  public static SocketServerData generateSocketServerData(
    SocketComponent component, SocketComponent clientComponent,
    SocketConnectionData[] serversToConnect
  ) {
    return new SocketServerData(
      ConnectionUtils.getComponentServerPort(component),
      ConnectionUtils.getComponentClientPort(component),
      clientComponent, ConnectionUtils.getComponentAddress(clientComponent),
      serversToConnect
    );
  }
}
