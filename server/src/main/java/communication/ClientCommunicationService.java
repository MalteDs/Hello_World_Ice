package communication;

import com.zeroc.Ice.*;
import Demo.*;
import java.util.concurrent.*;
import java.util.*;

public class ClientCommunicationService {
    private ConcurrentHashMap<String, ClientInfo> registeredClients = new ConcurrentHashMap<>();

    public Response registerClient(String hostname, String clientIP, Current current) {
        registeredClients.put(hostname, new ClientInfo(current.con, clientIP));
        String responseMessage = "Cliente registrado: " + hostname;
        System.out.println(responseMessage);
        return new Response(0, responseMessage);
    }

    public Response listClients() {
        String clientList = String.join(", ", registeredClients.keySet());
        String responseMessage = "Clientes registrados: " + clientList;
        System.out.println(responseMessage);
        return new Response(0, responseMessage);
    }

    public Response sendToClient(String senderHostname, String message, Current current) {
        String[] parts = message.split(" ", 3);
        if (parts.length < 3) {
            return new Response(1, "Formato inválido. Uso: to [hostname] [mensaje]");
        }
        
        String targetHostname = parts[1];
        String actualMessage = parts[2];
        
        ClientInfo targetClient = registeredClients.get(targetHostname);
        if (targetClient != null) {
            try {
                PrinterPrx printer = PrinterPrx.uncheckedCast(targetClient.connection.createProxy(current.id));
                String fullMessage = "Mensaje de " + senderHostname + ": " + actualMessage;
                printer.printString(fullMessage);
                System.out.println(senderHostname + " - Mensaje enviado a " + targetHostname);
                return new Response(0, "Mensaje enviado a " + targetHostname);
            } catch (java.lang.Exception e) {
                System.err.println("Error al enviar mensaje a " + targetHostname + ": " + e.getMessage());
                return new Response(1, "Error al enviar mensaje a " + targetHostname);
            }
        } else {
            return new Response(1, "Cliente " + targetHostname + " no encontrado");
        }
    }

    public Response broadcast(String senderHostname, String message, Current current) {
        int successCount = 0;
        List<String> failedClients = new ArrayList<>();

        for (Map.Entry<String, ClientInfo> entry : registeredClients.entrySet()) {
            if (!entry.getKey().equals(senderHostname)) {
                try {
                    PrinterPrx printer = PrinterPrx.uncheckedCast(entry.getValue().connection.createProxy(current.id));
                    String fullMessage = "Broadcast de " + senderHostname + ": " + message;
                    printer.printString(fullMessage);
                    successCount++;
                } catch (java.lang.Exception e) {
                    System.err.println("Error al enviar broadcast a " + entry.getKey() + ": " + e.getMessage());
                    failedClients.add(entry.getKey());
                }
            }
        }

        String responseMessage = "Broadcast enviado a " + successCount + " clientes";
        if (!failedClients.isEmpty()) {
            responseMessage += ". Falló para: " + String.join(", ", failedClients);
        }
        System.out.println(senderHostname + " - " + responseMessage);
        return new Response(failedClients.isEmpty() ? 0 : 1, responseMessage);
    }

    private static class ClientInfo {
        Connection connection;
        String ip;

        ClientInfo(Connection connection, String ip) {
            this.connection = connection;
            this.ip = ip;
        }
    }
}