package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import dtos.BankAccount;
import utils.ConsolePrinter;

public class ServerProcess {
  List<BankAccount> databaseAccounts;
  private final int port;

  public ServerProcess(
    int port, List<BankAccount> databaseAccounts
  ) {
    this.port = port;
    this.databaseAccounts = databaseAccounts == null ?
      new ArrayList<>() : databaseAccounts;
  }

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
