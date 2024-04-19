package utils;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import connections.components.SocketComponent;
import connections.data.SocketConnectionData;

public class ConnectionUtils {
  public static final String ANY_IP_ADDRESS = "0.0.0.0";
  private static final String LOCAL_HOST = "127.0.0.1";

  private static final int WAIT_TIME_TO_TRY_RECONNECTION = 3;

  private static final Map<
    SocketComponent, SocketConnectionData
  > COMPONENTS_CONNECTION_DATA = new HashMap<>();

  static {
    final int INITIAL_PORT = 10000;
    SocketComponent allComponents[] = SocketComponent.values();

    for(int ind = 0; ind < allComponents.length; ind++) {
      SocketComponent component = allComponents[ind];

      int serverPort = (ind + 1) * INITIAL_PORT;
      var connectionData = new SocketConnectionData(
        component, LOCAL_HOST, serverPort
      );

      COMPONENTS_CONNECTION_DATA.put(component, connectionData);
    }

    setComponentAddress(SocketComponent.CLIENT, ConnectionUtils.ANY_IP_ADDRESS);
    setComponentAddress(SocketComponent.FIREWALL, LOCAL_HOST);
    setComponentAddress(SocketComponent.GATEWAY, LOCAL_HOST);
    setComponentAddress(SocketComponent.AUTHENTICATION_SERVICE, LOCAL_HOST);
    setComponentAddress(SocketComponent.BANK_SERVICE, LOCAL_HOST);
    setComponentAddress(SocketComponent.BANK_DATABASE, LOCAL_HOST);
  }

  public static Future<Socket> asynchronouslyConnectToSocketServerWithRetry(
    String address, int port
  ) {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    return executor.submit(new Callable<Socket>() {
      @Override
      public Socket call() throws Exception {
        return connectToSocketServerWithRetry(address, port);
      }
    });
  }

  public static Socket connectToSocketServerWithRetry(
    String address, int port
  ) {
    String serverIp = address + ":" + port;
    ConsolePrinter.println(
      "Tentando conectar-se ao servidor socket " + serverIp
    );

    Socket socket = null;
    while (socket == null) {
      try {
        socket = new Socket(address, port);
      } catch(Exception exception) {
        waitToReconnect();
      }
    }

    ConsolePrinter.println(
      "Servidor socket " + serverIp + " conectado com sucesso!\n"
    );
    return socket;
  }

  private static void waitToReconnect() {
    ConsolePrinter.println("");
    ConsolePrinter.printlnError(
      "Falha ao conectar-se ao servidor socket, tentando novamente em " +
      WAIT_TIME_TO_TRY_RECONNECTION + " segundos..."
    );

    try {
      TimeUnit.SECONDS.sleep(WAIT_TIME_TO_TRY_RECONNECTION);
    } catch(Exception exception) {}
  }

  public static String getComponentAddress(SocketComponent component) {
    return getComponentSocketConnectionData(component).getAddress();
  }

  public static int getComponentPort(SocketComponent component) {
    return getComponentPort(component, 0);
  }

  public static int getComponentPort(
    SocketComponent component, int replicaIndex
  ) {
    int serverPort = getComponentSocketConnectionData(
      component
    ).getPort();
    return serverPort + replicaIndex;
  }

  private static void setComponentAddress(
    SocketComponent component, String address
  ) {
    getComponentSocketConnectionData(component).setAddress(address);
  }

  public static SocketConnectionData getComponentSocketConnectionData(
    SocketComponent component
  ) {
    return COMPONENTS_CONNECTION_DATA.get(component);
  }
}