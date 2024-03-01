package dtos.auth;

import javax.crypto.SecretKey;

import dtos.DTO;
import dtos.account.ClientData;
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
    ConsolePrinter.println("AuthKey: " + authKey.toString());
    clientData.print();
  }

  public SecretKey getAuthKey() {
    return authKey;
  }

  public ClientData getClientData() {
    return clientData;
  }
}
