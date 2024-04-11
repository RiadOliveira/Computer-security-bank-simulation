package socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import security.ObjectPacker;
import security.crypto.AsymmetricKey;
import security.crypto.ComponentSymmetricKeys;
import socket.components.SocketComponent;
import socket.data.SocketData;
import socket.data.SocketKeys;
import utils.ConsolePrinter;

public class SocketsInitializer {
  public static void initializeConnectedSockets(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) throws Exception {
    for (var entry : connectedSockets.entrySet()) {
      SocketComponent component = entry.getKey();
      List<SocketData> socketsData = entry.getValue();
      boolean isServerSocket = !component.equals(socketClientComponent);

      initializeComponentSockets(component, socketsData, isServerSocket);
    }
  }

  private static void initializeComponentSockets(
    SocketComponent component, List<SocketData> socketsData,
    boolean isServerSocket
  ) throws Exception {
    for (int ind=0 ; ind<socketsData.size() ; ind++) {
      SocketData data = socketsData.get(ind);
      initObjectStreams(data, isServerSocket);

      SocketKeys keys = SocketProcess.getConnectedSocketKeys(component, ind);
      if(keys == null) {
        SocketKeys keysObject = new SocketKeys();
        SocketProcess.addSocketComponentKeys(component, keysObject);
        handleKeysExchange(keysObject, data, isServerSocket);
      }
    }
  }

  private static void initObjectStreams(
    SocketData socketData, boolean isServerSocket
  ) throws Exception {
    ObjectInputStream inputStream = socketData.getInputStream();
    ObjectOutputStream outputStream = socketData.getOutputStream();

    boolean inputStreamIsNull = inputStream == null;
    boolean outputStreamIsNull = outputStream == null;
    if(!inputStreamIsNull && !outputStreamIsNull) return;

    if(!inputStreamIsNull) inputStream.close();
    if(!outputStreamIsNull) outputStream.close();

    Socket socket = socketData.getSocket();
    if(isServerSocket) {
      socketData.setInputStream(new ObjectInputStream(socket.getInputStream()));
      socketData.setOutputStream(new ObjectOutputStream(socket.getOutputStream()));
    } else {
      socketData.setOutputStream(new ObjectOutputStream(socket.getOutputStream()));
      socketData.setInputStream(new ObjectInputStream(socket.getInputStream()));
    }
  }

  private static void handleKeysExchange(
    SocketKeys keysObject, SocketData socketData,
    boolean isServerSocket
  ) throws Exception {
    ObjectInputStream inputStream = socketData.getInputStream();
    ObjectOutputStream outputStream = socketData.getOutputStream();

    if(isServerSocket) {
      sendPublicKey(outputStream);
      receivePublicKey(keysObject, inputStream);
      setAndSendSymmetricKeys(keysObject, outputStream);
    } else {
      receivePublicKey(keysObject, inputStream);
      sendPublicKey(outputStream);
      receiveSymmetricKeys(keysObject, inputStream);
    }

    ConsolePrinter.println("");
  }

  private static void sendPublicKey(
    ObjectOutputStream socketOutputStream
  ) throws Exception {
    String encodedKey = ObjectPacker.encodeObject(
      SocketProcess.getPublicKey()
    );
    socketOutputStream.writeObject(encodedKey);
    ConsolePrinter.println("Chave pública própria enviada.");
  }

  private static void receivePublicKey(
    SocketKeys keysObject, ObjectInputStream socketInputStream
  ) throws Exception {
    String encodedKey = (String) socketInputStream.readObject();
    AsymmetricKey publicKey = ObjectPacker.decodeObject(
      encodedKey
    );

    ConsolePrinter.println(
      "Chave pública do componente conectado recebida."
    );
    keysObject.setPublicKey(publicKey);
  }

  private static void setAndSendSymmetricKeys(
    SocketKeys keysObject, ObjectOutputStream outputStream
  ) throws Exception {
    keysObject.setSymmetricKeys(new ComponentSymmetricKeys());

    String packedKeys = ObjectPacker.packSymmetricKeys(
      keysObject.getSymmetricKeys(), keysObject.getPublicKey()
    );
    outputStream.writeObject(packedKeys);

    ConsolePrinter.println(
      "Chaves simétricas próprias de criptografia e hash enviadas."
    );
  }

  private static void receiveSymmetricKeys(
    SocketKeys keysObject, ObjectInputStream inputStream
  ) throws Exception {
    String packedKeys = (String) inputStream.readObject();
    ComponentSymmetricKeys symmetricKeys = ObjectPacker.unpackSymmetricKeys(
      packedKeys, SocketProcess.getPrivateKey()
    );

    ConsolePrinter.println(
      "Chaves simétricas de criptografia e hash " +
      "do componente conectado recebidas."
    );
    keysObject.setSymmetricKeys(symmetricKeys);
  }
}
