package dtos.generic;

public class ExceptionDTO extends MessageDTO {
  public ExceptionDTO(String message) {
    super("[Erro] " + message);
  }
}