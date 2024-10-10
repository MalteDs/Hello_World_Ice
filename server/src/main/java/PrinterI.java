import Demo.Response;
import com.zeroc.Ice.Current;
import calculations.FibonacciService;
import calculations.PrimeFactorService;
import network.NetworkService;
import network.PortScannerService;
import system.SystemCommandService;

public class PrinterI implements Demo.Printer {
    private FibonacciService fibonacciService = new FibonacciService();
    private PrimeFactorService primeFactorService = new PrimeFactorService();
    private NetworkService networkService = new NetworkService();
    private PortScannerService portScannerService = new PortScannerService();
    private SystemCommandService systemCommandService = new SystemCommandService();

    @Override
    public Response printString(String s, Current current) {
        String[] parts = s.split(":", 4);
        String userHostname = parts[0];
        String clientIP = parts.length > 2 ? parts[1] : "unknown";
        String message = parts.length > 2 ? parts[3] : parts[2];

        String responseMessage = "";

        // Imprime los detalles iniciales del cliente y su mensaje
        System.out.println(userHostname + ":" + clientIP + " - Processing request: " + message);

        try {
            int number = Integer.parseInt(message.trim());
            if (number > 0) {
                String fibonacciSeries = fibonacciService.calculateFibonacci(number);
                String primeFactors = primeFactorService.calculatePrimeFactors(number);
                
                // Imprime la serie de Fibonacci y los factores primos en el servidor
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
