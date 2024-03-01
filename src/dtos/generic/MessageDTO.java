package dtos.generic;

import dtos.DTO;
import utils.ConsolePrinter;

public class MessageDTO extends DTO {
  protected final String message;

  public MessageDTO(String message) {
    this.message = message;
  }

  @Override
  public void print() {
    ConsolePrinter.println(message);
  }
}
