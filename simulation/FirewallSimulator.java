package simulation;

import components.firewall.FirewallService;
import connections.components.SocketComponent;
import connections.components.SocketServer;
import connections.data.SocketConnectionData;
import utils.ConnectionUtils;

public class FirewallSimulator {
  public static void main(String[] args) {
    var serversToConnect = getServersToConnect();
    var firewallData = SimulationUtils.generateSocketServerData(
      SocketComponent.FIREWALL, SocketComponent.GATEWAY,
      serversToConnect
    );
    
    var firewallProcess = new SocketServer(FirewallService.class, firewallData);
    firewallProcess.run();
  }

  private static SocketConnectionData[] getServersToConnect() {
    int bankServiceInstances = SimulationUtils.BANK_SERVICE_INSTANCES_QUANTITY;
    SocketConnectionData[] serversToConnect = new SocketConnectionData[
      1 + bankServiceInstances
    ];
    serversToConnect[0] = ConnectionUtils.getComponentSocketConnectionData(
      SocketComponent.AUTHENTICATION_SERVICE
    );

    for(int ind=0 ; ind<bankServiceInstances ; ind++) {
      serversToConnect[ind+1] = new SocketConnectionData(
        SocketComponent.BANK_SERVICE,
        ConnectionUtils.getComponentAddress(SocketComponent.BANK_SERVICE),
        ConnectionUtils.getComponentServerPort(
          SocketComponent.BANK_SERVICE, ind
        )
      );
    }

    return serversToConnect;
  }
}
