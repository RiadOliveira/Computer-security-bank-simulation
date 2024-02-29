package dtos;

import utils.ConsolePrinter;

public class CommandDTO extends DTO {
  @Override
  public void print() {
    ConsolePrinter.println(command.name());
  }
}
