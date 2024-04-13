package simulation;

import components.gateway.Gateway;
import connections.components.SocketComponent;
import connections.components.SocketServer;
import connections.data.SocketConnectionData;
import utils.ConnectionUtils;

public class GatewaySimulator {
  public static void main(String[] args) {
    var firewallConnectionData = ConnectionUtils.getComponentSocketConnectionData(
      SocketComponent.FIREWALL
    );
    var gatewayData = SimulationUtils.generateSocketServerData(
      SocketComponent.GATEWAY, SocketComponent.CLIENT,
      new SocketConnectionData[]{firewallConnectionData}
    );
    
    var gatewayProcess = new SocketServer(Gateway.class, gatewayData);
    gatewayProcess.run();
  }
}
