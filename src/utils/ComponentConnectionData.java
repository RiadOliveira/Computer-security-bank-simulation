package utils;

import connections.data.SocketConnectionData;

public class ComponentConnectionData {
  private final int portToConnectToServers;
  private final SocketConnectionData socketConnectionData;

  public ComponentConnectionData(
    int portToConnectToServers,
    SocketConnectionData socketConnectionData
  ) {
    this.portToConnectToServers = portToConnectToServers;
    this.socketConnectionData = socketConnectionData;
  }

  public int getPortToConnectToServers() {
    return portToConnectToServers;
  }

  public SocketConnectionData getSocketConnectionData() {
    return socketConnectionData;
  }
}
