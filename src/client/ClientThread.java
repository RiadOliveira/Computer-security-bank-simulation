package client;

import java.net.Socket;

import process.AppThread;

public class ClientThread extends AppThread {
  public ClientThread(Socket serverSocket) {
    super(serverSocket, false);
  }

  @Override
  public void execute() throws Exception {
  }  
}
