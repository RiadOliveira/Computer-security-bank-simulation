package dtos;

import utils.ConsolePrinter;

public class AuthData extends DTO {
  private final String agency;
  private final String accountNumber;
  private final String password;

  public AuthData(
    String agency, String accountNumber,
    String password
  ) {
    this.agency = agency;
    this.accountNumber = accountNumber;
    this.password = password;
  }

  @Override
  public void print() {
    String propertySpaces = "  ";

    ConsolePrinter.println("Dados de autenticação: ");
    ConsolePrinter.println(propertySpaces + "Agência: " + agency);
    ConsolePrinter.println(
      propertySpaces + "Número da conta: " + accountNumber
    );
    ConsolePrinter.println(propertySpaces + "Senha: " + password);
  }

  public String getAgency() {
    return agency;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public String getPassword() {
    return password;
  }
}
