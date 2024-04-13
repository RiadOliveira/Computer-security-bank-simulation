package connections.components;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connections.SocketProcess;
import connections.SocketThread;
import connections.data.SocketClientData;
import connections.data.SocketData;
import utils.ConnectionUtils;
import utils.ConsolePrinter;

public class SocketClient extends SocketProcess {
  private final SocketClientData data;

  public SocketClient(
    Class<? extends SocketThread> socketThreadClass,
    SocketClientData data
  ) {
    super(socketThreadClass);
    this.data = data;
  }

  @Override
  public void run() {
    ConsolePrinter.println("Cliente iniciado!\n");

    try {
      var serverToConnect = data.getServerToConnect();
      Socket serverSocket = ConnectionUtils.connectToSocketServerWithRetry(
        serverToConnect.getAddress(), serverToConnect.getPort(),
        data.getPortToConnectToServer()
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
      data.getServerToConnect().getComponent(),
      new ArrayList<>(List.of(new SocketData(serverSocket)))
    );

    return connectedSockets;
  }
}
