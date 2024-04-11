package socket.components;

import socket.SocketProcess;
import socket.SocketThread;
import socket.data.SocketConnectionData;

public class SocketClient extends SocketProcess {
  private final SocketConnectionData serverToConnect;

  public SocketClient(
    Class<SocketThread> socketThreadClass,
    SocketConnectionData serverToConnect
  ) {
    super(socketThreadClass);
    this.serverToConnect = serverToConnect;
  }

  @Override
  public void run() {
  }
}
