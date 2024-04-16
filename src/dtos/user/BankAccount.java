package dtos.user;

import java.util.UUID;

import dtos.DTO;
import utils.ConsolePrinter;
import utils.NumberStringGenerator;
import utils.ValueFormatter;

public class BankAccount extends DTO {
  private final UUID userId;
  private final String agency;
  private final String accountNumber;

  private double balance = 0;
  private double fixedIncome = 0;

  public BankAccount(
    UUID userId, String agency, String accountNumber,
    double balance, double fixedIncome
  ) {
    this.userId = userId;
    this.agency = agency;
    this.accountNumber = accountNumber;
    this.balance = balance;
    this.fixedIncome = fixedIncome;
  }

  public BankAccount(UUID userId) {
    this.userId = userId;
    this.agency = NumberStringGenerator.generate(4);
    this.accountNumber = NumberStringGenerator.generate(8);
  }

  @Override
  public void print() {
    String propertySpaces = "  ";

    ConsolePrinter.println("Dados da conta: ");
    ConsolePrinter.println(propertySpaces + "UserId: " + userId);
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
  }

  public UUID getUserId() {
    return userId;
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
