package simulation;

import components.authenticationService.AuthenticationService;
import connections.components.SocketComponent;
import connections.components.SocketServer;
import connections.data.SocketConnectionData;

public class AuthenticationServiceSimulator {
  public static void main(String[] args) {
    try {
      int replicaIndex = Integer.valueOf(args[0]);
      var authServiceData = SimulationUtils.generateSocketServerData(
        SocketComponent.AUTHENTICATION_SERVICE, replicaIndex,
        SocketComponent.GATEWAY, new SocketConnectionData[]{}
      );

      var authenticationServiceProcess = new SocketServer(
        AuthenticationService.class, authServiceData
      );
      authenticationServiceProcess.run();
    } catch (Exception exception) {
      System.out.println(
        "Forneça um index de réplica válido (entre 0 e " +
        (SimulationUtils.AUTH_SERVICE_INSTANCES_QUANTITY - 1) +
        ")!"
      );
    }
  }
}
