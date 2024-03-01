package simulation;

import java.util.ArrayList;
import java.util.List;

import dtos.account.BankAccount;
import dtos.account.ClientData;
import process.server.ServerProcess;
import utils.PasswordHasher;

public class BankServer {
  private static String secretKeyBase64 = "HZvOpm3s2pquuMULrHxAkw==";

  public static void main(String[] args) {
    ServerProcess.init(
      secretKeyBase64, 4444, getInitialDatabaseAccounts()
    );
    ServerProcess.run();
  }

  private static List<BankAccount> getInitialDatabaseAccounts() {
    List<BankAccount> initialDatabaseAccounts = new ArrayList<>();

    initialDatabaseAccounts.add(
      new BankAccount("0011", "00001111", new ClientData(
        "First person", "000.000.111-11", 
        "Address of first person",
        "(00)9.0000-1111", PasswordHasher.hashAndEncode("00001111")
      ))
    );
    initialDatabaseAccounts.add(
      new BankAccount("0123", "01234567", new ClientData(
        "Second person", "012.345.678-99", 
        "Address of second person",
        "(01)9.2345-6789", PasswordHasher.hashAndEncode("01234567")
      ))
    );
    initialDatabaseAccounts.add(
      new BankAccount("3210", "76543210", new ClientData(
        "Third person", "987.654.321-00",
        "Address of third person",
        "(98)9.7654-3210", PasswordHasher.hashAndEncode("76543210")
      ))
    );

    return initialDatabaseAccounts;
  }
}
