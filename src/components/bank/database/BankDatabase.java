package components.bank.database;

import java.util.List;
import java.util.Map;

import socket.components.SocketComponent;
import socket.data.SocketData;

public class BankDatabase extends BaseBankDatabase {
  public BankDatabase(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
  }

  @Override
  protected void execute() throws Exception {
  }

  @Override
  protected void handleExecutionException(Exception exception) {
  }
}
