package simulation;

import java.util.ArrayList;
import java.util.List;

import dtos.account.BankAccount;
import dtos.account.ClientData;
import process.server.ServerProcess;
import utils.PasswordHasher;

public class BankServer {
  public static void main(String[] args) {
    ServerProcess.init(4444, getInitialDatabaseAccounts());
    ServerProcess.run();
  }

  private static List<BankAccount> getInitialDatabaseAccounts() {
    List<BankAccount> initialDatabaseAccounts = new ArrayList<>();

    initialDatabaseAccounts.add(
      new BankAccount("1122", "11112222", 5000.0, 400.0, new ClientData(
        "First person", "111.111.222-22", 
        "Address of first person",
        "(11)9.1111-2222", PasswordHasher.hashAndEncode("11112222")
      ))
    );
    initialDatabaseAccounts.add(
      new BankAccount("1234", "12345678", 9400.0, 850.0, new ClientData(
        "Second person", "123.456.789-00", 
        "Address of second person",
        "(12)9.3456-7890", PasswordHasher.hashAndEncode("12345678")
      ))
    );
    initialDatabaseAccounts.add(
      new BankAccount("4321", "87654321", 2000.0, 258.0, new ClientData(
        "Third person", "987.654.321-00",
        "Address of third person",
        "(98)9.7654-3210", PasswordHasher.hashAndEncode("87654321")
      ))
    );

    return initialDatabaseAccounts;
  }
}
