package simulation;

import java.net.InetAddress;

import process.client.ClientProcess;
import utils.ConsolePrinter;

public class LegitimateClient {
  private static String secretKeyBase64 = "HZvOpm3s2pquuMULrHxAkw==";
  
  public static void main(String[] args) {
    try {
      String serverIp = InetAddress.getLocalHost().getHostAddress();
      ClientProcess client = new ClientProcess(
        secretKeyBase64, serverIp, 4444
      );
      
      client.run();
    } catch (Exception exception) {
      ConsolePrinter.println("Erro ao iniciar o cliente!");
    }
  }
}
