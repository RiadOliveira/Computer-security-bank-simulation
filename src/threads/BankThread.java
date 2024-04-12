package threads;

import java.util.List;
import java.util.Map;

import socket.SocketThread;
import socket.components.SocketComponent;
import socket.data.SocketData;

public class BankThread extends SocketThread {
    public BankThread(Map<SocketComponent, List<SocketData>> connectedSockets, SocketComponent socketClientComponent) {
        super(connectedSockets, socketClientComponent);
    }

    @Override
    protected void execute() throws Exception {
        System.out.println("Serviço bancário rodando...");
        while (true) {

        }
    }

    @Override
    protected void handleExecutionException(Exception exception) {

    }
}
