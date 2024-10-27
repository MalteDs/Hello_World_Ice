import com.zeroc.Ice.*;

public class Server {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args, "server.cfg")) {
            ObjectAdapter adapter = communicator.createObjectAdapter("Printer");
            adapter.add(new PrinterI(), Util.stringToIdentity("SimplePrinter"));
            adapter.activate();
            System.out.println("Servidor iniciado. Esperando conexiones...");
            communicator.waitForShutdown();
        }
    }
}