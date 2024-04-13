package utils;

import java.net.InetSocketAddress;
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
    SocketComponent, ComponentConnectionData
  > COMPONENTS_CONNECTION_DATA = new HashMap<>();

  static {
    final int INITIAL_PORT = 10000;
    SocketComponent allComponents[] = SocketComponent.values();

    for(int ind=0 ; ind<allComponents.length ; ind++) {
      SocketComponent component = allComponents[ind];

      int portToConnectToServers = (ind+1) * INITIAL_PORT;
      var socketConnectionData = new SocketConnectionData(
        component, LOCAL_HOST, portToConnectToServers
      );

      var connectionData = new ComponentConnectionData(
        portToConnectToServers, socketConnectionData
      );
      COMPONENTS_CONNECTION_DATA.put(component, connectionData);
    }

    setComponentAddress(SocketComponent.CLIENT, ConnectionUtils.ANY_IP_ADDRESS);
    setComponentAddress(SocketComponent.GATEWAY, LOCAL_HOST);
    setComponentAddress(SocketComponent.FIREWALL, LOCAL_HOST);
    setComponentAddress(SocketComponent.AUTHENTICATION_SERVICE, LOCAL_HOST);
    setComponentAddress(SocketComponent.BANK_SERVICE, LOCAL_HOST);
    setComponentAddress(SocketComponent.BANK_DATABASE, LOCAL_HOST);
  }

  public static Future<Socket> asynchronouslyConnectToSocketServerWithRetry(
    String serverIp, int serverPort, int clientPort
  ) {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    return executor.submit(new Callable<Socket>() {
      @Override
      public Socket call() throws Exception {
        return connectToSocketServerWithRetry(
          serverIp, serverPort, clientPort
        );
      }
    });
  }

  public static Socket connectToSocketServerWithRetry(
    String serverIp, int serverPort, int thisClientPort
  ) {
    String serverAddress = serverIp + ":" + serverPort;
    ConsolePrinter.println(
      "Tentando conectar-se ao servidor socket: " + serverAddress
    );

    Socket socket = null;
    while (socket == null) {
      try {
        socket = new Socket();
        socket.bind(new InetSocketAddress(thisClientPort));
        socket.connect(new InetSocketAddress(serverIp, serverPort));
      } catch (Exception exception) {
        socket = null;
        waitToReconnect();
      }
    }

    ConsolePrinter.println(
      "Servidor socket " + serverAddress +
      " conectado com sucesso!\n"
    );
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

  public static SocketConnectionData getComponentSocketConnectionData(
    SocketComponent component
  ) {
    return COMPONENTS_CONNECTION_DATA.get(component).getSocketConnectionData();
  }

  public static String getComponentAddress(SocketComponent component) {
    return COMPONENTS_CONNECTION_DATA.get(component).
      getSocketConnectionData().getAddress();
  }

  public static int getComponentServerPort(SocketComponent component) {
    return getComponentServerPort(component, 0);
  }

  public static int getComponentServerPort(
    SocketComponent component, int replicaIndex
  ) {
    int clientPort = getComponentClientPort(component);
    return clientPort + (replicaIndex + 1);
  }

  public static int getComponentClientPort(SocketComponent component) {
    return COMPONENTS_CONNECTION_DATA.get(component).getPortToConnectToServers();
  }

  private static void setComponentAddress(SocketComponent component, String address) {
    COMPONENTS_CONNECTION_DATA.get(component).getSocketConnectionData().setAddress(address);
  }
}