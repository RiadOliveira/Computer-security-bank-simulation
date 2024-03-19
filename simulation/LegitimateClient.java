package simulation;

import java.net.InetAddress;

import process.client.ClientProcess;
import utils.ConsolePrinter;

public class LegitimateClient {
  public static void main(String[] args) {
    try {
      String serverIp = InetAddress.getLocalHost().getHostAddress();
      ClientProcess.init(serverIp, 4444, false);
      
      ClientProcess.run();
    } catch (Exception exception) {
      ConsolePrinter.println("Erro ao iniciar o cliente!");
    }
  }
}
