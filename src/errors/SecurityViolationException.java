package errors;

public class SecurityViolationException extends AppException {
  public SecurityViolationException(String errorMessage) {
    super(errorMessage);
  }
}
