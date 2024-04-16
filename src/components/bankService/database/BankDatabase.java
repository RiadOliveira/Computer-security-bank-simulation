package components.bankService.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.generic.MessageDTO;
import dtos.generic.ValueDTO;
import dtos.user.BankAccount;
import utils.ObjectConverter;
import utils.ValueFormatter;

public class BankDatabase extends BaseBankDatabase {
  private List<BankAccount> accountsDatabase = getInitialAccountDatabase();

  public BankDatabase(
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
  protected synchronized DTO deposit(DTO dto) throws Exception {
    ValueDTO parsedDTO = ObjectConverter.convert(dto);

    double depositValue = parsedDTO.getValue();

    if (depositValue <= 0.0)
      return new MessageDTO("Valor de depósito inválido!");

    // atualiza o valor na conta do cliente logado

    String depositFormattedValue = ValueFormatter.formatToBrazilianCurrency(depositValue);
    // pega o saldo da conta formatado

    return new MessageDTO("Valor de " + depositFormattedValue + " depositado com sucesso, seu saldo atual é ");
  }

  private List<BankAccount> getInitialAccountDatabase() {
    List<BankAccount> initialAccountDatabase = new ArrayList<>();

    return initialAccountDatabase;
  }

  // @Override
  // protected DTO handleGetBalance(DTO dto) throws Exception{
  //   String formattedBalanceValue = ValueFormatter.formatToBrazilianCurrency();

  //   return new MessageDTO("Seu saldo atual é de " + formattedBalanceValue + ".");
  // }
}
