package components.authenticationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.auth.AuthRequest;
import dtos.auth.AuthResponse;
import dtos.generic.ExceptionDTO;
import dtos.generic.MessageDTO;
import dtos.user.BankAccount;
import dtos.user.UserData;
import utils.Hasher;
import utils.ObjectConverter;

public class AuthenticationService extends BaseAuthenticationService {
  private final List<UserData> userDatabase = getInitialUserDatabase();

  public AuthenticationService(
      Map<SocketComponent, List<SocketData>> connectedSockets,
      SocketComponent socketClientComponent) {
    super(connectedSockets, socketClientComponent);
  }

  @Override
  protected void execute() throws Exception {
  }

  @Override
  protected void handleExecutionException(Exception exception) {
  }

  @Override
  protected DTO createAccount(DTO user) throws Exception {
    UserData parsedUserDTO = ObjectConverter.convert(user);

    if (parsedUserDTO == null)
      return new ExceptionDTO("Instância de DTO inválida!");

    boolean existsByCpf = findByCpf(parsedUserDTO.getCpf()) != null;

    if (existsByCpf)
      return new ExceptionDTO("Um usuário de mesmo cpf já existe!");

    parsedUserDTO.setPassword(Hasher.hashAndEncode(parsedUserDTO.getPassword()));

    userDatabase.add(parsedUserDTO);
    BankAccount newBankAccount = new BankAccount(parsedUserDTO);

    return newBankAccount;
  }

  @Override
  protected DTO authenticate(DTO authData) throws Exception {
    AuthRequest parsedDTO = ObjectConverter.convert(authData);

    if (parsedDTO == null)
      return new ExceptionDTO("Instância de DTO inválida!");

    return new AuthResponse(null, null, null);
  }

  private List<UserData> getInitialUserDatabase() {
    List<UserData> initialUserDatabase = new ArrayList<>();

    initialUserDatabase.add(new UserData("Lucas Paulino", "123.456.789-10", "Rua dos fulanos, 123", "84 99123-4567",
        Hasher.hashAndEncode("lucas123")));

    return initialUserDatabase;
  }

  private UserData findByCpf(String cpf) {
    for (UserData user : userDatabase)
      if (user.getCpf().equalsIgnoreCase(cpf))
        return user;

    return null;
  }
}
