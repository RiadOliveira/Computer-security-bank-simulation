package dtos.auth;

import java.util.UUID;

import dtos.DTO;
import utils.ConsolePrinter;

public class AuthenticatedDTO extends DTO {
  private String token;
  private UUID userId;
  private DTO dto;
  
  public AuthenticatedDTO(String token, DTO dto) {
    this.token = token;
    this.dto = dto;
  }

  public AuthenticatedDTO(UUID userId, DTO dto) {
    this.userId = userId;
    this.dto = dto;
  }

  public AuthenticatedDTO(UUID userId) {
    this.userId = userId;
  }

  @Override
  public void print() {
    ConsolePrinter.println("Token: " + token);
    ConsolePrinter.println("UserId: " + userId);

    if(dto != null) {
      ConsolePrinter.println("Dados do DTO:");
      dto.print();
    }
  }

  public String getToken() {
    return token;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public DTO getDTO() {
    return dto;
  }
}
