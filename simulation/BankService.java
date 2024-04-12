package simulation;

import constants.DefaultIpAndPorts;
import socket.components.SocketComponent;
import socket.components.SocketServer;
import socket.data.SocketConnectionData;
import socket.data.SocketServerData;
import threads.BankThread;
import utils.ConnectionUtils;
import utils.ConsolePrinter;

public class BankService {
    public static void main(String[] args) {
        SocketConnectionData[] serversToConnect = { ConnectionUtils.getDatabaseData() };

        var bankServiceServerData = new SocketServerData(DefaultIpAndPorts.BANK_SERVICE_PORT, SocketComponent.FIREWALL,
                DefaultIpAndPorts.IP_WHITELIST, serversToConnect);
        try {
            var bankServiceThread = new SocketServer(BankThread.class, bankServiceServerData);
            new Thread(bankServiceThread).start();

        } catch (Exception e) {
            ConsolePrinter.println("Erro interno no firewall: \n" + e.getMessage());
        }
    }
}
