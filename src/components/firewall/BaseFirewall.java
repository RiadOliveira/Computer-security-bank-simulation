package components.firewall;

import java.time.Duration;
import java.time.Instant;
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
import errors.SecurityViolationException;
import security.crypto.CryptoProcessor;
import utils.TokenProcessor;

public abstract class BaseFirewall extends SocketThread {
  private static final int MAX_AUTH_ATTEMPTS_BEFORE_LOCKOUT = 3;
  private static final int AUTH_LOCKOUT_TIMEOUT_IN_SECONDS = 20;

  private String TOKEN_SECRET_KEY = null;
  private UUID connectedUserId = null;
  
  private int userAuthenticationAttempts = 0;
  private Instant authLockoutStart = null;

  public BaseFirewall(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
  }

  protected void validateUserAccessAttempt() throws Exception {
    if(authLockoutStart == null) return;

    Duration duration = Duration.between(
      authLockoutStart, Instant.now()
    );
    long secondsPassed = duration.getSeconds();

    boolean timeoutPassed = 
      secondsPassed > AUTH_LOCKOUT_TIMEOUT_IN_SECONDS;
    if(timeoutPassed) {
      authLockoutStart = null;
      return;
    }

    throw new SecurityViolationException(
      "Acesso bloqueado. Tente novamente em " +
      (AUTH_LOCKOUT_TIMEOUT_IN_SECONDS - secondsPassed) +
      " segundos."
    );
  }

  protected void handleAuthenticationAttempt(DTO response) {
    connectedUserId = null;
    boolean validAuthResponse = AuthResponse.class.isInstance(
      response
    );

    if(validAuthResponse) {
      handleValidAuthResponse((AuthResponse) response);
      userAuthenticationAttempts = 0;
    } else handleInvalidAuthResponse();
  }

  private void handleValidAuthResponse(AuthResponse authResponse) {
    initializeTokenSecretKey();
    connectedUserId = authResponse.getUserData().getId();

    String generatedToken = TokenProcessor.generate(
      TOKEN_SECRET_KEY, connectedUserId
    );
    authResponse.setToken(generatedToken);
  }

  private void handleInvalidAuthResponse() {
    userAuthenticationAttempts++;
    boolean reachedMaxAttempts = 
      userAuthenticationAttempts == MAX_AUTH_ATTEMPTS_BEFORE_LOCKOUT;

    if(reachedMaxAttempts) authLockoutStart = Instant.now();
  }

  protected boolean validAuthenticatedDTO(
    AuthenticatedDTO authenticatedDTO
  ) {
    if(connectedUserId == null) return false;

    initializeTokenSecretKey();
    return TokenProcessor.isValid(
      authenticatedDTO.getToken(), TOKEN_SECRET_KEY,
      connectedUserId
    );
  }

  protected void parseAuthenticatedDTO(
    AuthenticatedDTO authenticatedDTO
  ) {
    authenticatedDTO.setUserId(connectedUserId);
  }

  protected void handleLogout() throws Exception {
    connectedUserId = null;

    DTO logoutDTO = new OperationDTO(RemoteOperation.LOGOUT);
    sendSecureDTO(SocketComponent.CLIENT, logoutDTO);
  }

  protected boolean userIsLogged() {
    return connectedUserId != null;
  }

  private void initializeTokenSecretKey() {
    if(TOKEN_SECRET_KEY != null) return;

    byte[] gatewayEncodedKey = getComponentHashKey(
      SocketComponent.GATEWAY
    ).getEncoded();
    TOKEN_SECRET_KEY = CryptoProcessor.encodeBase64(gatewayEncodedKey);
  }
}
