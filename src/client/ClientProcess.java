package client;

import java.net.Socket;
import java.util.concurrent.TimeUnit;

import utils.ConsolePrinter;

public class ClientProcess {
  private static final int WAIT_TIME_TO_TRY_RECONNECTION = 3;

  private final String serverIp;
  private final int serverPort;

  public ClientProcess(String serverIp, int serverPort) {
    this.serverIp = serverIp;
    this.serverPort = serverPort;
  }

  public void run() {
    ConsolePrinter.println("Cliente iniciado!");

    Socket serverSocket = connectToServerWithRetry();
    Thread clientThread = new Thread(
      new ClientThread(serverSocket)
    );
    clientThread.start();
  }

  private Socket connectToServerWithRetry() {
    ConsolePrinter.print("Tentando conectar-se ao servidor...");

    Socket serverSocket = null;
    while(serverSocket == null) {
      try {
        serverSocket = new Socket(serverIp, serverPort);
      } catch(Exception exception) {
        waitToReconnect();
      }
    }
    
    ConsolePrinter.print("Servidor conectado com sucesso!\n");
    return serverSocket;
  }

  private static void waitToReconnect() {
    ConsolePrinter.print(
      "Falha ao conectar-se ao servidor, tentando novamente em" +
      WAIT_TIME_TO_TRY_RECONNECTION + " segundos..."
    );

    try {
      TimeUnit.SECONDS.sleep(WAIT_TIME_TO_TRY_RECONNECTION);
    } catch (Exception exception) {
    }
  }
}