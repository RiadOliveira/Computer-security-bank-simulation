package process;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import dtos.DTO;
import utils.ConsolePrinter;
import utils.CryptoUtils;
import utils.SerializationUtils;
import utils.CryptoUtils.EncryptionAlgorithm;

public abstract class AppThread implements Runnable {
  protected final Socket connectedSocket;
  protected final boolean isServerThread;
  
  protected ObjectInputStream inputStream;
  protected ObjectOutputStream outputStream;

  public AppThread(Socket connectedSocket, boolean isServerThread) {
    this.connectedSocket = connectedSocket;
    this.isServerThread = isServerThread;
  }

  public abstract void execute() throws Exception;

  @Override
  public void run() {
    try {
      initObjectStreams();
      execute();
      closeObjectStreams();
    } catch (Exception exception) {
      ConsolePrinter.println("Erro interno do processo!");
    }
  }

  protected void initObjectStreams() throws Exception {
    boolean inputStreamIsNull = inputStream == null;
    boolean outputStreamIsNull = outputStream == null;
    if(!inputStreamIsNull && !outputStreamIsNull) return;

    if(!inputStreamIsNull) inputStream.close();
    if(!outputStreamIsNull) outputStream.close();

    if(isServerThread) {
      inputStream = new ObjectInputStream(connectedSocket.getInputStream());
      outputStream = new ObjectOutputStream(connectedSocket.getOutputStream());
    } else {
      outputStream = new ObjectOutputStream(connectedSocket.getOutputStream());
      inputStream = new ObjectInputStream(connectedSocket.getInputStream());
    }
  }

  protected void closeObjectStreams() throws Exception {
    if(inputStream != null) inputStream.close();
    if(outputStream != null) outputStream.close();
  }

  protected void sendDTO(DTO dto, EncryptionAlgorithm encryptionAlgorithm) {
    byte[] serializedBytes = SerializationUtils.serializeObject(dto);
    byte[] encryptedBytes = CryptoUtils.encryptData(
      encryptionAlgorithm, serializedBytes, AppProcess.getKey()
    );
    String encodedData = CryptoUtils.encodeBase64(encryptedBytes);

    try {
      outputStream.writeObject(encodedData);
    } catch (Exception exception) {
      ConsolePrinter.print("Erro ao enviar o DTO!");
    }
  }

  @SuppressWarnings("unchecked")
  protected<T> T receiveDTO(EncryptionAlgorithm encryptionAlgorithm) {
    try {
      String encodedData = (String) inputStream.readObject();

      byte[] decodedBytes = CryptoUtils.decodeBase64(encodedData);
      byte[] decryptedBytes = CryptoUtils.decryptData(
        encryptionAlgorithm, decodedBytes, AppProcess.getKey()
      );

      return (T) SerializationUtils.deserializeObject(decryptedBytes);
    } catch (Exception exception) {
      ConsolePrinter.print("Erro ao receber o DTO!");
      return null;
    }
  }
}
