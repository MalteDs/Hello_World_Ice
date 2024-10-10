package network;

import java.io.IOException;
import java.net.*;

public class PortScannerService {
    public String scanOpenPorts(String ipAddress) {
        StringBuilder result = new StringBuilder();
        int startPort = 1, endPort = 1024; // Puertos comunes
        result.append("Scanning open ports for IP: ").append(ipAddress).append("\n");
        try {
            for (int port = startPort; port <= endPort; port++) {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ipAddress, port), 50);
                    socket.close();
                    result.append("Port ").append(port).append(" is open\n");
                } catch (IOException ignored) {
                    // El puerto está cerrado
                }
            }
        } catch (Exception e) {
            result.append("Error scanning ports: ").append(e.getMessage());
        }
        return result.toString();
    }
}
