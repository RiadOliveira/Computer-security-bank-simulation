package process.client;

import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import process.AppProcess;
import utils.ConsolePrinter;

public class ClientProcess extends AppProcess {
  public static final Scanner scanner = new Scanner(System.in);
  private static final int WAIT_TIME_TO_TRY_RECONNECTION = 3;

  private final String serverIp;
  private final int serverPort;

  public ClientProcess(
    String keyBase64, String serverIp, int serverPort
  ) {
    super(keyBase64);
    this.serverIp = serverIp;
    this.serverPort = serverPort;
  }

  @Override
  public void run() {
    ConsolePrinter.println("Cliente iniciado!");

    Socket serverSocket = connectToServerWithRetry();
    Thread clientThread = new Thread(
      new ClientThread(serverSocket)
    );
    clientThread.start();

    try {
      clientThread.join();
      scanner.close();
    } catch (Exception e) {
      ConsolePrinter.print("Erro interno do cliente!");
    }
  }

  private Socket connectToServerWithRetry() {
    ConsolePrinter.println("Tentando conectar-se ao servidor...");

    Socket serverSocket = null;
    while(serverSocket == null) {
      try {
        serverSocket = new Socket(serverIp, serverPort);
      } catch(Exception exception) {
        waitToReconnect();
      }
    }
    
    ConsolePrinter.println("Servidor conectado com sucesso!\n");
    return serverSocket;
  }

  private static void waitToReconnect() {
    ConsolePrinter.println(
      "Falha ao conectar-se ao servidor, tentando novamente em " +
      WAIT_TIME_TO_TRY_RECONNECTION + " segundos..."
    );

    try {
      TimeUnit.SECONDS.sleep(WAIT_TIME_TO_TRY_RECONNECTION);
    } catch (Exception exception) {
    }
  }
}