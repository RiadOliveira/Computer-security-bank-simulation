package dtos;

import java.io.Serializable;

public abstract class DTO implements Serializable {
  protected RemoteOperation operation = null;

  public abstract void print();

  public RemoteOperation getOperation() {
    return operation;
  }

  public DTO setOperation(RemoteOperation operation) {
    this.operation = operation;
    return this;
  }
}
