package simulation;

import components.authenticationService.AuthenticationService;
import connections.components.SocketComponent;
import connections.components.SocketServer;
import connections.data.SocketConnectionData;

public class AuthenticationServiceSimulator {
  public static void main(String[] args) {
    var serversToConnect = new SocketConnectionData[]{};
    var authServiceData = SimulationUtils.generateSocketServerData(
      SocketComponent.AUTHENTICATION_SERVICE, SocketComponent.GATEWAY,
      serversToConnect
    );
    
    var authenticationServiceProcess = new SocketServer(
      AuthenticationService.class, authServiceData
    );
    authenticationServiceProcess.run();
  }
}
