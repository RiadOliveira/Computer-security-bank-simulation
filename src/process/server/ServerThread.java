package process.server;

import java.net.Socket;

import dtos.account.BankAccount;
import process.AppThread;

public class ServerThread extends AppThread {
  protected BankAccount clientAccount = null;
  
  public ServerThread(Socket clientSocket) {
    super(clientSocket, true);
  }

  @Override
  public void execute() throws Exception {
    
  }
}
