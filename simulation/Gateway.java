package simulation;

import constants.DefaultIpAndPorts;
import socket.components.SocketComponent;
import socket.components.SocketServer;
import socket.data.SocketConnectionData;
import socket.data.SocketServerData;
import threads.GatewayThread;
import utils.ConnectionUtils;
import utils.ConsolePrinter;

public class Gateway {
    public static void main(String[] args) {
        SocketConnectionData[] serversToConnect = { ConnectionUtils.getFirewallData() };

        var gatewayServerData = new SocketServerData(DefaultIpAndPorts.GATEWAY_PORT, SocketComponent.CLIENT,
                DefaultIpAndPorts.IP_WHITELIST, serversToConnect);

        try {
            var gatewayThread = new SocketServer(GatewayThread.class, gatewayServerData);
            new Thread(gatewayThread).start();

        } catch (Exception e) {
            ConsolePrinter.println("Erro interno no firewall: \n" + e.getMessage());
        }
    }
}
