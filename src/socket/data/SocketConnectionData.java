package socket.data;

import socket.components.SocketComponent;

public class SocketConnectionData {
  private final SocketComponent component;
  private final String ip;
  private final int port;
  
  public SocketConnectionData(
    SocketComponent component, String ip, int port
  ) {
    this.component = component;
    this.ip = ip;
    this.port = port;
  }

  public SocketConnectionData(
    SocketComponent component, String ip
  ) {
    this.component = component;
    this.ip = ip;
    this.port = -1;
  }

  public SocketComponent getComponent() {
    return component;
  }

  public String getIp() {
    return ip;
  }

  public int getPort() {
    return port;
  }
}
