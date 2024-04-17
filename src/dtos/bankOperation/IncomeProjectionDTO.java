package dtos.bankOperation;

import dtos.generic.ValueDTO;
import utils.ConsolePrinter;
import utils.ValueFormatter;

public class IncomeProjectionDTO extends ValueDTO {
  private final double yieldPercentage;
  private final ProjectionDTO[] projections;

  public IncomeProjectionDTO(
    double value, double yieldPercentage,
    int[] monthsForProjections
  ) {
    super(value);
    this.yieldPercentage = yieldPercentage;

    int quantityOfProjections = monthsForProjections.length;
    this.projections = new ProjectionDTO[quantityOfProjections];

    for(int ind=0 ; ind<quantityOfProjections ; ind++) {
      int months = monthsForProjections[ind];
      double projectionMultiplier = Math.pow(
        1+yieldPercentage, months
      );

      projections[ind] = new ProjectionDTO(
        value * projectionMultiplier, months
      );
    }
  }

  @Override
  public void print() {
    ConsolePrinter.println(
      "Valor aplicado: " +
      ValueFormatter.formatToBrazilianCurrency(value)
    );
    ConsolePrinter.println(
      "Porcentagem de rendimeto: " +
      yieldPercentage*100 + "%"
    );

    for(ProjectionDTO projection : projections) {
      projection.print(2);
    }
  }

  public double getYieldPercentage() {
    return yieldPercentage;
  }

  public ProjectionDTO[] getProjections() {
    return projections;
  }
}
