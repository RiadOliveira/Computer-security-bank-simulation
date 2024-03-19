package process.client;

import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import process.AppProcess;
import utils.ConsolePrinter;

public class ClientProcess extends AppProcess {
  public static final Scanner scanner = new Scanner(System.in);
  private static final int WAIT_TIME_TO_TRY_RECONNECTION = 3;

  private static boolean isAttacker = false;

  private static String serverIp;
  private static int serverPort;

  public static void init(
    String serverIp, int serverPort, boolean isAttacker
  ) {
    initAsymmetricKeyPair();

    ClientProcess.serverIp = serverIp;
    ClientProcess.serverPort = serverPort;
    ClientProcess.isAttacker = isAttacker;
  }

  public static void run() {
    ConsolePrinter.println("Cliente iniciado!\n");

    Socket serverSocket = connectToServerWithRetry();
    Thread clientThread = new Thread(
      new ClientThread(serverSocket)
    );
    clientThread.start();

    try {
      clientThread.join();
      scanner.close();
    } catch (Exception exception) {
      ConsolePrinter.print("Erro interno do cliente!");
    }
  }

  private static Socket connectToServerWithRetry() {
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
    } catch (Exception exception) {}
  }

  public static boolean isAttacker() {
    return isAttacker;
  }
}