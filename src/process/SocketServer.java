package process;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import utils.ConsolePrinter;

public class SocketServer extends SocketProcess {
  private final int port;
  private final List<SocketConnectionData> whitelistedClients;
  private final List<SocketConnectionData> serversToConnect;

  public SocketServer(
    int port, List<SocketConnectionData> whitelistedClients,
    List<SocketConnectionData> serversToConnect
  ) {
    this.port = port;
    this.whitelistedClients = whitelistedClients;
    this.serversToConnect = serversToConnect;
  }

  @Override
  public void run() {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      ConsolePrinter.println("Servidor iniciado!\n");
      while (true) handleClientConnection(serverSocket.accept());
    } catch (Exception exception) {
      ConsolePrinter.println("Erro interno do servidor socket!");
    }
  }

  private void handleClientConnection(Socket clientSocket) {
    if(!validClientConnection(clientSocket)) {
      ConsolePrinter.printlnError("Cliente socket não permitido!");
      return;
    }

    Thread serverThread = new Thread(new ServerThread(clientSocket));
    ConsolePrinter.println(
      "Servidor socket conectou-se a um novo cliente socket!\n"
    );
    serverThread.start();
  }

  private boolean validClientConnection(Socket clientSocket) {
    if(whitelistedClientIPs.isEmpty()) return true;

    String clientIp = clientSocket.getInetAddress().getHostAddress();
    return whitelistedClientIPs.contains(clientIp);
  }
}
