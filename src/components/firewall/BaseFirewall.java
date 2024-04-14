package components.firewall;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import connections.SocketThread;
import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.auth.AuthResponse;
import dtos.auth.AuthenticatedDTO;
import security.crypto.CryptoProcessor;
import utils.TokenProcessor;

public abstract class BaseFirewall extends SocketThread {
  private final String TOKEN_SECRET_KEY;
  private UUID connectedClientId = null;

  public BaseFirewall(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);

    byte[] gatewayEncodedKey = getComponentHashKey(
      SocketComponent.GATEWAY
    ).getEncoded();
    TOKEN_SECRET_KEY = CryptoProcessor.encodeBase64(gatewayEncodedKey);
  }

  protected boolean userIsLogged() {
    return connectedClientId != null;
  }

  protected void handleAuthResponse(AuthResponse authResponse) {
    this.connectedClientId = authResponse.getUserId();

    String generatedToken = TokenProcessor.generate(
      TOKEN_SECRET_KEY, connectedClientId
    );
    authResponse.setToken(generatedToken);
  }

  protected boolean validAuthenticatedDTO(
    AuthenticatedDTO authenticatedDTO
  ) {
    if(connectedClientId == null) return false;

    return TokenProcessor.isValid(
      authenticatedDTO.getToken(), TOKEN_SECRET_KEY,
      connectedClientId
    );
  }
}
