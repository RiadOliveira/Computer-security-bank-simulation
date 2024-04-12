package dtos.generic;

import dtos.AppCommand;
import dtos.DTO;
import utils.ConsolePrinter;

public class CommandDTO extends DTO {
  public CommandDTO(AppCommand command) {
    this.command = command;
  }

  @Override
  public void print() {
    ConsolePrinter.println(command.name());
  }
}
