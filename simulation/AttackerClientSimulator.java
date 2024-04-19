package simulation;

import components.attackerClient.AttackerClient;
import connections.components.SocketClient;
import connections.components.SocketComponent;
import utils.ConnectionUtils;

public class AttackerClientSimulator {
  public static void main(String[] args) {
    var bankDatabaseConnectionData = ConnectionUtils.getComponentSocketConnectionData(
      SocketComponent.BANK_DATABASE
    );

    var attackerClientProcess = new SocketClient(
      AttackerClient.class, bankDatabaseConnectionData
    );
    attackerClientProcess.run();
  }
}
