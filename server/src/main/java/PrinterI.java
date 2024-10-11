import Demo.Response;
import com.zeroc.Ice.Current;
import calculations.FibonacciService;
import calculations.PrimeFactorService;
import network.NetworkService;
import network.PortScannerService;
import system.SystemCommandService;
import communication.ClientCommunicationService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class PrinterI implements Demo.Printer {
    private FibonacciService fibonacciService = new FibonacciService();
    private PrimeFactorService primeFactorService = new PrimeFactorService();
    private NetworkService networkService = new NetworkService();
    private PortScannerService portScannerService = new PortScannerService();
    private SystemCommandService systemCommandService = new SystemCommandService();
    private ClientCommunicationService communicationService = new ClientCommunicationService();
    private ExecutorService threadPool;

    public PrinterI(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public Response printString(String s, Current current) {
        CompletableFuture<Response> future = CompletableFuture.supplyAsync(() -> {
            String[] parts = s.split(":", 4);
            String userHostname = parts[0];
            String clientIP = parts.length > 2 ? parts[1] : "unknown";
            String message = parts.length > 2 ? parts[3] : parts[2];

            System.out.println(userHostname + ":" + clientIP + " - Processing request: " + message);

            if (message.startsWith("register")) {
                return communicationService.registerClient(userHostname, clientIP, current);
            } else if (message.equals("list clients")) {
                return communicationService.listClients();
            } else if (message.startsWith("to ")) {
                return communicationService.sendToClient(userHostname, message, current);
            } else if (message.startsWith("BC ")) {
                return communicationService.broadcast(userHostname, message.substring(3), current);
            } else {
                return handleExistingFunctionality(userHostname, clientIP, message);
            }
        }, threadPool);

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(1, "Error processing request: " + e.getMessage());
        }
    }

    private Response handleExistingFunctionality(String userHostname, String clientIP, String message) {
        String responseMessage = "";

        try {
            int number = Integer.parseInt(message.trim());
            if (number > 0) {
                String fibonacciSeries = fibonacciService.calculateFibonacci(number);
                String primeFactors = primeFactorService.calculatePrimeFactors(number);
                
                System.out.println(userHostname + ":" + clientIP + ": Fibonacci Series up to " + number + ": " + fibonacciSeries);
                System.out.println(userHostname + ":" + clientIP + ": Prime Factors: " + primeFactors);

                responseMessage = String.format("Client: %s\nFibonacci Series: %s\nPrime Factors: %s", userHostname, fibonacciSeries, primeFactors);
            }
        } catch (NumberFormatException e) {
            if (message.startsWith("listifs")) {
                responseMessage = networkService.listNetworkInterfaces();
                System.out.println(userHostname + ":" + clientIP + ": Network Interfaces: " + responseMessage);
            } else if (message.startsWith("listports")) {
                String[] commandParts = message.split(" ");
                if (commandParts.length == 2) {
                    responseMessage = portScannerService.scanOpenPorts(commandParts[1]);
                    System.out.println(userHostname + ":" + clientIP + ": Open Ports for " + commandParts[1] + ": " + responseMessage);
                } else {
                    responseMessage = "Invalid format for 'listports'. Usage: listports <IPv4>";
                    System.out.println(userHostname + ":" + clientIP + ": Invalid format for 'listports'");
                }
            } else if (message.startsWith("!")) {
                responseMessage = systemCommandService.executeCommand(message.substring(1).trim());
                System.out.println(userHostname + ":" + clientIP + ": Command executed: " + message.substring(1).trim());
            }
        }

        return new Response(0, responseMessage);
    }
}