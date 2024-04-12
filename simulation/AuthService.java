package simulation;

import constants.DefaultIpAndPorts;
import socket.components.SocketComponent;
import socket.components.SocketServer;
import socket.data.SocketConnectionData;
import socket.data.SocketServerData;
import threads.AuthThread;
import utils.ConsolePrinter;

public class AuthService {
    public static void main(String[] args) {
        SocketConnectionData[] serversToConnect = { };

        var authServiceServerData = new SocketServerData(DefaultIpAndPorts.AUTH_SERVICE_PORT, SocketComponent.FIREWALL,
                DefaultIpAndPorts.IP_WHITELIST, serversToConnect);
        try {
            var authServiceThread = new SocketServer(AuthThread.class, authServiceServerData);
            new Thread(authServiceThread).start();

        } catch (Exception e) {
            ConsolePrinter.println("Erro interno no firewall: \n" + e.getMessage());
        }
    }
}
