package components.bankService.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.user.BankAccount;

public class BankDatabase extends BaseBankDatabase {
  private List<BankAccount> accountsDatabase = new ArrayList<>();

  public BankDatabase(
      Map<SocketComponent, List<SocketData>> connectedSockets,
      SocketComponent socketClientComponent) {
    super(connectedSockets, socketClientComponent);
  }

  @Override
  protected void execute() throws Exception {
  }

  @Override
  protected void handleExecutionException(Exception exception) {
  }
}
