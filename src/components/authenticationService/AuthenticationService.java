package components.authenticationService;

import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import dtos.auth.AuthRequest;
import dtos.auth.AuthResponse;
import dtos.user.UserData;
import errors.AppException;
import errors.SecurityViolationException;
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
      throw new AppException("Instância de DTO inválida!");
    }

    boolean existsByCpf = findByCpf(parsedDTO.getCpf()) != null;
    if (existsByCpf) {
      throw new AppException("Um usuário de mesmo CPF já existe!");
    }

    parsedDTO.generateAndSetId();
    userDatabase.add(parsedDTO);

    return parsedDTO;
  }

  @Override
  protected DTO authenticate(DTO authData) throws Exception {
    AuthRequest parsedDTO = ObjectConverter.convert(authData);
    if (parsedDTO == null) {
      throw new AppException("Instância de DTO inválida!");
    }

    UserData accountFound = findByCpf(parsedDTO.getCpf());
    if(accountFound == null) {
      throw new AppException("Conta especificada não encontrada!");
    }

    boolean correctPassword = Hasher.compare(
      accountFound.getPassword(), parsedDTO.getPassword()
    );
    if(!correctPassword) {
      throw new SecurityViolationException(
        "Dados de autenticação inválidos"
      );
    }

    return new AuthResponse(accountFound);
  }

  private UserData findByCpf(String cpf) {
    for (UserData user : userDatabase)
      if (user.getCpf().equalsIgnoreCase(cpf))
        return user;

    return null;
  }
}
