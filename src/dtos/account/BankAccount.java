package dtos.account;

import dtos.DTO;
import utils.ConsolePrinter;
import utils.NumberStringGenerator;
import utils.ValueFormatter;

public class BankAccount extends DTO {
  protected final String agency;
  protected final String accountNumber;
  protected final ClientData clientData;

  protected double balance = 0;
  protected double fixedIncome = 0;

  public BankAccount(
    String agency, String accountNumber,
    double balance, double fixedIncome,
    ClientData clientData
  ) {
    this.agency = agency;
    this.accountNumber = accountNumber;
    this.balance = balance;
    this.fixedIncome = fixedIncome;
    this.clientData = clientData;
  }

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
  
  @Override
  public void print() {
    String propertySpaces = "  ";

    ConsolePrinter.println("Dados da conta: ");
    ConsolePrinter.println(propertySpaces + "Agência: " + agency);
    ConsolePrinter.println(
      propertySpaces + "Número da conta: " + accountNumber
    );
    ConsolePrinter.println(
      propertySpaces + "Saldo: " +
      ValueFormatter.formatToBrazilianCurrency(balance)
    );
    ConsolePrinter.println(
      propertySpaces + "Renda fixa: " +
      ValueFormatter.formatToBrazilianCurrency(fixedIncome)
    );
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

  public double getBalance() {
    return balance;
  }

  public void updateBalance(double updateValue) {
    this.balance += updateValue;
  }

  public double getFixedIncome() {
    return fixedIncome;
  }

  public void updateFixedIncome(double updateValue) {
    this.fixedIncome += updateValue;
    this.balance -= updateValue;
  }
}
