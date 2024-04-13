package connections.data;

import connections.components.SocketComponent;

public class SocketServerData {
  private final int port, portToConnectToOtherServers;
  private final SocketComponent clientComponent;
  private final String whitelistedClientAddress;
  private final SocketConnectionData serversToConnect[];

  public SocketServerData(
    int port, int portToConnectToOtherServers,
    SocketComponent clientComponent,
    String whitelistedClientAddress,
    SocketConnectionData[] serversToConnect
  ) {
    this.port = port;
    this.portToConnectToOtherServers = portToConnectToOtherServers;
    this.clientComponent = clientComponent;
    this.whitelistedClientAddress = whitelistedClientAddress;
    this.serversToConnect = serversToConnect;
  }

  public int getPort() {
    return port;
  }

  public int getPortToConnectToOtherServers() {
    return portToConnectToOtherServers;
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
