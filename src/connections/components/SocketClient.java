package connections.components;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connections.SocketProcess;
import connections.SocketThread;
import connections.data.SocketConnectionData;
import connections.data.SocketData;
import utils.ConnectionUtils;
import utils.ConsolePrinter;

public class SocketClient extends SocketProcess {
  private final SocketConnectionData serverToConnect;

  public SocketClient(
    Class<? extends SocketThread> socketThreadClass,
    SocketConnectionData serverToConnect
  ) {
    super(socketThreadClass);
    this.serverToConnect = serverToConnect;
  }

  @Override
  public void run() {
    ConsolePrinter.println("Cliente iniciado!\n");

    try {
      Socket serverSocket = ConnectionUtils.connectToSocketServerWithRetry(
        serverToConnect.getAddress(), serverToConnect.getPort()
      );
      
      handleConnection(serverSocket);
    } catch (Exception exception) {
      ConsolePrinter.printlnError("Falha interna do cliente socket!");
    }
  }

  private void handleConnection(Socket serverSocket) throws Exception {
    var connectedSockets = generateConnectedSockets(serverSocket);
    Thread clientThread = new Thread(
      socketThreadConstructor.newInstance(connectedSockets, null)
    );
    clientThread.start();
  }

  private Map<SocketComponent, List<SocketData>> generateConnectedSockets(
    Socket serverSocket
  ) throws Exception {
    Map<SocketComponent, List<SocketData>> connectedSockets = new HashMap<>();
    connectedSockets.put(
      serverToConnect.getComponent(),
      new ArrayList<>(List.of(new SocketData(serverSocket)))
    );

    return connectedSockets;
  }
}
