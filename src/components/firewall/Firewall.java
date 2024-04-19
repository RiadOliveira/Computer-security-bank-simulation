package components.firewall;

import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;
import constants.Constants;
import dtos.RemoteOperation;
import dtos.DTO;
import dtos.auth.AuthResponse;
import dtos.auth.AuthenticatedDTO;
import errors.SecurityViolationException;

public class Firewall extends BaseFirewall {
  public Firewall(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
  }

  @Override
  protected void execute() throws Exception {
    DTO receivedDTO = receiveSecureDTO(SocketComponent.CLIENT);
    RemoteOperation operation = receivedDTO.getOperation();

    boolean isLogout = RemoteOperation.LOGOUT.equals(operation);
    if(isLogout) {
      handleLogout();
      return;
    }

    checkAndParseDTOWithPermissionValidation(receivedDTO);
    handleReceivedDTO(receivedDTO);
  }

  @Override
  protected void handleExecutionException(Exception exception) {
    executeDefaultExceptionHandling(exception);
  }

  private void checkAndParseDTOWithPermissionValidation(
    DTO receivedDTO
  ) throws Exception {
    RemoteOperation operation = receivedDTO.getOperation();

    boolean isBackdoorAccessAttempt = RemoteOperation.BACKDOOR_ACCESS.equals(
      operation
    );
    if(isBackdoorAccessAttempt && !Constants.BACKDOOR_ACCESS_ALLOWED()) {
      throw new SecurityViolationException("Operação inválida bloqueada!");
    }

    if(isBackdoorAccessAttempt || !operationRequiresAuth(operation)) return;
    if(!userIsLogged()) {
      throw new SecurityViolationException(
        "O usuário precisa estar autenticado para executar esta ação!"
      );
    }

    boolean isAuthenticatedDTO = AuthenticatedDTO.class.isInstance(
      receivedDTO
    );
    if(!isAuthenticatedDTO) {
      throw new SecurityViolationException(
        "É necessário enviar um DTO autenticado para essa ação!"
      );
    }

    AuthenticatedDTO authenticatedDTO = (AuthenticatedDTO) receivedDTO;
    if(!validAuthenticatedDTO(authenticatedDTO)) {
      throw new SecurityViolationException("Token de autenticação inválido!");
    }
    parseAuthenticatedDTO(authenticatedDTO);
  }

  private boolean operationRequiresAuth(RemoteOperation operation) {
    boolean isCreateAccount = RemoteOperation.CREATE_ACCOUNT.equals(operation);
    boolean isAuthenticate = RemoteOperation.AUTHENTICATE.equals(operation);

    return !isCreateAccount && !isAuthenticate;
  }

  private void handleReceivedDTO(DTO receivedDTO) throws Exception {
    sendSecureDTO(SocketComponent.GATEWAY, receivedDTO);
    DTO gatewayResponse = receiveSecureDTO(SocketComponent.GATEWAY);

    boolean authenticationResponse = AuthResponse.class.isInstance(gatewayResponse);
    if(authenticationResponse) handleAuthResponse((AuthResponse) gatewayResponse);

    sendSecureDTO(SocketComponent.CLIENT, gatewayResponse);
  }
}
