package simulation;

import constants.DefaultIpAndPorts;
import socket.components.SocketComponent;
import socket.components.SocketServer;
import socket.data.SocketConnectionData;
import socket.data.SocketServerData;
import threads.DatabaseThread;
import utils.ConnectionUtils;
import utils.ConsolePrinter;

public class Database {
    public static void main(String[] args) {
        SocketConnectionData[] serversToConnect = {};

        var bankServiceServerData = new SocketServerData(DefaultIpAndPorts.DATABASE_PORT, SocketComponent.BANK_SERVICE,
                DefaultIpAndPorts.IP_WHITELIST, serversToConnect);
        try {
            var bankServiceThread = new SocketServer(DatabaseThread.class, bankServiceServerData);
            new Thread(bankServiceThread).start();

        } catch (Exception e) {
            ConsolePrinter.println("Erro interno no firewall: \n" + e.getMessage());
        }
    }
}
