package socket.data;

import socket.components.SocketComponent;

public class SocketServerData {
  private final int port;
  private final SocketComponent clientComponent;
  private final String whitelistedClientIps[];
  private final SocketConnectionData serversToConnect[];

  public SocketServerData(
    int port, SocketComponent clientComponent,
    String[] whitelistedClientIps,
    SocketConnectionData[] serversToConnect
  ) {
    this.port = port;
    this.clientComponent = clientComponent;
    this.whitelistedClientIps = whitelistedClientIps;
    this.serversToConnect = serversToConnect;
  }

  public int getPort() {
    return port;
  }

  public SocketComponent getClientComponent() {
    return clientComponent;
  }

  public String[] getWhitelistedClientIps() {
    return whitelistedClientIps;
  }

  public SocketConnectionData[] getServersToConnect() {
    return serversToConnect;
  }
}
