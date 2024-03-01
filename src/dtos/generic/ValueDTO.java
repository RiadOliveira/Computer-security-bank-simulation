package dtos.generic;

import dtos.DTO;
import utils.ConsolePrinter;

public class ValueDTO extends DTO {
  protected final double value;

  public ValueDTO(double value) {
    this.value = value;
  }

  @Override
  public void print() {
    ConsolePrinter.println("Valor: " + value);
  }

  public double getValue() {
    return value;
  }
}
