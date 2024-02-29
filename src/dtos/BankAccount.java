package dtos;

import utils.ConsolePrinter;
import utils.NumberStringGenerator;

public class BankAccount implements DTO {
  private final String agency;
  private final String accountNumber;
  private final ClientData clientData;

  private long balance = 0;

  public BankAccount(
    String agency, String accountNumber,
    ClientData clientData
  ) {
    this.agency = agency;
    this.accountNumber = accountNumber;
    this.clientData = clientData;
  }

  public BankAccount(ClientData clientData) {
    this.clientData = clientData;
    this.agency = NumberStringGenerator.generate(4);
    this.accountNumber = NumberStringGenerator.generate(8);
  }
  
  public void print() {
    String propertySpaces = "  ";

    ConsolePrinter.println("Dados da conta: ");
    ConsolePrinter.println(propertySpaces + "Agência: " + agency);
    ConsolePrinter.println(
      propertySpaces + "Número da conta: " + accountNumber
    );
    ConsolePrinter.println(propertySpaces + "Saldo: " + balance);
    clientData.print(2);
  }

  public String getAgency() {
    return agency;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public ClientData getClientData() {
    return clientData;
  }

  public long getBalance() {
    return balance;
  }

  public void updateBalance(long updateValue) {
    this.balance += updateValue;
  }
}
