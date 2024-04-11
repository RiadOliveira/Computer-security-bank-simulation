package socket.data;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketData {
  private final Socket socket;
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;

  public SocketData(Socket socket) {
    this.socket = socket;
  }

  public Socket getSocket() {
    return socket;
  }

  public ObjectInputStream getInputStream() {
    return inputStream;
  }

  public void setInputStream(ObjectInputStream inputStream) {
    this.inputStream = inputStream;
  }

  public ObjectOutputStream getOutputStream() {
    return outputStream;
  }

  public void setOutputStream(ObjectOutputStream outputStream) {
    this.outputStream = outputStream;
  }
}
