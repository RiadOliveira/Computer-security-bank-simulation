package dtos.auth;

import dtos.DTO;
import utils.ConsolePrinter;

public class AuthRequest extends DTO {
  private final String cpf;
  private final String password;

  public AuthRequest(String cpf, String password) {
    this.cpf = cpf;
    this.password = password;
  }

  @Override
  public void print() {
    String propertySpaces = "  ";

    ConsolePrinter.println("Dados de autenticação: ");
    ConsolePrinter.println(propertySpaces + "CPF: " + cpf);
    ConsolePrinter.println(propertySpaces + "Senha: " + password);
  }

  public String getCpf() {
    return cpf;
  }

  public String getPassword() {
    return password;
  }
}
