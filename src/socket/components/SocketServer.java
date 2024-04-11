package socket.components;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import socket.SocketProcess;
import socket.SocketThread;
import socket.data.SocketData;
import socket.data.SocketConnectionData;
import socket.data.SocketServerData;
import utils.ConnectionUtils;
import utils.ConsolePrinter;

public class SocketServer extends SocketProcess {
  private final SocketServerData data;

  public SocketServer(
    Class<? extends SocketThread> socketThreadClass,
    SocketServerData data
  ) {
    super(socketThreadClass);
    this.data = data;
  }

  @Override
  public void run() {
    try (ServerSocket serverSocket = new ServerSocket(data.getPort())) {
      ConsolePrinter.println("Servidor iniciado!\n");
      acceptAndHandleConnections(serverSocket);
    } catch (Exception exception) {
      ConsolePrinter.printlnError("Falha interna do servidor socket!");
    }
  }

  private void acceptAndHandleConnections(ServerSocket serverSocket) {
    while(true) {
      try {
        handleClientConnection(serverSocket.accept());
      } catch(Exception exception) {
        ConsolePrinter.printlnError(
          "Falha ao se conectar com cliente socket!"
        );
      }
    }
  }

  private void handleClientConnection(Socket clientSocket) throws Exception {
    String clientAddress = clientSocket.getInetAddress().getHostAddress();

    if(!validClientConnection(clientSocket)) {
      ConsolePrinter.printlnError(
        "Cliente socket " + clientAddress +
        " não está na whitelist!"
      );
      return;
    }

    var connectedSockets = generateConnectedSockets(clientSocket);
    Thread serverThread = new Thread(
      socketThreadConstructor.newInstance(
        connectedSockets, data.getClientComponent()
      )
    );
    
    ConsolePrinter.println(
      "Servidor socket conectou-se com sucesso ao cliente socket " +
      clientAddress
    );
    serverThread.start();
  }
  
  private boolean validClientConnection(Socket clientSocket) {
    String whitelistClientIps[] = data.getWhitelistedClientIps();
    if(whitelistClientIps.length == 0) return true;

    String clientIp = clientSocket.getInetAddress().getHostAddress();
    for(String whitelistIp : whitelistClientIps) {
      if(whitelistIp.equals(clientIp)) return true;
    }

    return false;
  }

  private Map<SocketComponent, List<SocketData>> generateConnectedSockets(
    Socket clientSocket
  ) throws Exception {
    Map<SocketComponent, List<SocketData>> connectedSockets = new HashMap<>();
    connectedSockets.put(
      data.getClientComponent(),
      new ArrayList<>(List.of(new SocketData(clientSocket)))
    );

    var serversToConnect = data.getServersToConnect();
    List<Future<Socket>> socketRequests = new ArrayList<>(serversToConnect.length);

    for(SocketConnectionData server : serversToConnect) {
      Future<Socket> request = ConnectionUtils.asynchronouslyConnectToSocketServerWithRetry(
        server.getIp(), server.getPort()
      );
      socketRequests.add(request);
    }

    addRequestsToConnectedSockets(connectedSockets, socketRequests);
    return connectedSockets;
  }

  private void addRequestsToConnectedSockets(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    List<Future<Socket>> socketRequests
  ) throws Exception {
    SocketConnectionData[] serversToConnect = data.getServersToConnect();

    for(int ind=0 ; ind<serversToConnect.length ; ind++) {
      Socket socket = socketRequests.get(ind).get();
      SocketData socketData = new SocketData(socket);

      SocketComponent component = serversToConnect[ind].getComponent();
      List<SocketData> componentSocketsData = connectedSockets.get(component);

      if(componentSocketsData != null) componentSocketsData.add(socketData);
      else connectedSockets.put(
        component, new ArrayList<>(List.of(socketData))
      );
    }
  }
}
