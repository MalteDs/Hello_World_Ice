package network;

import java.net.*;
import java.util.*;

public class NetworkService {
    public String listNetworkInterfaces() {
        StringBuilder result = new StringBuilder();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                result.append(ni.getName()).append(" - ").append(ni.getDisplayName()).append("\n");
            }
        } catch (SocketException e) {
            result.append("Error retrieving network interfaces: ").append(e.getMessage());
        }
        return result.toString();
    }
}
