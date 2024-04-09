package process;

import java.util.HashMap;

import dtos.DTO;

public abstract class CommandHandler {
  @FunctionalInterface
  public interface ThrowingConsumer<T, E extends Exception> {
    DTO accept(T t) throws E;
  }

  protected HashMap<
    AppCommand, ThrowingConsumer<DTO, Exception>
  > commandHandlers;

  public CommandHandler() {
    commandHandlers = new HashMap<>();
    
    commandHandlers.put(
      AppCommand.CREATE_ACCOUNT, this::handleCreateAccount
    );
    commandHandlers.put(
      AppCommand.AUTHENTICATE, this::handleAuthenticate
    );
    commandHandlers.put(
      AppCommand.GET_ACCOUNT_DATA, this::handleGetAccountData
    );
    commandHandlers.put(
      AppCommand.WITHDRAW, this::handleWithdraw
    );
    commandHandlers.put(
      AppCommand.DEPOSIT, this::handleDeposit
    );
    commandHandlers.put(
      AppCommand.WIRE_TRANSFER, this::handleWireTransfer
    );
    commandHandlers.put(
      AppCommand.GET_BALANCE, this::handleGetBalance
    );
    commandHandlers.put(
      AppCommand.GET_SAVINGS_PROJECTIONS, this::handleGetSavingsProjections
    );
    commandHandlers.put(
      AppCommand.GET_FIXED_INCOME_PROJECTIONS, this::handleGetFixedIncomeProjections
    );
    commandHandlers.put(
      AppCommand.UPDATE_FIXED_INCOME, this::handleUpdateFixedIncome
    );
  }

  protected abstract DTO handleCreateAccount(DTO dto) throws Exception;
  protected abstract DTO handleAuthenticate(DTO dto) throws Exception;
  protected abstract DTO handleGetAccountData(DTO dto) throws Exception;
  protected abstract DTO handleWithdraw(DTO dto) throws Exception;
  protected abstract DTO handleDeposit(DTO dto) throws Exception;
  protected abstract DTO handleWireTransfer(DTO dto) throws Exception;
  protected abstract DTO handleGetBalance(DTO dto) throws Exception;
  protected abstract DTO handleGetSavingsProjections(DTO dto) throws Exception;
  protected abstract DTO handleGetFixedIncomeProjections(DTO dto) throws Exception;
  protected abstract DTO handleUpdateFixedIncome(DTO dto) throws Exception;
}
