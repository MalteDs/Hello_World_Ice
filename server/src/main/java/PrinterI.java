import Demo.Response;
import java.time.Duration;
import java.time.Instant;

public class PrinterI implements Demo.Printer
{
    public Response printString(String s, com.zeroc.Ice.Current current)
    {
        Instant start = Instant.now(); // Registrar el tiempo de inicio
        int number = 0;
        String responseMessage;

        if (s.equalsIgnoreCase("exit")) {
            responseMessage = "Exiting...";
        } else {
            String[] parts = s.split(":", 4);
            String userHostname = parts[0]; 
            String clientIP = parts.length > 2 ? parts[1] : "unknown";
            String message = parts.length > 2 ? parts[3] : parts[2];

            try {
                number = Integer.parseInt(message.trim());
                if (number > 0) {
                    // Calcular la serie de Fibonacci y los factores primos
                    String fibonacciSeries = Server.fibonacci(number);
                    String primeFactors = Server.primeFactors(number);

                    // Imprimir en el servidor la serie de Fibonacci con el username/hostname
                    System.out.println(userHostname + ":" + clientIP + ": Fibonacci Series up to " + number + ": " + fibonacciSeries);

                    // Preparar la respuesta para el cliente
                    responseMessage = String.format(
                        "Client: %s\nFibonacci Series up to %d: %s\nPrime Factors: %s",
                        userHostname, number, fibonacciSeries, primeFactors);
                } else {
                    responseMessage = "The number must be positive.";
                }
            } catch (NumberFormatException e) {
                responseMessage = "The message is not a valid positive integer.";
            }
        }

        Instant end = Instant.now();
        Duration delay = Duration.between(start, end); 
        String fullResponse = "Number: " + number + "\n" + responseMessage + "\nResponse Time: " + delay.toMillis() + " ms";

        return new Response(0, fullResponse);
    }
}
