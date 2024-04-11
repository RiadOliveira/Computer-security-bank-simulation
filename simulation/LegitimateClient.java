package simulation;

import java.net.InetAddress;

import process.ClientAttackType;
import process.client.ClientProcess;
import process.client.ClientThread;
import socket.components.SocketClient;
import socket.components.SocketComponent;
import socket.data.SocketConnectionData;
import utils.ConsolePrinter;

public class LegitimateClient {
  public static void main(String[] args) {
    try {
      String serverIp = InetAddress.getLocalHost().getHostAddress();
      ClientProcess.init(serverIp, 4444, ClientAttackType.NONE);
      //ClientProcess.run();

      SocketConnectionData connectionData = new SocketConnectionData(
        SocketComponent.STORE_SERVICE, serverIp, 4000
      );
      SocketClient client = new SocketClient(ClientThread.class, connectionData);
      client.run();
    } catch (Exception exception) {
      ConsolePrinter.println("Erro ao iniciar o cliente!");
    }
  }
}
