package components.bankService;

import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.generic.MessageDTO;
import dtos.generic.ValueDTO;
import dtos.operation.WireTransferDTO;
import errors.AppException;
import utils.ObjectConverter;
import utils.ValueFormatter;

public class BankService extends BaseBankService {
  public BankService(
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
  protected DTO getAccountData() throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAccountData'");
  }

  @Override
  protected DTO withdraw(DTO dto) throws Exception {
    ValueDTO parsedDTO = ObjectConverter.convert(dto);
    double withdrawValue = parsedDTO.getValue();

    if (withdrawValue <= 0.0)
      throw new AppException("Valor de saque inválido!");

    ValueDTO receivedDTO = ObjectConverter.convert(receiveSecureDTO(SocketComponent.BANK_DATABASE));
    double accountBalance = receivedDTO.getValue();

    if (withdrawValue > accountBalance)
      throw new AppException("Valor de saque maior do que saldo disponível!");

    sendSecureDTO(SocketComponent.BANK_DATABASE, new ValueDTO(-withdrawValue));

    String formattedWithdrawValue = ValueFormatter.formatToBrazilianCurrency(withdrawValue);
    String updatedFormattedBalanceValue = ValueFormatter.formatToBrazilianCurrency(accountBalance - withdrawValue);

    return new MessageDTO(
        "Valor de " + formattedWithdrawValue +
            " retirado com sucesso, seu saldo atual é " +
            updatedFormattedBalanceValue + '.');

  }

  @Override
  protected DTO deposit(DTO dto) throws Exception {
    ValueDTO parsedDTO = ObjectConverter.convert(dto);
    double depositValue = parsedDTO.getValue();

    if (depositValue <= 0.0)
      throw new Exception("Valor de depósito inválido!");

    sendSecureDTO(SocketComponent.BANK_DATABASE, new ValueDTO(depositValue));
    ValueDTO receivedDTO = ObjectConverter.convert(receiveSecureDTO(SocketComponent.BANK_DATABASE));

    String formattedDepositValue = ValueFormatter.formatToBrazilianCurrency(depositValue);
    String updatedFormattedBalanceValue = ValueFormatter.formatToBrazilianCurrency(receivedDTO.getValue());

    return new MessageDTO("Valor de " + formattedDepositValue + " depositado com sucesso, seu saldo atual é "
        + updatedFormattedBalanceValue + ".");
  }

  @Override
  protected DTO wireTransfer(DTO dto) throws Exception {
    WireTransferDTO parsedDTO = ObjectConverter.convert(dto);
    double transferValue = parsedDTO.getValue();

    if (transferValue <= 0.0)
      throw new AppException("Valor da transferência inválido!");

    ValueDTO receivedDTO = ObjectConverter.convert(receiveSecureDTO(SocketComponent.BANK_DATABASE));
    double accountBalance = receivedDTO.getValue();

    if (transferValue > accountBalance)
      throw new AppException("Valor de transferência maior que saldo disponível!");

    String agencyToTransfer = parsedDTO.getTargetAgency();
    String accountNumberToTransfer = parsedDTO.getTargetAccountNumber();

    return new MessageDTO("Valor de " + " transferido com sucesso para " + ", seu saldo atual é " + '.');
  }

  @Override
  protected DTO getBalance() throws Exception {
    ValueDTO receivedDTO = ObjectConverter.convert(receiveSecureDTO(SocketComponent.BANK_DATABASE));
    String formattedBalanceValue = ValueFormatter.formatToBrazilianCurrency(receivedDTO.getValue());

    return new MessageDTO("Seu saldo atual é " + formattedBalanceValue + ".");
  }

  @Override
  protected DTO getSavingsProjections() throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getSavingsProjections'");
  }

  @Override
  protected DTO getFixedIncomeProjections() throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getFixedIncomeProjections'");
  }

  @Override
  protected DTO createAccount() throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'createAccount'");
  }

  @Override
  protected DTO updateFixedIncome(DTO dto) throws Exception {
    ValueDTO parsedDTO = ObjectConverter.convert(dto);
    double fixedIncomeUpdateValue = parsedDTO.getValue();

    if (fixedIncomeUpdateValue == 0.0)
      throw new AppException(
          "Valor de atualização de renda fixa inválido!");

    return new MessageDTO(
        "A renda fixa foi atualizada com o valor de " + ", sua renda fixa atual é " + ", e seu saldo atual é " +
            '.');
  }

}
