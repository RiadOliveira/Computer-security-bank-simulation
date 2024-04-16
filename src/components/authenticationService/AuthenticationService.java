package components.authenticationService;

import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import dtos.auth.AuthRequest;
import dtos.auth.AuthResponse;
import dtos.generic.ExceptionDTO;
import dtos.user.UserData;
import utils.Hasher;
import utils.ObjectConverter;

public class AuthenticationService extends BaseAuthenticationService {
  public AuthenticationService(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
  }

  @Override
  protected void execute() throws Exception {
    DTO receivedDTO = receiveSecureDTO(SocketComponent.GATEWAY);
    RemoteOperation operation = receivedDTO.getOperation();

    DTO responseDTO = operationHandlers.get(operation).run(
      receivedDTO
    );
    sendSecureDTO(SocketComponent.GATEWAY, responseDTO);
  }

  @Override
  protected void handleExecutionException(Exception exception) {
    executeDefaultExceptionHandling(exception);
  }

  @Override
  protected DTO createAccount(DTO user) throws Exception {
    UserData parsedDTO = ObjectConverter.convert(user);
    if (parsedDTO == null) {
      return new ExceptionDTO("Instância de DTO inválida!");
    }

    boolean existsByCpf = findByCpf(parsedDTO.getCpf()) != null;
    if (existsByCpf) {
      return new ExceptionDTO("Um usuário de mesmo cpf já existe!");
    }

    parsedDTO.generateAndSetId();
    parsedDTO.setPassword(
      Hasher.hashAndEncode(parsedDTO.getPassword())
    );
    userDatabase.add(parsedDTO);

    return parsedDTO;
  }

  @Override
  protected DTO authenticate(DTO authData) throws Exception {
    AuthRequest parsedDTO = ObjectConverter.convert(authData);
    if (parsedDTO == null) {
      return new ExceptionDTO("Instância de DTO inválida!");
    }

    UserData findedAccount = findByCpf(parsedDTO.getCpf());
    if(findedAccount == null) {
      return new ExceptionDTO("Conta especificada não encontrada!");
    }

    boolean correctPassword = Hasher.compare(
      findedAccount.getPassword(), parsedDTO.getPassword()
    );
    if(!correctPassword) {
      return new ExceptionDTO("Dados de autenticação inválidos");
    }

    return new AuthResponse(findedAccount);
  }

  private UserData findByCpf(String cpf) {
    for (UserData user : userDatabase)
      if (user.getCpf().equalsIgnoreCase(cpf))
        return user;

    return null;
  }
}
