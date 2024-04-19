package components.firewall;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import connections.SocketThread;
import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.RemoteOperation;
import dtos.DTO;
import dtos.auth.AuthResponse;
import dtos.auth.AuthenticatedDTO;
import dtos.generic.OperationDTO;
import security.crypto.CryptoProcessor;
import utils.TokenProcessor;

public abstract class BaseFirewall extends SocketThread {
  private static final int MAX_AUTH_ATTEMPTS_BEFORE_TIMEOUT = 3;
  private static final int AUTH_FAILURE_TIMEOUT_IN_MS = 10000;

  private String TOKEN_SECRET_KEY = null;
  private UUID connectedClientId = null;
  
  private int clientAuthenticationAttempts = 0;

  public BaseFirewall(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
  }

  protected boolean userIsLogged() {
    return connectedClientId != null;
  }

  protected void handleAuthResponse(AuthResponse authResponse) {
    initializeTokenSecretKey();
    connectedClientId = authResponse.getUserData().getId();

    String generatedToken = TokenProcessor.generate(
      TOKEN_SECRET_KEY, connectedClientId
    );
    authResponse.setToken(generatedToken);
  }

  protected boolean validAuthenticatedDTO(
    AuthenticatedDTO authenticatedDTO
  ) {
    if(connectedClientId == null) return false;

    initializeTokenSecretKey();
    return TokenProcessor.isValid(
      authenticatedDTO.getToken(), TOKEN_SECRET_KEY,
      connectedClientId
    );
  }

  protected void parseAuthenticatedDTO(
    AuthenticatedDTO authenticatedDTO
  ) {
    authenticatedDTO.setUserId(connectedClientId);
  }

  protected void handleLogout() throws Exception {
    connectedClientId = null;

    DTO logoutDTO = new OperationDTO(RemoteOperation.LOGOUT);
    sendSecureDTO(SocketComponent.CLIENT, logoutDTO);
  }

  private void initializeTokenSecretKey() {
    if(TOKEN_SECRET_KEY != null) return;

    byte[] gatewayEncodedKey = getComponentHashKey(
      SocketComponent.GATEWAY
    ).getEncoded();
    TOKEN_SECRET_KEY = CryptoProcessor.encodeBase64(gatewayEncodedKey);
  }

  // private void handleAuthenticationAttempt() {
  //   authenticationAttemptCounter++;
  //   System.out.println(authenticationAttemptCounter + "/3 tentativas antes do bloqueio.\n");

  //   if(authenticationAttemptCounter == 3) {
  //     authenticationAttemptCounter = 0;

  //     System.out.println("Acesso bloqueado. Tente novamente em 10 segundos.\n");
  //     Thread.sleep(THREE_TIME_AUTH_ATTEMPT_FAILURE_TIMEOUT);
  //   }
  // }
}
