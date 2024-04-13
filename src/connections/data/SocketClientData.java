package connections.data;

public class SocketClientData {
  private final int portToConnectToServer;
  private final SocketConnectionData serverToConnect;

  public SocketClientData(
    int portToConnectToServer, SocketConnectionData serverToConnect
  ) {
    this.portToConnectToServer = portToConnectToServer;
    this.serverToConnect = serverToConnect;
  }

  public int getPortToConnectToServer() {
    return portToConnectToServer;
  }

  public SocketConnectionData getServerToConnect() {
    return serverToConnect;
  }
}
