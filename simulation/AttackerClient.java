package simulation;

import java.net.InetAddress;

import process.client.ClientProcess;
import utils.ConsolePrinter;

public class AttackerClient {
  private static String validSecretKey = "HZvOpm3s2pquuMULrHxAkw==";
  //private static String invalidSecretKey = "rXfim1WFRurBw0i97TdIzQ==";

  public static void main(String[] args) {
    try {
      String serverIp = InetAddress.getLocalHost().getHostAddress();
      ClientProcess.init(validSecretKey, serverIp, 4444, true);
      
      ClientProcess.run();
    } catch (Exception exception) {
      ConsolePrinter.println("Erro ao iniciar o cliente!");
    }
  }
}
