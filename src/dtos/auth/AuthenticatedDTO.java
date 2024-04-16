package dtos.auth;

import java.util.UUID;

import dtos.DTO;
import utils.ConsolePrinter;

public class AuthenticatedDTO extends DTO {
  private final String token;
  private final DTO dto;
  
  private UUID userId = null;
  
  public AuthenticatedDTO(String token, DTO dto) {
    this.token = token;
    this.dto = dto;
  }

  @Override
  public void print() {
    ConsolePrinter.println("Token: " + token);
    ConsolePrinter.println("Dados do DTO:");
    dto.print();
  }

  public String getToken() {
    return token;
  }

  public DTO getDTO() {
    return dto;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }
}
