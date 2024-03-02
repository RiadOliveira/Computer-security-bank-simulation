package dtos.generic;

import dtos.DTO;
import utils.ConsolePrinter;
import utils.ValueFormatter;

public class ValueDTO extends DTO {
  protected final double value;

  public ValueDTO(double value) {
    this.value = value;
  }

  @Override
  public void print() {
    ConsolePrinter.println(
      "Valor: " +
      ValueFormatter.formatToBrazilianCurrency(value)
    );
  }

  public double getValue() {
    return value;
  }
}
