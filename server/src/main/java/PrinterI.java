import Demo.CallbackPrx;
import Demo.Response;
import com.zeroc.Ice.Current;
import calculations.FibonacciService;
import calculations.PrimeFactorService;
import network.NetworkService;
import network.PortScannerService;
import system.SystemCommandService;
import communication.ClientCommunicationService;
import java.util.concurrent.ConcurrentHashMap;

public class PrinterI implements Demo.Printer {
    private final FibonacciService fibonacciService = new FibonacciService();
    private final PrimeFactorService primeFactorService = new PrimeFactorService();
    private final NetworkService networkService = new NetworkService();
    private final PortScannerService portScannerService = new PortScannerService();
    private final SystemCommandService systemCommandService = new SystemCommandService();
    private final ClientCommunicationService communicationService = new ClientCommunicationService();
    private final ConcurrentHashMap<String, CallbackPrx> callbacks = new ConcurrentHashMap<>();

    @Override
    public void subscribe(String hostname, CallbackPrx callback, Current current) {
        callbacks.put(hostname, callback);
        Response response = communicationService.registerClient(hostname, 
            current.con.getEndpoint().toString(),
            callback,  // Pasamos el callback al servicio de comunicación
            current);
        System.out.println("Cliente registrado con callback: " + hostname + 
            " - " + response.value);
    }

    @Override
    public void unsubscribe(String hostname, Current current) {
        callbacks.remove(hostname);
        System.out.println("Cliente desregistrado: " + hostname);
    }

    @Override
    public Response printString(String s, Current current) {
        try {
            String[] parts = s.split(":", 4);
            if (parts.length < 2) {
                return new Response(1, "Formato de mensaje inválido");
            }

            String userHostname = parts[0];
            String clientIP = parts.length > 2 ? parts[1] : "unknown";
            String message = parts.length == 4 ? parts[3] : parts[2];

            System.out.println(userHostname + ":" + clientIP + " - Processing request: " + message);
            
            // Casos 2a-2d
            try {
                // 2a. Si el mensaje es un número entero positivo
                int number = Integer.parseInt(message.trim());
                if (number > 0) {
                    String fibonacciSeries = fibonacciService.calculateFibonacci(number);
                    String primeFactors = primeFactorService.calculatePrimeFactors(number);
                    
                    System.out.println(userHostname + ":" + clientIP + ": Fibonacci Series up to " + number + ": " + fibonacciSeries);
                    System.out.println(userHostname + ":" + clientIP + ": Prime Factors: " + primeFactors);

                    return new Response(0, String.format("Client: %s\nFibonacci Series: %s\nPrime Factors: %s", 
                        userHostname, fibonacciSeries, primeFactors));
                }
            } catch (NumberFormatException e) {
                // Si no es un número, continúa con los demás casos
            }

            // 2b. Si el mensaje inicia con "listifs"
            if (message.startsWith("listifs")) {
                String responseMessage = networkService.listNetworkInterfaces();
                System.out.println(userHostname + ":" + clientIP + ": Network Interfaces: " + responseMessage);
                return new Response(0, responseMessage);
            }

            // 2c. Si el mensaje inicia con "listports" y una dirección IPv4
            if (message.startsWith("listports")) {
                String[] commandParts = message.split(" ");
                if (commandParts.length == 2) {
                    String responseMessage = portScannerService.scanOpenPorts(commandParts[1]);
                    System.out.println(userHostname + ":" + clientIP + ": Open Ports for " + commandParts[1] + ": " + responseMessage);
                    return new Response(0, responseMessage);
                } else {
                    String error = "Formato inválido para 'listports'. Uso: listports <IPv4>";
                    System.out.println(userHostname + ":" + clientIP + ": " + error);
                    return new Response(1, error);
                }
            }

            // 2d. Si el mensaje inicia con "!" y un comando del sistema
            if (message.startsWith("!")) {
                String command = message.substring(1).trim();
                String responseMessage = systemCommandService.executeCommand(command);
                System.out.println(userHostname + ":" + clientIP + ": Command executed: " + command);
                return new Response(0, responseMessage);
            }

            // Casos 4a-4c
            // 4a. Si el mensaje es "list clients"
            if (message.equals("list clients")) {
                return communicationService.listClients();
            }

            // 4b. Si el mensaje inicia con "to X:" para mensaje directo
            if (message.startsWith("to ")) {
                return communicationService.sendToClient(userHostname, message, current);
            }

            // 4c. Si el mensaje inicia con "BC" para broadcast
            if (message.startsWith("BC ")) {
                return communicationService.broadcast(userHostname, message.substring(3), current);
            }

            // Si no se reconoce el comando
            return new Response(1, "Comando no reconocido: " + message);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(1, "Error processing request: " + e.getMessage());
        }
    }
}