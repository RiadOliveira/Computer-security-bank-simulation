package components.bankService;

import java.util.List;
import java.util.Map;

import connections.SocketThread;
import connections.components.SocketComponent;
import connections.data.SocketData;

public abstract class BaseBankService extends SocketThread {
  public BaseBankService(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
  }
}
