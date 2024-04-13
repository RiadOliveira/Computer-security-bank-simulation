package connections.data;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import security.crypto.AsymmetricKey;
import security.crypto.ComponentSymmetricKeys;

public class SocketData {
  private final Socket socket;
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;

  private ComponentSymmetricKeys symmetricKeys;
  private AsymmetricKey publicKey;

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

  public ComponentSymmetricKeys getSymmetricKeys() {
    return symmetricKeys;
  }

  public void setSymmetricKeys(ComponentSymmetricKeys symmetricKeys) {
    this.symmetricKeys = symmetricKeys;
  }

  public AsymmetricKey getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(AsymmetricKey publicKey) {
    this.publicKey = publicKey;
  }
}
