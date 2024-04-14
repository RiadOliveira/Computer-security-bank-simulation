package dtos;

import java.io.Serializable;

public abstract class DTO implements Serializable {
  protected AppCommand command = null;

  public abstract void print();

  public AppCommand getCommand() {
    return command;
  }

  public void setCommand(AppCommand command) {
    this.command = command;
  }
}
