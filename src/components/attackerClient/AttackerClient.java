package components.attackerClient;

import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import dtos.auth.AuthenticatedDTO;
import dtos.generic.MessageDTO;
import utils.ConsolePrinter;

public class AttackerClient extends BaseAttackerClient {
  public AttackerClient(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
  }

  @Override
  protected void execute() throws Exception {
    DTO attackDTO = new AuthenticatedDTO(
      "ATTACKER_TOKEN", new MessageDTO("DATABASE_MESSAGE")
    ).setOperation(RemoteOperation.BACKDOOR_ACCESS);

    sendSecureDTO(SocketComponent.BANK_DATABASE, attackDTO);
    receiveSecureDTO(SocketComponent.BANK_DATABASE);

    ConsolePrinter.println("Ataque efetuado com sucesso!");
    System.exit(0);
  }

  @Override
  protected void handleExecutionException(Exception exception) {
    executeDefaultExceptionHandling(exception);
  }
}
