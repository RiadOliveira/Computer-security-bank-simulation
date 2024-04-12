package threads;

import java.util.List;
import java.util.Map;

import socket.SocketThread;
import socket.components.SocketComponent;
import socket.data.SocketData;

public class AuthThread extends SocketThread {
    public AuthThread(Map<SocketComponent, List<SocketData>> connectedSockets, SocketComponent socketClientComponent) {
        super(connectedSockets, socketClientComponent);
    }

    @Override
    protected void execute() throws Exception {
        System.out.println("Serviço de autenticação rodando...");
        while (true) {

        }
    }

    @Override
    protected void handleExecutionException(Exception exception) {

    }
}
