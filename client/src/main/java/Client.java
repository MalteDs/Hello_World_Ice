import Demo.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

public class Client
{
    public static void main(String[] args)
    {
        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args,"config.client",extraArgs))
        {
            Response response = null;
            Demo.PrinterPrx service = Demo.PrinterPrx
                    .checkedCast(communicator.propertyToProxy("Printer.Proxy"));
            
            if(service == null)
            {
                throw new Error("Invalid proxy");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String message;
            String userHostname = System.getProperty("user.name") + ":" + java.net.InetAddress.getLocalHost().getHostName();
            String clientIP = java.net.InetAddress.getLocalHost().getHostAddress();

            while (true) {
                System.out.print("Enter message (or 'exit' to quit): ");
                message = reader.readLine();
                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }
                message = userHostname + ":" + clientIP + ":" + message;

                Instant start = Instant.now(); // Registrar el tiempo antes de enviar el mensaje
                
                response = service.printString(message);

                Instant end = Instant.now(); // Registrar el tiempo después de recibir la respuesta
                Duration delay = Duration.between(start, end); // Calcular el tiempo de delay

                System.out.println("Server response: " + response.value);
                System.out.println("Time taken for the response: " + delay.toMillis() + " ms");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
