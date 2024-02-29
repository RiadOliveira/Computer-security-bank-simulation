package dtos;

import utils.ConsolePrinter;

public class UpdateValueDTO extends DTO {
  private final long updateValue;

  public UpdateValueDTO(long updateValue) {
    this.updateValue = updateValue;
  }

  @Override
  public void print() {
    ConsolePrinter.println(
      "Valor de atualização: " + updateValue
    );
  }

  public long getUpdateValue() {
    return updateValue;
  }
}
