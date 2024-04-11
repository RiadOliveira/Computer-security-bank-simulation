package utils;

import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ConnectionUtils {
  private static final int WAIT_TIME_TO_TRY_RECONNECTION = 3;

  public static final int GATEWAY_PORT = 4444;
  public static final int FIREWALL_PORT = 5555;
  public static final int AUTH_SERVICE_PORT = 6666;
  public static final int BANK_SERVICE_PORT = 7777;
  public static final int DATABASE_PORT = 8888;

  public static Future<Socket> asynchronouslyConnectToSocketServerWithRetry(
    String ip, int port
  ) {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    return executor.submit(new Callable<Socket>() {
      @Override
      public Socket call() throws Exception {
        return connectToSocketServerWithRetry(ip, port);
      }
    });
  }

  public static Socket connectToSocketServerWithRetry(String ip, int port) {
    ConsolePrinter.println("Tentando conectar-se ao servidor socket...");

    Socket socket = null;
    while (socket == null) {
      try {
        socket = new Socket(ip, port);
      } catch (Exception exception) {
        waitToReconnect();
      }
    }

    ConsolePrinter.println("Servidor socket conectado com sucesso!\n");
    return socket;
  }

  private static void waitToReconnect() {
    ConsolePrinter.println(
      "Falha ao conectar-se ao servidor socket, tentando novamente em " +
      WAIT_TIME_TO_TRY_RECONNECTION + " segundos..."
    );

    try {
      TimeUnit.SECONDS.sleep(WAIT_TIME_TO_TRY_RECONNECTION);
    } catch (Exception exception) {}
  }
}