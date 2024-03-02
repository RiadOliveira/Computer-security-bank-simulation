package dtos.auth;

import javax.crypto.SecretKey;

import dtos.DTO;
import dtos.account.ClientData;
import security.CryptoProcessor;
import utils.ConsolePrinter;

public class AuthResponse extends DTO {
  protected final SecretKey authKey;
  protected final ClientData clientData;

  public AuthResponse(
    SecretKey authKey, ClientData clientData
  ) {
    this.authKey = authKey;
    this.clientData = clientData;
  }

  @Override
  public void print() {
    String parsedKey = CryptoProcessor.encodeBase64(
      authKey.getEncoded()
    );

    ConsolePrinter.println("AuthKey: " + parsedKey);
    clientData.print();
  }

  public SecretKey getAuthKey() {
    return authKey;
  }

  public ClientData getClientData() {
    return clientData;
  }
}
