package threads;

import java.util.List;
import java.util.Map;

import socket.SocketThread;
import socket.components.SocketComponent;
import socket.data.SocketData;

public class ClientThread extends SocketThread {

    public ClientThread(Map<SocketComponent, List<SocketData>> connectedSockets,
            SocketComponent socketClientComponent) {
        super(connectedSockets, socketClientComponent);
    }

    @Override
    protected void execute() throws Exception {

        System.out.println("Cliente rodando...");
        while (true) {

        }
    }

    @Override
    protected void handleExecutionException(Exception exception) {

    }

}
