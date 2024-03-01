package process.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import dtos.account.BankAccount;
import process.AppProcess;
import utils.ConsolePrinter;

public class ServerProcess extends AppProcess {
  private static List<BankAccount> databaseAccounts;
  private static int port;

  public static void init(
    String keyBase64, int port,
    List<BankAccount> databaseAccounts
  ) {
    initKey(keyBase64);

    ServerProcess.port = port;
    ServerProcess.databaseAccounts = databaseAccounts == null ?
      new ArrayList<>() : databaseAccounts;
  }

  public static void run() {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      ConsolePrinter.println("Servidor iniciado!");
  
      while (true) {
        Socket clientSocket = serverSocket.accept();
        Thread serverThread = new Thread(
          new ServerThread(clientSocket)
        );

        ConsolePrinter.println(
          "Servidor conectou-se a um novo cliente!"
        );
        serverThread.start();
      }
    } catch (Exception exception) {
      ConsolePrinter.println("Erro interno do servidor!");
    }
  }

  public static void addDatabaseAccount(BankAccount newAccount) {
    databaseAccounts.add(newAccount);
  }

  public static boolean existsAccountWithCpf(String cpf) {
    for(BankAccount account : databaseAccounts) {
      boolean equalCpf = account.getClientData().getCpf().equals(cpf);
      if(equalCpf) return true;
    }

    return false;
  }

  public static BankAccount findAccountByAgencyAndNumber(
    String agency, String accountNumber
  ) {
    for(BankAccount account : databaseAccounts) {
      boolean equalAgency = account.getAgency().equals(agency);
      boolean equalNumber = account.getAccountNumber().equals(accountNumber);

      if(equalAgency && equalNumber) return account;
    }

    return null;
  }
}
