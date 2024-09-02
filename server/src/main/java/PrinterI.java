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
            String[] parts = s.split(":", 3);
            String message = parts.length > 2 ? parts[2] : parts[1];
            try {
                number = Integer.parseInt(message.trim());
                if (number > 0) {
                    String fibonacciSeries = Server.fibonacci(number);
                    String primeFactors = Server.primeFactors(number);
                    responseMessage = String.format(
                        "Fibonacci Series up to %d: %s\nPrime Factors: %s",
                        number, fibonacciSeries, primeFactors);
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

        // System.out.println(s); 
        return new Response(0, fullResponse);
    }
}
