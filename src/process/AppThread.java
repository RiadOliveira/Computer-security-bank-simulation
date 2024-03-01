package process;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.crypto.SecretKey;

import dtos.DTO;
import security.SecureDTOPacker;
import utils.ConsolePrinter;

public abstract class AppThread extends CommandHandler implements Runnable {
  protected SecretKey authKey = null;

  protected final Socket connectedSocket;
  protected final boolean isServerThread;
  
  protected ObjectInputStream inputStream;
  protected ObjectOutputStream outputStream;

  public AppThread(Socket connectedSocket, boolean isServerThread) {
    this.connectedSocket = connectedSocket;
    this.isServerThread = isServerThread;
  }
  
  protected abstract void execute();

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

  protected void sendDTO(DTO dto) throws Exception {
    String packedDTO = SecureDTOPacker.packDTO(
      dto, authKey, AppProcess.getKey()
    );

    outputStream.writeObject(packedDTO);
  }

  protected<T> T receiveDTO() throws Exception {
    String encodedData = (String) inputStream.readObject();
    
    return SecureDTOPacker.unpackDTO(
      encodedData, authKey, AppProcess.getKey()
    );
  }
}
