import com.zeroc.Ice.*;
import java.util.concurrent.*;
import com.zeroc.Ice.Object;


public class Server {
    private static final int THREAD_POOL_SIZE = 10;
    private ExecutorService threadPool;

    public Server() {
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public void start(String[] args) {
        try (Communicator communicator = Util.initialize(args, "config.server")) {
            ObjectAdapter adapter = communicator.createObjectAdapter("Printer");
            Object printerI = new PrinterI(threadPool);
            adapter.add(printerI, Util.stringToIdentity("SimplePrinter"));
            adapter.activate();

            System.out.println("Servidor iniciado. Esperando conexiones...");
            communicator.waitForShutdown();
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
        System.out.println("Servidor cerrado.");
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start(args);
    }
}