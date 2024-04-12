package threads;

import java.util.List;
import java.util.Map;

import socket.SocketThread;
import socket.components.SocketComponent;
import socket.data.SocketData;

public class FirewallThread extends SocketThread {
    public FirewallThread(Map<SocketComponent, List<SocketData>> connectedSockets,
            SocketComponent socketClientComponent) {
        super(connectedSockets, socketClientComponent);
    }

    @Override
    protected void execute() throws Exception {
        System.out.println("Firewall rodando...");
        while (true) {

        }
    }

    @Override
    protected void handleExecutionException(Exception exception) {
        throw new UnsupportedOperationException("Unimplemented method 'handleExecutionException'");
    }

}
