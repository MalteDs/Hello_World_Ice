import com.zeroc.Ice.*;
import java.util.concurrent.*;
import com.zeroc.Ice.Object;

import Demo.Response;

public class Server {
    private static final int THREAD_POOL_SIZE = 10;
    private Communicator communicator;
    private ObjectAdapter adapter;
    private ExecutorService threadPool;
    private volatile boolean isRunning;

    public Server() {
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.isRunning = true;
    }

    public void start(String[] args) {
        try {
            communicator = Util.initialize(args, "config.server");
            adapter = communicator.createObjectAdapter("Printer");
            Object printerI = new PrinterI(this);
            adapter.add(printerI, Util.stringToIdentity("SimplePrinter"));
            adapter.activate();

            System.out.println("Servidor iniciado. Esperando conexiones...");

            while (isRunning) {
                try {
                    Thread.sleep(1000); // Espera para no consumir CPU innecesariamente
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        isRunning = false;
        if (communicator != null) {
            try {
                communicator.shutdown();
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
        }
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

    public Response handleClient(Callable<Response> task) {
        try {
            return threadPool.submit(task).get();
        } catch (java.lang.Exception e) {
            e.printStackTrace();
            return new Response(1, "Error processing request");
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start(args);
    }
}