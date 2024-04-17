package components.firewall;

import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.RemoteOperation;
import dtos.DTO;
import dtos.auth.AuthResponse;
import dtos.auth.AuthenticatedDTO;
import errors.AppException;
import errors.SecurityViolationException;

public class Firewall extends BaseFirewall {
  public Firewall(
      Map<SocketComponent, List<SocketData>> connectedSockets,
      SocketComponent socketClientComponent) {
    super(connectedSockets, socketClientComponent);
  }

  @Override
  protected void execute() throws Exception {
    DTO receivedDTO = receiveSecureDTO(SocketComponent.CLIENT);

    boolean tryingToAccessBackdoor = RemoteOperation.ACCESS_BACKDOOR.equals(
        receivedDTO.getOperation());

    if (tryingToAccessBackdoor) {
     // throw new AppException("Operação inválida bloqueada.\n");

      sendSecureDTO(SocketComponent.GATEWAY, receivedDTO);
      handleReceivedDTO(receivedDTO);
    } else {
      boolean isLogout = RemoteOperation.LOGOUT.equals(
          receivedDTO.getOperation());
      if (isLogout) {
        handleLogout();
        return;
      }

      boolean isAuthenticatedDTO = AuthenticatedDTO.class.isInstance(
          receivedDTO);
      throwsExceptionIfInvalidOperationRequested(receivedDTO, isAuthenticatedDTO);

      if (isAuthenticatedDTO)
        parseAuthenticatedDTO(((AuthenticatedDTO) receivedDTO));

      handleReceivedDTO(receivedDTO);
    }
  }

  @Override
  protected void handleExecutionException(Exception exception) {
    executeDefaultExceptionHandling(exception);
  }

  private void throwsExceptionIfInvalidOperationRequested(
      DTO receivedDTO, boolean isAuthenticatedDTO) throws Exception {
    RemoteOperation operation = receivedDTO.getOperation();
    boolean requiresAuthentication = operationRequiresAuth(operation);
    if (!requiresAuthentication)
      return;

    if (!userIsLogged()) {
      throw new SecurityViolationException(
          "O usuário precisa estar autenticado para executar esta ação!");
    }
    if (!isAuthenticatedDTO) {
      throw new SecurityViolationException(
          "É necessário enviar um DTO autenticado para essa ação!");
    }
    if (!validAuthenticatedDTO((AuthenticatedDTO) receivedDTO)) {
      throw new SecurityViolationException("Token de autenticação inválido!");
    }
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
    if (authenticationResponse)
      handleAuthResponse((AuthResponse) gatewayResponse);

    sendSecureDTO(SocketComponent.CLIENT, gatewayResponse);
  }
}
