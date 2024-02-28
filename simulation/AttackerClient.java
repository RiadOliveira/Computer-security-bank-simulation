package simulation;

import java.net.InetAddress;

import client.ClientProcess;
import utils.ConsolePrinter;

public class AttackerClient {
  public static void main(String[] args) {
    try {
      String serverIp = InetAddress.getLocalHost().getHostAddress();
      ClientProcess client = new ClientProcess(serverIp, 4444);
      
      client.run();
    } catch (Exception exception) {
      ConsolePrinter.println("Erro ao iniciar o cliente!");
    }
  }
}
