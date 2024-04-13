package connections.data;

import connections.components.SocketComponent;

public class SocketConnectionData {
  private final SocketComponent component;
  private String address;
  private int port;
  
  public SocketConnectionData(
    SocketComponent component, String address, int port
  ) {
    this.component = component;
    this.address = address;
    this.port = port;
  }
  
  public SocketComponent getComponent() {
    return component;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }
}
