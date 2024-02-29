package process.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import dtos.BankAccount;
import process.AppProcess;
import utils.ConsolePrinter;

public class ServerProcess extends AppProcess {
  List<BankAccount> databaseAccounts;
  private final int port;

  public ServerProcess(
    String keyBase64, int port,
    List<BankAccount> databaseAccounts
  ) {
    super(keyBase64);

    this.port = port;
    this.databaseAccounts = databaseAccounts == null ?
      new ArrayList<>() : databaseAccounts;
  }

  @Override
  public void run() {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      ConsolePrinter.println("Servidor iniciado!");
  
      while (true) {
        Socket clientSocket = serverSocket.accept();
        Thread serverThread = new Thread(
          new ServerThread(clientSocket)
        );
        serverThread.start();
      }
    } catch (Exception exception) {
      ConsolePrinter.println("Erro interno do servidor!");
    }
  }
}
