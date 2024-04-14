package dtos.generic;

import dtos.RemoteOperation;
import dtos.DTO;
import utils.ConsolePrinter;

public class OperationDTO extends DTO {
  public OperationDTO(RemoteOperation operation) {
    this.operation = operation;
  }

  @Override
  public void print() {
    ConsolePrinter.println(operation.name());
  }
}
