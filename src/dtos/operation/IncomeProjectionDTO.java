package dtos.operation;

import dtos.generic.ValueDTO;

public class IncomeProjectionDTO extends ValueDTO {
  protected final float yieldPercentage;
  protected final ProjectionDTO[] projections;

  public IncomeProjectionDTO(
    long value, float yieldPercentage,
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
        months, value * projectionMultiplier
      );
    }
  }

  public float getYieldPercentage() {
    return yieldPercentage;
  }

  public ProjectionDTO[] getProjections() {
    return projections;
  }
}
