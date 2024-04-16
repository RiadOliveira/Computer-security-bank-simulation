package dtos.user;

import java.util.UUID;

import dtos.DTO;
import utils.ConsolePrinter;
import utils.Hasher;

public class UserData extends DTO {
  private UUID id;
  private final String name;
  private final String cpf;
  private final String address;
  private final String phoneNumber;
  private final String password;

  public UserData(
      String name, String cpf,
      String address, String phoneNumber,
      String password) {
    this.name = name;
    this.cpf = cpf;
    this.address = address;
    this.phoneNumber = phoneNumber;
    this.password = Hasher.hashAndEncode(password);
  }

  @Override
  public void print() {
    print(0);
  }

  public void print(int spacesBefore) {
    String spaces = " ".repeat(spacesBefore);
    String propertySpace = spaces + "  ";

    ConsolePrinter.println(spaces + "Dados do cliente: ");
    ConsolePrinter.println(propertySpace + "Id: " + id);
    ConsolePrinter.println(propertySpace + "Nome: " + name);
    ConsolePrinter.println(propertySpace + "CPF: " + cpf);
    ConsolePrinter.println(propertySpace + "Endereço: " + address);
    ConsolePrinter.println(propertySpace + "Telefone: " + phoneNumber);
    ConsolePrinter.println(propertySpace + "Senha: " + password);
  }

  public UUID getId() {
    return id;
  }

  public void generateAndSetId() {
    this.id = UUID.randomUUID();
  }

  public String getName() {
    return name;
  }

  public String getCpf() {
    return cpf;
  }

  public String getAddress() {
    return address;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getPassword() {
    return password;
  }
}
