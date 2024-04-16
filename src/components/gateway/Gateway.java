package components.gateway;

import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import dtos.generic.MessageDTO;

public class Gateway extends BaseGateway {
  public Gateway(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
  }

  @Override
  protected void execute() throws Exception {
    DTO receivedDTO = receiveSecureDTO(SocketComponent.FIREWALL);
    RemoteOperation operation = receivedDTO.getOperation();

    SocketComponent componentToRedirect = getComponentToRedirect(operation);
    int replicasQuantity = getComponentReplicasQuantity(componentToRedirect);
    
    for(int ind=0 ; ind<replicasQuantity ; ind++) {
      sendSecureDTO(componentToRedirect, ind, receivedDTO);
    }

    //Needs to be handled
    for(int ind=0 ; ind<replicasQuantity ; ind++) {
      DTO replicaResponse = receiveSecureDTO(componentToRedirect, ind);
    }

    sendSecureDTO(
      SocketComponent.FIREWALL,
      new MessageDTO("Insert response instead of this DTO")
    );
  }

  @Override
  protected void handleExecutionException(Exception exception) {
    executeDefaultExceptionHandling(exception);
  }

  private SocketComponent getComponentToRedirect(RemoteOperation operation) {
    if(isForAuthenticationService(operation)) return SocketComponent.AUTHENTICATION_SERVICE;
    return SocketComponent.BANK_SERVICE;
  }

  private boolean isForAuthenticationService(RemoteOperation operation) {
    boolean isCreateAccount = operation.equals(RemoteOperation.CREATE_ACCOUNT);
    boolean isAuthenticate = operation.equals(RemoteOperation.AUTHENTICATE);

    return isCreateAccount || isAuthenticate;
  }
}
