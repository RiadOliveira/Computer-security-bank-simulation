package process;

public class SocketClient extends SocketProcess {
  private final SocketConnectionData serverToConnect;

  public SocketClient(SocketConnectionData serverToConnect) {
    this.serverToConnect = serverToConnect;
  }

  @Override
  public void run() {
  }
}
