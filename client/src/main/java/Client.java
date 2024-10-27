import Demo.*;
import com.zeroc.Ice.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;

public class Client {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args, "client.cfg")) {
            // Configurar la conexión bidireccional
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "CallbackAdapter", 
                "tcp -h " + java.net.InetAddress.getLocalHost().getHostAddress()
            );
            
            // Crear e instalar el objeto callback
            CallbackI callback = new CallbackI();
            Identity ident = new Identity();
            ident.name = java.util.UUID.randomUUID().toString();
            ident.category = "";
            
            adapter.add(callback, ident);
            adapter.activate();
            
            // Obtener el proxy del printer y configurarlo para usar la conexión bidireccional
            ObjectPrx base = communicator.propertyToProxy("Printer.Proxy");
            base = base.ice_connectionId("callback");
            PrinterPrx printer = PrinterPrx.checkedCast(base.ice_preferSecure(false));
            
            if (printer == null) {
                throw new Error("Invalid proxy");
            }

            // Configurar el callback proxy
            CallbackPrx callbackProxy = CallbackPrx.uncheckedCast(
                adapter.createProxy(ident).ice_connectionId("callback")
            );

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String userHostname = System.getProperty("user.name") + "@" + java.net.InetAddress.getLocalHost().getHostName();
            String clientIP = java.net.InetAddress.getLocalHost().getHostAddress();

            // Registrar el cliente con callback
            printer.subscribe(userHostname, callbackProxy);
            System.out.println("Registered with server as: " + userHostname);

            while (true) {
                System.out.print("> ");
                String message = reader.readLine();
                if ("exit".equalsIgnoreCase(message)) {
                    printer.unsubscribe(userHostname);
                    break;
                }

                Instant start = Instant.now();
                Response response = printer.printString(userHostname + ":" + clientIP + ":" + message);
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