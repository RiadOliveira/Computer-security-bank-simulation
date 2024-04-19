package simulation;

import connections.components.SocketComponent;
import connections.data.SocketConnectionData;
import connections.data.SocketServerData;
import utils.ConnectionUtils;

public class SimulationUtils {
  public static final int AUTH_SERVICE_INSTANCES_QUANTITY = 1;
  public static final int BANK_SERVICE_INSTANCES_QUANTITY = 3;

  public static SocketServerData generateSocketServerData(
    SocketComponent component, SocketComponent clientComponent,
    SocketConnectionData[] serversToConnect
  ) {
    return generateSocketServerData(
      component, 0, clientComponent, serversToConnect
    );
  }

  public static SocketServerData generateSocketServerData(
    SocketComponent component, int replicaIndex,
    SocketComponent clientComponent, SocketConnectionData[] serversToConnect
  ) {
    return new SocketServerData(
      ConnectionUtils.getComponentPort(component, replicaIndex),
      clientComponent, ConnectionUtils.getComponentAddress(clientComponent),
      serversToConnect
    );
  }

  public static SocketConnectionData generateSocketConnectionData(
    SocketComponent component, int replicaIndex
  ) {
    return new SocketConnectionData(
      component, ConnectionUtils.getComponentAddress(component),
      ConnectionUtils.getComponentPort(component, replicaIndex)
    );
  }
}
