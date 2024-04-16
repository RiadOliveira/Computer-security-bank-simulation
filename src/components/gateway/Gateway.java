package components.gateway;

import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;

public class Gateway extends BaseGateway {
  public Gateway(
      Map<SocketComponent, List<SocketData>> connectedSockets,
      SocketComponent socketClientComponent) {
    super(connectedSockets, socketClientComponent);
  }

  @Override
  protected void execute() throws Exception {
    DTO receivedDTO = receiveDTO(SocketComponent.FIREWALL);
    handleReceivedDTO(receivedDTO);
  }

  private void handleReceivedDTO(DTO receivedDTO) throws Exception{
    RemoteOperation operation = receivedDTO.getOperation();

    if(operationRequiresAuth(operation)){
      
    }
  }

  private boolean operationRequiresAuth(RemoteOperation operation) {
    boolean isCreateAccount = operation.equals(RemoteOperation.CREATE_ACCOUNT);
    boolean isAuthenticate = operation.equals(RemoteOperation.AUTHENTICATE);

    return !isCreateAccount && !isAuthenticate;
  }

  @Override
  protected void handleExecutionException(Exception exception) {
  }
}
