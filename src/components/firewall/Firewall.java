package components.firewall;

import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.AppCommand;
import dtos.DTO;
import dtos.auth.AuthResponse;
import dtos.auth.AuthenticatedDTO;
import dtos.generic.ExceptionDTO;
import errors.AppException;
import utils.ConsolePrinter;

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
    boolean isAuthenticatedDTO = AuthenticatedDTO.class.isInstance(
      receivedDTO
    );
    throwsExceptionIfInvalidCommandRequested(
      receivedDTO, isAuthenticatedDTO
    );

    DTO parsedDTO = !isAuthenticatedDTO ? receivedDTO :
      ((AuthenticatedDTO) receivedDTO).getDTO();
    handleReceivedDTO(parsedDTO);
  }

  @Override
  protected void handleExecutionException(Exception exception) {
    try {
      String errorMessage = exception instanceof AppException ?
        exception.getMessage() : "Falha ao realizar operação!";
  
      ExceptionDTO exceptionDTO = new ExceptionDTO(errorMessage);
      sendSecureDTO(SocketComponent.CLIENT, exceptionDTO);
    } catch (Exception e) {
      ConsolePrinter.printlnError("Falha ao se comunicar com o cliente!");
    }
  }

  private void throwsExceptionIfInvalidCommandRequested(
    DTO receivedDTO, boolean isAuthenticatedDTO
  ) throws Exception {
    AppCommand command = receivedDTO.getCommand();
    boolean requiresAuthentication = commandRequiresAuth(command);
    if(!requiresAuthentication) return;

    if(!userIsLogged()) {
      throw new SecurityException(
        "O usuário precisa estar autenticado para executar esta ação!"
      );
    }
    if(!isAuthenticatedDTO) {
      throw new SecurityException(
        "É necessário enviar um DTO autenticado para essa ação!"
      );
    }
    if(!validAuthenticatedDTO((AuthenticatedDTO) receivedDTO)) {
      throw new SecurityException("Token de autenticação inválido!");
    }
  }

  private boolean commandRequiresAuth(AppCommand command) {
    boolean isCreateAccount = command.equals(AppCommand.CREATE_ACCOUNT);
    boolean isAuthenticate = command.equals(AppCommand.AUTHENTICATE);

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
