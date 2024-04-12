package simulation;

import constants.DefaultIpAndPorts;
import socket.SocketThread;
import socket.components.SocketComponent;
import socket.components.SocketServer;
import socket.data.SocketConnectionData;
import socket.data.SocketServerData;
import threads.FirewallThread;
import utils.ConnectionUtils;
import utils.ConsolePrinter;

public class Firewall {
    public static void main(String[] args) {
        SocketConnectionData[] serversToConnect = { ConnectionUtils.getAuthServiceData(),
                ConnectionUtils.getBankServiceData() };

        var firewallServerData = new SocketServerData(DefaultIpAndPorts.FIREWALL_PORT, SocketComponent.GATEWAY,
                DefaultIpAndPorts.IP_WHITELIST, serversToConnect);

        try {
            var firewallThread = new SocketServer(FirewallThread.class, firewallServerData);
            new Thread(firewallThread).start();

        } catch (Exception e) {
            ConsolePrinter.println("Erro interno nos serviços: \n" + e.getMessage());
        }
    }
}
