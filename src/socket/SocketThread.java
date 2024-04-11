package socket;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import dtos.DTO;
import security.ObjectPacker;
import socket.components.SocketComponent;
import socket.data.SocketData;
import utils.ConsolePrinter;

public abstract class SocketThread implements Runnable {
  private final Map<SocketComponent, List<SocketData>> connectedSockets;
  private final SocketComponent socketClientComponent;
  
  public SocketThread(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    this.connectedSockets = connectedSockets;
    this.socketClientComponent = socketClientComponent;
  }
  
  protected abstract void execute() throws Exception;
  protected abstract void handleExecutionException(Exception exception);

  @Override
  public void run() {
    try {
      SocketsInitializer.initializeConnectedSockets(
        connectedSockets, socketClientComponent
      );
      handleExecution();
    } catch (Exception exception) {
      ConsolePrinter.println("Erro interno da thread!");
    }
  }

  protected void sendSecureDTO(
    DTO dto, SocketComponent component
  ) throws Exception {
    sendSecureDTO(dto, component, 0);
  }

  protected void sendSecureDTO(
    DTO dto, SocketComponent component, int socketIndex
  ) throws Exception {
    ObjectOutputStream outputStream = getSocketData(
      component, socketIndex
    ).getOutputStream();
    var socketKeys = SocketProcess.getConnectedSocketKeys(
      component, socketIndex
    );

    String packedDTO = ObjectPacker.packObject(
      dto, socketKeys.getSymmetricKeys(),
      SocketProcess.getPrivateKey()
    );
    outputStream.writeObject(packedDTO);

    printTransmissionDTO(dto, true);
  }

  protected DTO receiveSecureDTO(
    SocketComponent component
  ) throws Exception {
    return receiveSecureDTO(component);
  }

  protected DTO receiveSecureDTO(
    SocketComponent component, int socketIndex
  ) throws Exception {
    ObjectInputStream inputStream = getSocketData(
      component, socketIndex
    ).getInputStream();
    var socketKeys = SocketProcess.getConnectedSocketKeys(
      component, socketIndex
    );

    String packedDTO = (String) inputStream.readObject();
    DTO dto = ObjectPacker.unpackObject(
      packedDTO, socketKeys.getSymmetricKeys(),
      socketKeys.getPublicKey()
    );

    printTransmissionDTO(dto, false);
    return dto;
  }

  private SocketData getSocketData(
    SocketComponent component, int socketIndex
  ) {
    var socketsData = connectedSockets.get(component);
    if(socketsData == null) return null;

    return socketsData.get(socketIndex);
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

  private void handleExecution() {
    boolean socketDisconnected = false;

    while(!socketDisconnected) {
      try {
        execute();
      } catch(Exception exception) {
        socketDisconnected = exception instanceof EOFException;

        if(!socketDisconnected) handleExecutionException(exception);
        else ConsolePrinter.printlnError(
          "Conexão com o socket perdida, finalizando thread...\n"
        );
      }
    }
  }
}
