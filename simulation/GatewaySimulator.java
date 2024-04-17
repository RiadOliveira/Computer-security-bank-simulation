package simulation;

import components.gateway.Gateway;
import connections.components.SocketComponent;
import connections.components.SocketServer;
import connections.data.SocketConnectionData;
import utils.ConnectionUtils;

public class GatewaySimulator {
  public static void main(String[] args) {
    var serversToConnect = getServersToConnect();
    var gatewayData = SimulationUtils.generateSocketServerData(
      SocketComponent.GATEWAY, SocketComponent.FIREWALL,
      serversToConnect
    );
    
    var gatewayProcess = new SocketServer(Gateway.class, gatewayData);
    gatewayProcess.run();
  }

  private static SocketConnectionData[] getServersToConnect() {
    int authServiceInstances = SimulationUtils.AUTH_SERVICE_INSTANCES_QUANTITY;
    int bankServiceInstances = SimulationUtils.BANK_SERVICE_INSTANCES_QUANTITY;
    SocketConnectionData[] serversToConnect = new SocketConnectionData[
      authServiceInstances + bankServiceInstances
    ];

    for(int ind=0 ; ind<authServiceInstances ; ind++) {
      serversToConnect[ind] = ConnectionUtils.getComponentSocketConnectionData(
        SocketComponent.AUTHENTICATION_SERVICE
      );
    }
    
    for(int ind=0 ; ind<bankServiceInstances ; ind++) {
      int serverInd = ind+authServiceInstances;
      serversToConnect[serverInd] = SimulationUtils.generateSocketConnectionData(
        SocketComponent.BANK_SERVICE, ind
      );
    }

    return serversToConnect;
  }
}