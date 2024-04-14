package dtos.auth;

import dtos.DTO;
import utils.ConsolePrinter;

public class AuthenticatedDTO extends DTO {
  private final String token;
  private final DTO dto;
  
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
}
