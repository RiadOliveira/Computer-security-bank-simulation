package connections;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.generic.ExceptionDTO;
import errors.AppException;
import security.ObjectPacker;
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

  private void handleExecution() {
    boolean socketDisconnected = false;

    while (!socketDisconnected) {
      try {
        execute();
      } catch (Exception exception) {
        socketDisconnected = exception instanceof EOFException;

        if (!socketDisconnected) handleExecutionException(exception);
        else ConsolePrinter.printlnError(
          "Conexão com o socket perdida, finalizando thread...\n"
        );
      }
    }
  }

  protected void executeDefaultExceptionHandling(
    Exception exception
  ) {
    try {
      String errorMessage = exception instanceof AppException ?
        exception.getMessage() : "Falha ao realizar operação!";

      ExceptionDTO exceptionDTO = new ExceptionDTO(errorMessage);
      sendSecureDTO(socketClientComponent, exceptionDTO);
    } catch (Exception e) {
      ConsolePrinter.printlnError("Falha ao se comunicar com o componente!");
    }
  }

  protected void sendSecureDTO(
    SocketComponent component, DTO dto
  ) throws Exception {
    sendSecureDTO(component, 0, dto);
  }

  protected void sendSecureDTO(
    SocketComponent component, int replicaIndex, DTO dto
  ) throws Exception {
    var socketData = getConnectedSocketData(component, replicaIndex);
    ObjectOutputStream outputStream = socketData.getOutputStream();

    String packedDTO = ObjectPacker.packObject(
      dto, socketData.getSymmetricKeys(),
      SocketProcess.getPrivateKey()
    );
    outputStream.writeObject(packedDTO);

    printTransmissionDTO(dto, component, replicaIndex, true);
  }

  protected DTO receiveSecureDTO(
    SocketComponent component
  ) throws Exception {
    return receiveSecureDTO(component, 0);
  }

  protected DTO receiveSecureDTO(
    SocketComponent component, int replicaIndex
  ) throws Exception {
    var socketData = getConnectedSocketData(component, replicaIndex);
    ObjectInputStream inputStream = socketData.getInputStream();

    String packedDTO = (String) inputStream.readObject();
    DTO dto = ObjectPacker.unpackObject(
      packedDTO, socketData.getSymmetricKeys(),
      socketData.getPublicKey()
    );

    printTransmissionDTO(dto, component, replicaIndex, false);
    return dto;
  }

  // protected void sendSecureDTO(
  //   SocketComponent component, DTO dto
  // ) throws Exception {
  //   sendSecureDTO(component, 0, dto);
  // }

  // protected void sendSecureDTO(
  //   SocketComponent component, int replicaIndex, DTO dto
  // ) throws Exception {
  //   var socketData = getConnectedSocketData(component, replicaIndex);
  //   ObjectOutputStream outputStream = socketData.getOutputStream();

  //   String encodedDTO = ObjectPacker.encodeObject(dto);
  //   outputStream.writeObject(encodedDTO);

  //   printTransmissionDTO(dto, component, replicaIndex, true);
  // }

  // protected DTO receiveSecureDTO(SocketComponent component) throws Exception {
  //   return receiveSecureDTO(component, 0);
  // }

  // protected DTO receiveSecureDTO(
  //   SocketComponent component, int replicaIndex
  // ) throws Exception {
  //   var socketData = getConnectedSocketData(component, replicaIndex);
  //   ObjectInputStream inputStream = socketData.getInputStream();

  //   String encodedDTO = (String) inputStream.readObject();
  //   DTO dto = ObjectPacker.decodeObject(encodedDTO);

  //   printTransmissionDTO(dto, component, replicaIndex, false);
  //   return dto;
  // }

  protected SecretKey getComponentHashKey(SocketComponent component) {
    return getComponentHashKey(component, 0);
  }

  protected SecretKey getComponentHashKey(
    SocketComponent component, int replicaIndex
  ) {
    return connectedSockets.get(component).get(
      replicaIndex
    ).getSymmetricKeys().getHashKey();
  }

  protected int getComponentReplicasQuantity(SocketComponent component) {
    return connectedSockets.get(component).size();
  }

  private synchronized void printTransmissionDTO(
    DTO dto, SocketComponent component,
    int replicaIndex, boolean isBeingSent
  ) {
    ConsolePrinter.println(
      "Dados " + (isBeingSent ? "enviados para " : "recebidos do ") +
      component + " (Réplica " + replicaIndex + "):"
    );
    dto.print();
    ConsolePrinter.println("");
  }

  private SocketData getConnectedSocketData(
    SocketComponent component, int replicaIndex
  ) {
    var componentSockets = connectedSockets.get(component);
    if(componentSockets == null) return null;

    return componentSockets.get(replicaIndex);
  }
}
