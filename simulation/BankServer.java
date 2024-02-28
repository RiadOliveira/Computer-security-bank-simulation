package simulation;

import java.util.ArrayList;
import java.util.List;

import dtos.BankAccount;
import dtos.ClientData;
import server.ServerProcess;

public class BankServer {
  public static void main(String[] args) {
    ServerProcess server = new ServerProcess(
      4444, getInitialDatabaseAccounts()
    );
    server.run();
  }

  private static List<BankAccount> getInitialDatabaseAccounts() {
    List<BankAccount> initialDatabaseAccounts = new ArrayList<>();

    initialDatabaseAccounts.add(
      new BankAccount("0011", "00001111", new ClientData(
        "First person", "000.000.111-11", 
        "Address of first person",
        "(00)9.0000-1111"
      ))
    );
    initialDatabaseAccounts.add(
      new BankAccount("0123", "01234567", new ClientData(
        "Second person", "012.345.678-99", 
        "Address of second person",
        "(01)9.2345-6789"
      ))
    );
    initialDatabaseAccounts.add(
      new BankAccount("3210", "76543210", new ClientData(
        "Third person", "987.654.321-00", 
        "Address of third person",
        "(98)9.7654-3210"
      ))
    );

    return initialDatabaseAccounts;
  }
}
