package simulation;

import components.appClient.AppClient;
import connections.components.SocketClient;
import connections.components.SocketComponent;
import connections.data.SocketClientData;
import utils.ConnectionUtils;

public class ClientSimulator {
  public static void main(String[] args) {
    var gatewayConnectionData = ConnectionUtils.getComponentSocketConnectionData(
      SocketComponent.GATEWAY
    );
    SocketClientData clientData = new SocketClientData(
      ConnectionUtils.getComponentClientPort(SocketComponent.CLIENT),
      gatewayConnectionData
    );

    var clientProcess = new SocketClient(AppClient.class, clientData);
    clientProcess.run();
  }
}
