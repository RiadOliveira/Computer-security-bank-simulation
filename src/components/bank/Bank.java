package components.bank;

import java.util.List;
import java.util.Map;

import socket.components.SocketComponent;
import socket.data.SocketData;

public class Bank extends BaseBank {
  public Bank(
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
