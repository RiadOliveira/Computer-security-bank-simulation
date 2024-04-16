package dtos.auth;

import dtos.DTO;
import dtos.user.UserData;
import utils.ConsolePrinter;

public class AuthResponse extends DTO {
  private final UserData userData;

  private String token;

  public AuthResponse(UserData userData) {
    this.userData = userData;
  }

  @Override
  public void print() {
    ConsolePrinter.println("Token: " + token);
    ConsolePrinter.println("Dados do usuário:");
    userData.print(2);
  }

  public UserData getUserData() {
    return userData;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
