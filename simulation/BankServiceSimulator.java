package simulation;

import components.bankService.BankService;
import components.bankService.database.BankDatabase;
import connections.components.SocketComponent;
import connections.components.SocketServer;
import connections.data.SocketConnectionData;

public class BankServiceSimulator {
  public static void main(String[] args) {
    try {
      int replicaIndex = Integer.valueOf(args[0]);
      var bankDatabaseConnectionData = SimulationUtils.generateSocketConnectionData(
        SocketComponent.BANK_DATABASE, replicaIndex
      );

      var bankDatabaseData = SimulationUtils.generateSocketServerData(
        SocketComponent.BANK_DATABASE, replicaIndex,
        SocketComponent.BANK_SERVICE, new SocketConnectionData[] {}
      );
      var bankServiceData = SimulationUtils.generateSocketServerData(
        SocketComponent.BANK_SERVICE, replicaIndex, SocketComponent.GATEWAY,
        new SocketConnectionData[]{bankDatabaseConnectionData}
      );

      var bankDatabaseProcessThread = new Thread(
        new SocketServer(BankDatabase.class, bankDatabaseData)
      );
      var bankServiceProcessThread = new Thread(
        new SocketServer(BankService.class, bankServiceData)
      );

      bankDatabaseProcessThread.start();
      bankServiceProcessThread.start();
    } catch(Exception exception) {
      System.out.println(
        "Forneça um index de réplica válido (entre 0 e " +
        (SimulationUtils.BANK_SERVICE_INSTANCES_QUANTITY - 1) +
        ")!"
      );
    }
  }
}
