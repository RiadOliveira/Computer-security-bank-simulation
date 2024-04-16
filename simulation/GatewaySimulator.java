package simulation;

import components.gateway.Gateway;
import connections.components.SocketComponent;
import connections.components.SocketServer;
import connections.data.SocketConnectionData;
import utils.ConnectionUtils;

public class GatewaySimulator {
  public static void main(String[] args) {
    SocketConnectionData[] serversToConnect = getServersToConnect();
    var gatewayData = SimulationUtils.generateSocketServerData(
        SocketComponent.GATEWAY, SocketComponent.FIREWALL,
        serversToConnect);

    var gatewayProcess = new SocketServer(Gateway.class, gatewayData);
    gatewayProcess.run();
  }

  private static SocketConnectionData[] getServersToConnect() {
    // int bankServiceInstances = SimulationUtils.BANK_SERVICE_INSTANCES_QUANTITY;
    // SocketConnectionData[] serversToConnect = new SocketConnectionData[1 +
    // bankServiceInstances];

    SocketConnectionData[] serversToConnect = {ConnectionUtils.getComponentSocketConnectionData(SocketComponent.AUTHENTICATION_SERVICE)};

    // for (int ind = 0; ind < bankServiceInstances; ind++) {
    // serversToConnect[ind + 1] = SimulationUtils.generateSocketConnectionData(
    // SocketComponent.BANK_SERVICE, ind);
    // }

    return serversToConnect;
  }
}
