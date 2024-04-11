package simulation;

import java.net.InetAddress;

import process.ClientAttackType;
import process.client.ClientProcess;
import utils.ConsolePrinter;

public class AttackerClient {
  public static void main(String[] args) {
    try {
      String serverIp = InetAddress.getLocalHost().getHostAddress();
      ClientProcess.init(
        serverIp, 4444,
        ClientAttackType.ENCRYPTION_KEY
      );
      
      //ClientProcess.run();
    } catch (Exception exception) {
      ConsolePrinter.println("Erro ao iniciar o cliente!");
    }
  }
}
