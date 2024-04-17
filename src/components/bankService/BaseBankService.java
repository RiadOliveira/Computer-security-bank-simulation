package components.bankService;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import connections.SocketThread;
import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import interfaces.ThrowingConsumer;

public abstract class BaseBankService extends SocketThread {
        protected final Map<RemoteOperation, ThrowingConsumer<DTO, DTO, Exception>> operationHandlers = new HashMap<>();

        public BaseBankService(
                        Map<SocketComponent, List<SocketData>> connectedSockets,
                        SocketComponent socketClientComponent) {
                super(connectedSockets, socketClientComponent);
                operationHandlers.put(
                                RemoteOperation.CREATE_ACCOUNT, this::createAccount);
                operationHandlers.put(
                                RemoteOperation.GET_ACCOUNT_DATA, this::getAccountData);
                operationHandlers.put(
                                RemoteOperation.WITHDRAW, this::withdraw);
                operationHandlers.put(
                                RemoteOperation.DEPOSIT, this::deposit);
                operationHandlers.put(
                                RemoteOperation.WIRE_TRANSFER, this::wireTransfer);
                operationHandlers.put(
                                RemoteOperation.GET_BALANCE, this::getBalance);
                operationHandlers.put(
                                RemoteOperation.GET_SAVINGS_PROJECTIONS, this::getSavingsProjections);
                operationHandlers.put(
                                RemoteOperation.GET_FIXED_INCOME_PROJECTIONS, this::getFixedIncomeProjections);
                operationHandlers.put(
                                RemoteOperation.UPDATE_FIXED_INCOME, this::updateFixedIncome);
        }

        protected abstract DTO createAccount(DTO dto) throws Exception;

        protected abstract DTO getAccountData(DTO dto) throws Exception;

        protected abstract DTO withdraw(DTO dto) throws Exception;

        protected abstract DTO deposit(DTO dto) throws Exception;

        protected abstract DTO wireTransfer(DTO dto) throws Exception;

        protected abstract DTO getBalance(DTO dto) throws Exception;

        protected abstract DTO getSavingsProjections(DTO dto) throws Exception;

        protected abstract DTO getFixedIncomeProjections(DTO dto)
                        throws Exception;

        protected abstract DTO updateFixedIncome(DTO dto) throws Exception;
}
