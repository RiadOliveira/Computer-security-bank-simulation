package dtos;

import java.io.Serializable;

import utils.ConsolePrinter;

public class ClientData implements Serializable {
  private final String name;
  private final String cpf;
  private final String address;
  private final String phoneNumber;

  public ClientData(
    String name, String cpf,
    String address, String phoneNumber
  ) {
    this.name = name;
    this.cpf = cpf;
    this.address = address;
    this.phoneNumber = phoneNumber;
  }

  public void print() {
    print(0);
  }

  public void print(int spacesBefore) {
    String spaces = " ".repeat(spacesBefore);
    String propertySpace = spaces + "  ";

    ConsolePrinter.println(spaces + "Dados do cliente: ");
    ConsolePrinter.println(propertySpace + "Nome: " + name);
    ConsolePrinter.println(propertySpace + "CPF: " + cpf);
    ConsolePrinter.println(propertySpace + "Endereço: " + address);
    ConsolePrinter.println(propertySpace + "Telefone: " + phoneNumber);
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
}
