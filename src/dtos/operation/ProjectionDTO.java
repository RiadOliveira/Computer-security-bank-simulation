package dtos.operation;

public class ProjectionDTO {
  protected final int months;
  protected final double value;

  public ProjectionDTO(int months, double value) {
    this.months = months;
    this.value = value;
  }

  public int getMonths() {
    return months;
  }

  public double getValue() {
    return value;
  }
}
