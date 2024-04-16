package components.bankService;

import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;

public class BankService extends BaseBankService {
  public BankService(
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
