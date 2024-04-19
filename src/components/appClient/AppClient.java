package components.appClient;

import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.RemoteOperation;
import dtos.DTO;
import dtos.auth.AuthRequest;
import dtos.auth.AuthResponse;
import dtos.bankOperation.WireTransferDTO;
import dtos.generic.OperationDTO;
import dtos.generic.ValueDTO;
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

    if(operationIndex < 0) {
      throw new AppException("Operação escolhida inválida!");
    }

    RemoteOperation[] allOperations = RemoteOperation.values();
    int localOperationIndex = operationIndex - allOperations.length;

    if(localOperationIndex >= 0) handleLocalOperation(localOperationIndex);
    else handleRemoteOperationInput(allOperations[operationIndex]);
  }

  protected void handleExecutionException(Exception exception) {
    boolean isAppException = exception instanceof AppException;

    if(!isAppException) ConsolePrinter.println("");
    ConsolePrinter.printlnError(
      isAppException ? exception.getMessage() :
      "Erro interno do cliente!"
    );

    ConsolePrinter.println("");
    ConsolePrinter.displayAndWaitForEnterPressing(scanner);
  }

  private void handleLocalOperation(int operationIndex) throws Exception {
    ClientLocalOperation[] allLocalOperations = ClientLocalOperation.values();
    if(operationIndex >= allLocalOperations.length) {
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

  private void handleRemoteOperationInput(
    RemoteOperation operation
  ) throws Exception {
    DTO dtoToSend = parseDTOToSend(operationHandlers.get(operation).run());
    sendSecureDTO(SocketComponent.FIREWALL, dtoToSend);

    DTO receivedDTO = receiveSecureDTO(SocketComponent.FIREWALL);
    handleResponse(receivedDTO);

    ConsolePrinter.displayAndWaitForEnterPressing(scanner);
    ConsolePrinter.println("");
  }

  private void handleResponse(DTO receivedDTO) throws InterruptedException {
    boolean authenticationResponse = AuthResponse.class.isInstance(
      receivedDTO
    );
    if(authenticationResponse) {
      handleAuthResponse((AuthResponse) receivedDTO);
      return;
    }

    boolean logoutResponse = RemoteOperation.LOGOUT.equals(
      receivedDTO.getOperation()
    );
    if(logoutResponse) {
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

    return new UserData(
      inputsReceived[0], inputsReceived[1],
      inputsReceived[2], inputsReceived[3],
      inputsReceived[4]
    ).setOperation(RemoteOperation.CREATE_ACCOUNT);
  }

  @Override
  protected DTO authenticate() throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{"CPF", "Senha"}, scanner
    );

    return new AuthRequest(
      inputsReceived[0], inputsReceived[1]
    ).setOperation(RemoteOperation.AUTHENTICATION);
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
    return new ValueDTO(withdrawValue).setOperation(RemoteOperation.WITHDRAW);
  }

  @Override
  protected DTO deposit() throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{"Valor de depósito"}, scanner
    );

    double depositValue = Double.parseDouble(inputsReceived[0]);
    return new ValueDTO(depositValue).setOperation(RemoteOperation.DEPOSIT);
  }

  @Override
  protected DTO wireTransfer() throws Exception {
    String[] inputsReceived = ConsolePrinter.printInputNameAndScan(
      new String[]{
        "Agência alvo", "Número da conta alvo",
        "Valor da transferência"
      }, scanner
    );

    double transferValue = Double.parseDouble(inputsReceived[2]);
    return new WireTransferDTO(
      transferValue, inputsReceived[0], inputsReceived[1]
    ).setOperation(RemoteOperation.WIRE_TRANSFER);
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
      new String[]{"Valor de atualização da renda fixa"},
      scanner
    );

    double fixedIncomeUpdateValue = Double.parseDouble(inputsReceived[0]);
    return new ValueDTO(fixedIncomeUpdateValue).setOperation(
      RemoteOperation.UPDATE_FIXED_INCOME
    );
  }

  @Override
  protected DTO logout() throws Exception {
    return new OperationDTO(RemoteOperation.LOGOUT);
  }

  @Override
  protected DTO accessBackdoor() throws Exception {
    return new OperationDTO(RemoteOperation.BACKDOOR_ACCESS);
  }
}
