package simulation;

import components.bankService.database.BankDatabase;
import connections.components.SocketComponent;
import connections.components.SocketServer;
import connections.data.SocketConnectionData;

public class BankDatabaseSimulator {
  public static void main(String[] args) {
    var serversToConnect = new SocketConnectionData[]{};
    var bankDatabaseData = SimulationUtils.generateSocketServerData(
      SocketComponent.BANK_DATABASE, SocketComponent.BANK_SERVICE,
      serversToConnect
    );
    
    var bankDatabaseProcess = new SocketServer(
      BankDatabase.class, bankDatabaseData
    );
    bankDatabaseProcess.run();
  }
}
