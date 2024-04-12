package utils;

import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import constants.DefaultIpAndPorts;
import socket.components.SocketComponent;
import socket.data.SocketConnectionData;

public class ConnectionUtils {
  private static final int WAIT_TIME_TO_TRY_RECONNECTION = 3;

  private static final SocketConnectionData gateway = new SocketConnectionData(SocketComponent.GATEWAY,
      DefaultIpAndPorts.IP_ADDRESS,
      DefaultIpAndPorts.GATEWAY_PORT);

  private static final SocketConnectionData firewall = new SocketConnectionData(SocketComponent.FIREWALL,
      DefaultIpAndPorts.IP_ADDRESS,
      DefaultIpAndPorts.FIREWALL_PORT);

  private static final SocketConnectionData authService = new SocketConnectionData(
      SocketComponent.AUTHENTICATION_SERVICE,
      DefaultIpAndPorts.IP_ADDRESS,
      DefaultIpAndPorts.AUTH_SERVICE_PORT);

  private static final SocketConnectionData bankService = new SocketConnectionData(
      SocketComponent.BANK_SERVICE,
      DefaultIpAndPorts.IP_ADDRESS,
      DefaultIpAndPorts.BANK_SERVICE_PORT);

  private static final SocketConnectionData database = new SocketConnectionData(
      SocketComponent.DATABASE,
      DefaultIpAndPorts.IP_ADDRESS,
      DefaultIpAndPorts.DATABASE_PORT);

  public static Future<Socket> asynchronouslyConnectToSocketServerWithRetry(
      String ip, int port) {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    return executor.submit(new Callable<Socket>() {
      @Override
      public Socket call() throws Exception {
        return connectToSocketServerWithRetry(ip, port);
      }
    });
  }

  public static Socket connectToSocketServerWithRetry(String ip, int port) {
    String serverAddress = ip + ":" + port;
    ConsolePrinter.println(
        "Tentando conectar-se ao servidor socket: " + serverAddress);

    Socket socket = null;
    while (socket == null) {
      try {
        socket = new Socket(ip, port);
      } catch (Exception exception) {
        waitToReconnect();
      }
    }

    ConsolePrinter.println(
        "Servidor socket " + serverAddress + " conectado com sucesso!\n");
    return socket;
  }

  private static void waitToReconnect() {
    ConsolePrinter.println(
        "Falha ao conectar-se ao servidor socket, tentando novamente em " +
            WAIT_TIME_TO_TRY_RECONNECTION + " segundos...");

    try {
      TimeUnit.SECONDS.sleep(WAIT_TIME_TO_TRY_RECONNECTION);
    } catch (Exception exception) {
    }
  }

  public static SocketConnectionData getGatewayData() {
    return gateway;
  }

  public static SocketConnectionData getFirewallData() {
    return firewall;
  }

  public static SocketConnectionData getAuthServiceData() {
    return authService;
  }

  public static SocketConnectionData getBankServiceData() {
    return bankService;
  }

  public static SocketConnectionData getDatabaseData() {
    return database;
  }
}