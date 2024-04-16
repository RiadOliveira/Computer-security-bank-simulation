package components.bankService.database;

import java.util.List;
import java.util.Map;

import connections.SocketThread;
import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;

public abstract class BaseBankDatabase extends SocketThread {
  public BaseBankDatabase(
      Map<SocketComponent, List<SocketData>> connectedSockets,
      SocketComponent socketClientComponent) {
    super(connectedSockets, socketClientComponent);
  }

  protected abstract DTO deposit(DTO dto) throws Exception;
  //protected abstract DTO handleGetBalance(DTO dto) throws Exception;
}
