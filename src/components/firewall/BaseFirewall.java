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
  private String TOKEN_SECRET_KEY = null;
  private UUID connectedClientId = null;

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
    connectedClientId = authResponse.getUserId();

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
}
