package components.appClient;

import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.RemoteOperation;
import dtos.DTO;
import dtos.auth.AuthRequest;
import dtos.auth.AuthResponse;
import dtos.generic.OperationDTO;
import dtos.generic.ValueDTO;
import dtos.operation.WireTransferDTO;
import dtos.user.UserData;
import errors.AppException;
import utils.ConsolePrinter;

public class AppClient extends BaseAppClient {
  public AppClient(
    Map<SocketComponent, List<SocketData>> connectedSockets,
    SocketComponent socketClientComponent
  ) {
    super(connectedSockets, socketClientComponent);
  }

  @Override
  public void execute() throws Exception {
    ConsolePrinter.printClientOperationPanel();
    int operationIndex = Integer.parseInt(scanner.nextLine()) - 1;
    ConsolePrinter.println("");

    if (operationIndex < 0) {
      throw new AppException("Operação escolhida inválida!");
    }

    RemoteOperation[] allOperations = RemoteOperation.values();
    int localOperationIndex = operationIndex - allOperations.length;

    if (localOperationIndex >= 0) LocalOperation(localOperationIndex);
    else RemoteOperationInput(allOperations[operationIndex]);
  }

  protected void handleExecutionException(Exception exception) {
    boolean isAppException = exception instanceof AppException;

    if (!isAppException) ConsolePrinter.println("");
    ConsolePrinter.printlnError(
      isAppException ? exception.getMessage() : "Erro interno do cliente!"
    );

    ConsolePrinter.println("");
    ConsolePrinter.displayAndWaitForEnterPressing(scanner);
  }

  private void LocalOperation(int operationIndex) throws Exception {
    ClientLocalOperation[] allLocalOperations = ClientLocalOperation.values();
    if (operationIndex >= allLocalOperations.length) {
      throw new AppException("Operação escolhida inválida!");
    }

    ClientLocalOperation operation = allLocalOperations[operationIndex];
    switch (operation) {
      case CLEAR_CONSOLE: {
        ConsolePrinter.clearConsole();
        break;
      }
      case EXIT: {
        System.exit(0);
        break;
      }
      default: break;
    }
  }

  private void RemoteOperationInput(
    RemoteOperation operation
  ) throws Exception {
    DTO dtoToSend = parseDTOToSend(operationHandlers.get(operation).run());
    sendSecureDTO(SocketComponent.FIREWALL, dtoToSend);

    DTO receivedDTO = receiveSecureDTO(SocketComponent.FIREWALL);
    Response(receivedDTO);

    ConsolePrinter.displayAndWaitForEnterPressing(scanner);
    ConsolePrinter.println("");
  }

  private void Response(DTO receivedDTO) {
    boolean authenticationResponse = AuthResponse.class.isInstance(
      receivedDTO
    );
    if (authenticationResponse) {
      handleAuthResponse((AuthResponse) receivedDTO);
      return;
    }

    boolean logoutResponse = receivedDTO.getOperation().equals(
      RemoteOperation.LOGOUT
    );
    if (logoutResponse) {
      handleLogoutResponse();
      return;
    }
  }

  @Override
  protected DTO createAccount() throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{"Nome", "CPF", "Endereço", "Telefone", "Senha"},
      scanner
    );

    UserData clientData = new UserData(
      inputsReceived[0], inputsReceived[1],
      inputsReceived[2], inputsReceived[3],
      inputsReceived[4]
    );
    clientData.setOperation(RemoteOperation.CREATE_ACCOUNT);
    return clientData;
  }

  @Override
  protected DTO authenticate() throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{"CPF", "Senha"},
      scanner
    );

    AuthRequest authData = new AuthRequest(
      inputsReceived[0], inputsReceived[1]
    );
    authData.setOperation(RemoteOperation.AUTHENTICATE);
    return authData;
  }

  @Override
  protected DTO getAccountData() throws Exception {
    return new OperationDTO(RemoteOperation.GET_ACCOUNT_DATA);
  }

  @Override
  protected DTO withdraw() throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{"Valor de saque"}, scanner
    );

    double withdrawValue = Double.parseDouble(inputsReceived[0]);
    ValueDTO withdrawData = new ValueDTO(withdrawValue);
    withdrawData.setOperation(RemoteOperation.WITHDRAW);

    return withdrawData;
  }

  @Override
  protected DTO deposit() throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{"Valor de depósito"}, scanner
    );

    double depositValue = Double.parseDouble(inputsReceived[0]);
    ValueDTO depositData = new ValueDTO(depositValue);
    depositData.setOperation(RemoteOperation.DEPOSIT);

    return depositData;
  }

  @Override
  protected DTO wireTransfer() throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[] {
        "Agência alvo", "Número da conta alvo",
        "Valor da transferência"
      }, scanner
    );

    double transferValue = Double.parseDouble(inputsReceived[2]);
    WireTransferDTO wireTransferData = new WireTransferDTO(
      transferValue, inputsReceived[0], inputsReceived[1]
    );
    wireTransferData.setOperation(RemoteOperation.WIRE_TRANSFER);

    return wireTransferData;
  }

  @Override
  protected DTO getBalance() throws Exception {
    return new OperationDTO(RemoteOperation.GET_BALANCE);
  }

  @Override
  protected DTO getSavingsProjections() throws Exception {
    return new OperationDTO(RemoteOperation.GET_SAVINGS_PROJECTIONS);
  }

  @Override
  protected DTO getFixedIncomeProjections() throws Exception {
    return new OperationDTO(RemoteOperation.GET_FIXED_INCOME_PROJECTIONS);
  }

  @Override
  protected DTO updateFixedIncome() throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[] {"Valor de atualização da renda fixa"},
      scanner
    );

    double fixedIncomeUpdateValue = Double.parseDouble(inputsReceived[0]);
    ValueDTO fixedIncomeUpdateData = new ValueDTO(fixedIncomeUpdateValue);
    fixedIncomeUpdateData.setOperation(RemoteOperation.UPDATE_FIXED_INCOME);

    return fixedIncomeUpdateData;
  }

  @Override
  protected DTO logout() throws Exception {
    return new OperationDTO(RemoteOperation.LOGOUT);
  }
}