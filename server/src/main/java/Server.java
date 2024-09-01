import java.io.*;
import java.time.Duration;
import java.time.Instant;

public class Server
{
    public static void main(String[] args)
    {
        java.util.List<String> extraArgs = new java.util.ArrayList<String>();

        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args,"config.server",extraArgs))
        {
            if(!extraArgs.isEmpty())
            {
                System.err.println("too many arguments");
                for(String v:extraArgs){
                    System.out.println(v);
                }
            }
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Printer");
            com.zeroc.Ice.Object object = new PrinterI();
            adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("SimplePrinter"));
            adapter.activate();
            communicator.waitForShutdown();
        }
    }

    // Métodos para calcular Fibonacci y factores primos
    public static String fibonacci(int n) {
        if (n <= 0) return "";
        StringBuilder result = new StringBuilder();
        int a = 0, b = 1;
        result.append(a).append(" ");
        for (int i = 1; i < n; i++) {
            result.append(b).append(" ");
            int next = a + b;
            a = b;
            b = next;
        }
        return result.toString().trim();
    }

    public static String primeFactors(int n) {
        StringBuilder result = new StringBuilder();
        while (n % 2 == 0) {
            result.append(2).append(" ");
            n /= 2;
        }
        for (int i = 3; i <= Math.sqrt(n); i += 2) {
            while (n % i == 0) {
                result.append(i).append(" ");
                n /= i;
            }
        }
        if (n > 2) {
            result.append(n).append(" ");
        }
        return result.toString().trim();
    }
}
