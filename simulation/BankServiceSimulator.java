package simulation;

import components.bankService.BankService;
import connections.components.SocketComponent;
import connections.components.SocketServer;
import connections.data.SocketConnectionData;
import utils.ConnectionUtils;

public class BankServiceSimulator {
  public static void main(String[] args) {
    var bankDatabaseConnectionData = ConnectionUtils.getComponentSocketConnectionData(
      SocketComponent.BANK_DATABASE
    );
    var bankServiceData = SimulationUtils.generateSocketServerData(
      SocketComponent.BANK_SERVICE, SocketComponent.FIREWALL,
      new SocketConnectionData[]{bankDatabaseConnectionData}
    );
    
    var bankServiceProcess = new SocketServer(BankService.class, bankServiceData);
    bankServiceProcess.run();
  }
}
