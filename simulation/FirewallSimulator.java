package simulation;

import components.firewall.Firewall;
import connections.components.SocketComponent;
import connections.components.SocketServer;
import connections.data.SocketConnectionData;
import utils.ConnectionUtils;

public class FirewallSimulator {
  public static void main(String[] args) {
    var gatewayConnectionData = ConnectionUtils.getComponentSocketConnectionData(
      SocketComponent.GATEWAY
    );
    var firewallData = SimulationUtils.generateSocketServerData(
      SocketComponent.FIREWALL, SocketComponent.CLIENT,
      new SocketConnectionData[]{gatewayConnectionData}
    );
    
    var firewallProcess = new SocketServer(Firewall.class, firewallData);
    firewallProcess.run();
  }
}
