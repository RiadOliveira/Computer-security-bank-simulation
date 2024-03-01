package process.client;

import java.net.Socket;

import dtos.DTO;
import process.AppThread;

public class ClientThread extends AppThread {
  public ClientThread(Socket serverSocket) {
    super(serverSocket, false);
  }

  @Override
  public void execute() {
  }

  @Override
  protected void handleCreateAccount(DTO dto) throws Exception {
  }

  @Override
  protected void handleAuthenticate(DTO dto) throws Exception {
  }

  @Override
  protected void handleGetAccountData(DTO dto) throws Exception {
  }

  @Override
  protected void handleWithdraw(DTO dto) throws Exception {
  }

  @Override
  protected void handleDeposit(DTO dto) throws Exception {
  }

  @Override
  protected void handleWireTransfer(DTO dto) throws Exception {
  }

  @Override
  protected void handleGetBalance(DTO dto) throws Exception {
  }

  @Override
  protected void handleGetSavingsProjections(DTO dto) throws Exception {
  }

  @Override
  protected void handleGetFixedIncomeProjections(DTO dto) throws Exception {
  }

  @Override
  protected void handleUpdateFixedIncome(DTO dto) throws Exception {
  }
}
