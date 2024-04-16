package components.bankService;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import connections.SocketThread;
import connections.components.SocketComponent;
import connections.data.SocketData;
import dtos.DTO;
import dtos.RemoteOperation;
import interfaces.ThrowingRunnable;

public abstract class BaseBankService extends SocketThread {
        protected final Map<RemoteOperation, ThrowingRunnable<DTO, Exception>> operationHandlers = new HashMap<>();

        public BaseBankService(
                        Map<SocketComponent, List<SocketData>> connectedSockets,
                        SocketComponent socketClientComponent) {
                super(connectedSockets, socketClientComponent);

        }

        protected abstract DTO createAccount() throws Exception;

        protected abstract DTO getAccountData() throws Exception;

        protected abstract DTO withdraw(DTO dto) throws Exception;

        protected abstract DTO deposit(DTO dto) throws Exception;

        protected abstract DTO wireTransfer(DTO dto) throws Exception;

        protected abstract DTO getBalance() throws Exception;

        protected abstract DTO getSavingsProjections() throws Exception;

        protected abstract DTO getFixedIncomeProjections() throws Exception;

        protected abstract DTO updateFixedIncome(DTO dto) throws Exception;
}
