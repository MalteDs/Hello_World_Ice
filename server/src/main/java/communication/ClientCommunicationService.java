package communication;

import com.zeroc.Ice.*;
import Demo.*;
import java.util.concurrent.*;
import java.util.*;

public class ClientCommunicationService {
    private ConcurrentHashMap<String, ClientInfo> registeredClients = new ConcurrentHashMap<>();

    public Response registerClient(String hostname, String clientIP, CallbackPrx callback, Current current) {
        registeredClients.put(hostname, new ClientInfo(callback, clientIP));
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
        if (targetClient != null && targetClient.callback != null) {
            try {
                // Intentar enviar el mensaje
                System.out.println("Intentando enviar mensaje a " + targetHostname);
                targetClient.callback.receiveMessage(senderHostname, actualMessage);
                System.out.println("Mensaje enviado exitosamente a " + targetHostname);
                return new Response(0, "Mensaje enviado exitosamente a " + targetHostname);
            } catch (java.lang.Exception e) {
                String errorMessage = "Error al enviar mensaje a " + targetHostname + ": " + e.getMessage();
                System.err.println(errorMessage);
                e.printStackTrace();  // Para ver el stack trace completo
                return new Response(1, errorMessage);
            }
        } else {
            String error = targetClient == null ? 
                "Cliente " + targetHostname + " no encontrado" :
                "Callback no disponible para " + targetHostname;
            System.err.println(error);
            return new Response(1, error);
        }
    }

    public Response broadcast(String senderHostname, String message, Current current) {
        int successCount = 0;
        List<String> failedClients = new ArrayList<>();

        for (Map.Entry<String, ClientInfo> entry : registeredClients.entrySet()) {
            String targetHostname = entry.getKey();
            if (!targetHostname.equals(senderHostname)) {
                ClientInfo clientInfo = entry.getValue();
                if (clientInfo.callback != null) {
                    try {
                        clientInfo.callback.receiveMessage(senderHostname, message);
                        successCount++;
                    } catch (java.lang.Exception e) {
                        System.err.println("Error al enviar broadcast a " + targetHostname + ": " + e.getMessage());
                        failedClients.add(targetHostname);
                    }
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
        CallbackPrx callback;
        String ip;

        ClientInfo(CallbackPrx callback, String ip) {
            this.callback = callback;
            this.ip = ip;
        }
    }
}