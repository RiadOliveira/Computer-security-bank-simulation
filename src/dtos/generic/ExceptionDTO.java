package dtos.generic;

import utils.ConsolePrinter;

public class ExceptionDTO extends MessageDTO {
  public ExceptionDTO(String message) {
    super(ConsolePrinter.ERROR_PREFIX + message);
  }
}