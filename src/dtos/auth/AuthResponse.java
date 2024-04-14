package dtos.auth;

import java.util.UUID;

import dtos.DTO;
import dtos.user.UserData;
import utils.ConsolePrinter;

public class AuthResponse extends DTO {
  private final UUID userId;
  private final UserData userData;

  private String token;

  public AuthResponse(
    UUID userId, UserData userData,
    String token
  ) {
    this.userId = userId;
    this.userData = userData;
    this.token = token;
  }

  @Override
  public void print() {
    ConsolePrinter.println("Id: " + userId);
    ConsolePrinter.println("Dados do usuário:");
    userData.print(2);
  }

  public UUID getUserId() {
    return userId;
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
