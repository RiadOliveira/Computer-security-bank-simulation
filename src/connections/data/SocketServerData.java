package connections.data;

import connections.components.SocketComponent;

public class SocketServerData {
  private final int port;
  private final SocketComponent clientComponent;
  private final String whitelistedClientAddress;
  private final SocketConnectionData serversToConnect[];

  public SocketServerData(
    int port, SocketComponent clientComponent,
    String whitelistedClientAddress,
    SocketConnectionData[] serversToConnect
  ) {
    this.port = port;
    this.clientComponent = clientComponent;
    this.whitelistedClientAddress = whitelistedClientAddress;
    this.serversToConnect = serversToConnect;
  }

  public int getPort() {
    return port;
  }

  public SocketComponent getClientComponent() {
    return clientComponent;
  }

  public String getWhitelistedClientAddress() {
    return whitelistedClientAddress;
  }

  public SocketConnectionData[] getServersToConnect() {
    return serversToConnect;
  }
}
