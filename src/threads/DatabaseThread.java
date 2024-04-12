package threads;

import java.util.List;
import java.util.Map;

import socket.SocketThread;
import socket.components.SocketComponent;
import socket.data.SocketData;

public class DatabaseThread extends SocketThread {
    public DatabaseThread(Map<SocketComponent, List<SocketData>> connectedSockets,
            SocketComponent socketClientComponent) {
        super(connectedSockets, socketClientComponent);
    }

    @Override
    protected void execute() throws Exception {
        System.out.println("Banco de dados rodando...");
        while (true) {

        }
    }

    @Override
    protected void handleExecutionException(Exception exception) {

    }
}
