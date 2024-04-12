package simulation;

import socket.components.SocketClient;
import threads.ClientThread;
import utils.ConsolePrinter;
import utils.ConnectionUtils;

public class Client {
    public static void main(String[] args) {
        try {
            var client = new SocketClient(ClientThread.class, ConnectionUtils.getGatewayData());
            new Thread(client).start();

        } catch (Exception e) {
            ConsolePrinter.println("Erro interno no gateway: \n" + e.getMessage());
        }
    }
}
