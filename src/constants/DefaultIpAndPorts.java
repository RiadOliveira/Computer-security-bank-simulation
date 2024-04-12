package constants;

public class DefaultIpAndPorts {
    public static String IP_ADDRESS = "127.0.0.1";
    public static String[] IP_WHITELIST = { DefaultIpAndPorts.IP_ADDRESS };

    public static int GATEWAY_PORT = 2000;

    public static int FIREWALL_PORT = 3000;

    public static int GATEWAY_FIREWALL_PORT = 3001;
    public static int AUTH_FIREWALL_PORT = 3002;
    public static int BANK_FIREWALL_PORT = 3003;

    public static int AUTH_SERVICE_PORT = 4000;

    public static int BANK_SERVICE_PORT = 5000;

    public static int DATABASE_BANK_PORT = 5001;

    public static int DATABASE_PORT = 6000;
}
