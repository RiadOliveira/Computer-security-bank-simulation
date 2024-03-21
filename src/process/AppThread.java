package process;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import dtos.DTO;
import security.ObjectPacker;
import security.crypto.AsymmetricKey;
import security.crypto.ComponentSymmetricKeys;
import utils.ConsolePrinter;

public abstract class AppThread extends CommandHandler implements Runnable {
  protected final Socket connectedSocket;
  protected final boolean isServerThread;
  
  protected ObjectInputStream inputStream;
  protected ObjectOutputStream outputStream;

  protected ComponentSymmetricKeys symmetricKeys;
  protected AsymmetricKey connectedComponentPublicKey;

  public AppThread(Socket connectedSocket, boolean isServerThread) {
    this.connectedSocket = connectedSocket;
    this.isServerThread = isServerThread;
    this.symmetricKeys = new ComponentSymmetricKeys();
  }
  
  public abstract void execute();

  @Override
  public void run() {
    try {
      initObjectStreams();
      handleRecognitionCommunication();
      execute();
    } catch (Exception exception) {
      ConsolePrinter.println("Erro interno do processo!");
    }
  }

  protected void sendSecureDTO(DTO dto) throws Exception {
    String packedDTO = ObjectPacker.packObject(
      dto, symmetricKeys,
      AppProcess.getPrivateKey()
    );
    outputStream.writeObject(packedDTO);

    printTransmissionDTO(dto, true);
  }

  protected DTO receiveSecureDTO() throws Exception {
    String packedDTO = (String) inputStream.readObject();
    DTO dto = ObjectPacker.unpackObject(
      packedDTO, symmetricKeys,
      connectedComponentPublicKey
    );

    printTransmissionDTO(dto, false);
    return dto;
  }

  private void printTransmissionDTO(
    DTO dto, boolean isBeingSent
  ) {
    ConsolePrinter.println(
      "Dados " +
      (isBeingSent ? "enviados" : "recebidos") + ":")
    ;
    dto.print();
    ConsolePrinter.println("");
  }

  private void initObjectStreams() throws Exception {
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

  private void handleRecognitionCommunication() throws Exception {
    if(isServerThread) {
      sendPublicKey();
      receivePublicKey();
      generateAndSendSymmetricKeys();
    } else {
      receivePublicKey();
      sendPublicKey();
      receiveSymmetricKeys();
    }

    ConsolePrinter.println("");
  }

  private void sendPublicKey() throws Exception {
    String encodedKey = ObjectPacker.encodeObject(
      AppProcess.getPublicKey()
    );
    outputStream.writeObject(encodedKey);
    ConsolePrinter.println("Chave pública própria enviada.");
  }

  private void receivePublicKey() throws Exception {
    String encodedKey = (String) inputStream.readObject();
    connectedComponentPublicKey = ObjectPacker.decodeObject(
      encodedKey
    );
    ConsolePrinter.println(
      "Chave pública do componente conectado recebida."
    );
  }

  private void generateAndSendSymmetricKeys() throws Exception {
    symmetricKeys = new ComponentSymmetricKeys();

    String packedKeys = ObjectPacker.packSymmetricKeys(
      symmetricKeys, connectedComponentPublicKey
    );
    outputStream.writeObject(packedKeys);

    ConsolePrinter.println(
      "Chaves simétricas próprias de criptografia e hash enviadas."
    );
  }

  private void receiveSymmetricKeys() throws Exception {
    String packedKeys = (String) inputStream.readObject();
    symmetricKeys = ObjectPacker.unpackSymmetricKeys(
      packedKeys, AppProcess.getPrivateKey()
    );

    ConsolePrinter.println(
      "Chaves simétricas de criptografia e hash " +
      "do componente conectado recebidas."
    );
  }
}
