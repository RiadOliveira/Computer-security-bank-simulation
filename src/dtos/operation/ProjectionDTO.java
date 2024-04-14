package dtos.operation;

import dtos.generic.ValueDTO;
import utils.ConsolePrinter;
import utils.ValueFormatter;

public class ProjectionDTO extends ValueDTO {
  private final int months;

  public ProjectionDTO(double value, int months) {
    super(value);
    this.months = months;
  }

  @Override
  public void print() {
    print(0);
  }

  public void print(int spacesBefore) {
    String spaces = " ".repeat(spacesBefore);

    ConsolePrinter.println(
      spaces + "Projeção de " +
      months + " meses: " +
      ValueFormatter.formatToBrazilianCurrency(value)
    );
  }

  public int getMonths() {
    return months;
  }

  public double getValue() {
    return value;
  }
}
