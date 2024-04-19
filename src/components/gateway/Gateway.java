package components.gateway;

import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import dtos.auth.AuthenticatedDTO;
import dtos.generic.ExceptionDTO;
import dtos.user.UserData;
import utils.ObjectConverter;
import utils.OperationClassifier;
import utils.RandomNumberGenerator;

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
    boolean isReadOperation = isReadOperationForComponent(
      operation, componentToRedirect
    );
    int replicasQuantity = getComponentReplicasQuantity(componentToRedirect);

    DTO componentResponse = getComponentResponse(
      receivedDTO, componentToRedirect,
      isReadOperation, replicasQuantity
    );
    sendSecureDTO(SocketComponent.FIREWALL, componentResponse);
  }

  @Override
  protected void handleExecutionException(Exception exception) {
    executeDefaultExceptionHandling(exception);
  }

  private DTO getComponentResponse(
    DTO dtoToRedirect, SocketComponent component,
    boolean isReadOperation, int replicasQuantity
  ) throws Exception {
    if(isReadOperation) {
      int replicaIndex = RandomNumberGenerator.generateFromZeroTo(
        replicasQuantity
      );
      return communicateWithOneReplica(
        dtoToRedirect, component, replicaIndex
      );
    }

    DTO replicasResponse;
    synchronized(Gateway.class) {
      replicasResponse = communicateWithAllReplicas(
        dtoToRedirect, component, replicasQuantity
      );
    }

    boolean isCreateAccount = RemoteOperation.CREATE_ACCOUNT.equals(
      dtoToRedirect.getOperation()
    );
    if(!isCreateAccount) return replicasResponse;
    return handleCreateAccountAdditionalCommunications(replicasResponse);
  }

  private DTO handleCreateAccountAdditionalCommunications(
    DTO replicasResponse
  ) throws Exception {
    UserData parsedResponse = ObjectConverter.convert(
      replicasResponse
    );
    DTO createAccountDTO = new AuthenticatedDTO(
      parsedResponse.getId()
    ).setOperation(RemoteOperation.CREATE_ACCOUNT);

    var bankServiceComponent = SocketComponent.BANK_SERVICE;
    synchronized(Gateway.class) {
      return communicateWithAllReplicas(
        createAccountDTO, bankServiceComponent,
        getComponentReplicasQuantity(bankServiceComponent)
      );
    }
  }

  private DTO communicateWithOneReplica(
    DTO dtoToRedirect, SocketComponent component,
    int replicaIndex
  ) throws Exception {
    sendSecureDTO(component, replicaIndex, dtoToRedirect);
    return receiveSecureDTO(component, replicaIndex);
  }

  private DTO communicateWithAllReplicas(
    DTO dtoToRedirect, SocketComponent component,
    int replicasQuantity
  ) throws Exception {
    for(int ind = 0; ind < replicasQuantity; ind++) {
      sendSecureDTO(component, ind, dtoToRedirect);
    }

    DTO replicaResponse = null;
    for(int ind = 0; ind < replicasQuantity; ind++) {
      replicaResponse = receiveSecureDTO(component, ind);

      boolean exceptionReplicaResponse = ExceptionDTO.class.isInstance(
        replicaResponse
      );
      if(exceptionReplicaResponse) break;
    }

    return replicaResponse;
  }

  private SocketComponent getComponentToRedirect(RemoteOperation operation) {
    if(OperationClassifier.isForAuthenticationService(operation)) {
      return SocketComponent.AUTHENTICATION_SERVICE;
    }
    return SocketComponent.BANK_SERVICE;
  }

  private boolean isReadOperationForComponent(
    RemoteOperation operation, SocketComponent component
  ) {
    if(SocketComponent.AUTHENTICATION_SERVICE.equals(component)) {
      return !RemoteOperation.CREATE_ACCOUNT.equals(operation);
    }

    switch (operation) {
      case DEPOSIT: return false;
      case UPDATE_FIXED_INCOME: return false;
      case WIRE_TRANSFER: return false;
      case WITHDRAW: return false;
      default: return true;
    }
  }
}
