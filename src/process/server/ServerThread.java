package process.server;

import java.net.Socket;

import process.AppThread;

public class ServerThread extends AppThread {
  public ServerThread(Socket clientSocket) {
    super(clientSocket, true);
  }

  @Override
  public void execute() throws Exception {
    
  }
}
