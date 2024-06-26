package dtos.bankOperation;

import dtos.generic.ValueDTO;
import utils.ConsolePrinter;

public class WireTransferDTO extends ValueDTO {
  private final String targetAgency;
  private final String targetAccountNumber;

  public WireTransferDTO(
    double value, String targetAgency,
    String targetAccountNumber
  ) {
    super(value);
    this.targetAgency = targetAgency;
    this.targetAccountNumber = targetAccountNumber;
  }

  @Override
  public void print() {
    super.print();
    String propertySpaces = "  ";

    ConsolePrinter.println("Dados da conta para transferir:");
    ConsolePrinter.println(
      propertySpaces + "Agência: " + targetAgency
    );
    ConsolePrinter.println(
      propertySpaces + "Número da conta: " + targetAccountNumber
    );
  }

  public String getTargetAgency() {
    return targetAgency;
  }

  public String getTargetAccountNumber() {
    return targetAccountNumber;
  }
}
