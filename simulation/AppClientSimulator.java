package simulation;

import components.appClient.AppClient;
import connections.components.SocketClient;
import connections.components.SocketComponent;
import utils.ConnectionUtils;

public class AppClientSimulator {
  public static void main(String[] args) {
    var firewallConnectionData = ConnectionUtils.getComponentSocketConnectionData(
      SocketComponent.FIREWALL
    );

    var clientProcess = new SocketClient(AppClient.class, firewallConnectionData);
    clientProcess.run();
  }
}
