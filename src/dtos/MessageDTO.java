package dtos;

import utils.ConsolePrinter;

public class MessageDTO extends DTO {
  private final String message;

  public MessageDTO(String message) {
    this.message = message;
  }

  @Override
  public void print() {
    ConsolePrinter.println(message);
  }
}
