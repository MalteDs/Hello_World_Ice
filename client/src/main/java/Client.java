import Demo.*;
import com.zeroc.Ice.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;

public class Client {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args, "config.client")) {
            ObjectAdapter adapter = communicator.createObjectAdapter("");
            ClientCallbackI callback = new ClientCallbackI();
            adapter.activate();

            PrinterPrx printer = PrinterPrx.checkedCast(communicator.propertyToProxy("Printer.Proxy"));
            if (printer == null) {
                throw new Error("Invalid proxy");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String userHostname = System.getProperty("user.name") + ":" + java.net.InetAddress.getLocalHost().getHostName();
            String clientIP = java.net.InetAddress.getLocalHost().getHostAddress();

            // Register the client
            Response response = printer.printString(userHostname + ":" + clientIP + ":register ");
            System.out.println("Server response: " + response.value);

            while (true) {
                System.out.print("Enter message (or 'exit' to quit): ");
                String message = reader.readLine();
                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }

                Instant start = Instant.now();
                response = printer.printString(userHostname + ":" + clientIP + ":" + message);
                Instant end = Instant.now();
                Duration delay = Duration.between(start, end);

                System.out.println("Server response: " + response.value);
                System.out.println("Time taken for the response: " + delay.toMillis() + " ms");
            }
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }
}

class ClientCallbackI implements ClientCallback {
    @Override
    public void receiveMessage(String sender, String message, Current current) {
        System.out.println("Received message from " + sender + ": " + message);
    }
}