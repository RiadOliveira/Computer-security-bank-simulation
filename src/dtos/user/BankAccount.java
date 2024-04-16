package dtos.user;

import java.util.UUID;

import dtos.DTO;
import utils.ConsolePrinter;
import utils.NumberStringGenerator;
import utils.ValueFormatter;

public class BankAccount extends DTO {
  private final UserData userData;

  private UUID id;
  private String agency;
  private String accountNumber;

  private double balance = 0;
  private double fixedIncome = 0;

  public BankAccount(
    String agency, String accountNumber,
    double balance, double fixedIncome,
    UserData userData
  ) {
    this.agency = agency;
    this.accountNumber = accountNumber;
    this.balance = balance;
    this.fixedIncome = fixedIncome;
    this.userData = userData;
  }

  public BankAccount(UserData userData) {
    this.userData = userData;
    generateDatabaseData();
  }

  @Override
  public void print() {
    String propertySpaces = "  ";

    ConsolePrinter.println("Dados da conta: ");
    ConsolePrinter.println(propertySpaces + "Id: " + id);
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
    userData.print(2);
  }

  public void generateDatabaseData() {
    this.id = UUID.randomUUID();
    this.agency = NumberStringGenerator.generate(4);
    this.accountNumber = NumberStringGenerator.generate(8);
  }

  public UserData getUserData() {
    return userData;
  }

  public UUID getId() {
    return id;
  }

  public String getAgency() {
    return agency;
  }

  public String getAccountNumber() {
    return accountNumber;
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
