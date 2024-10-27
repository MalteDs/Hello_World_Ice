import Demo.Callback;
import com.zeroc.Ice.Current;

public class CallbackI implements Callback {
    @Override
    public void receiveMessage(String sender, String message, Current current) {
        // Asegurarse de que la salida sea visible
        System.out.println();  // Nueva línea para separar del prompt actual
        System.out.println("=== Mensaje recibido ===");
        System.out.println("De: " + sender);
        System.out.println("Mensaje: " + message);
        System.out.println("=====================");
        System.out.print("> ");  // Restaurar el prompt
        System.out.flush();  // Asegurar que todo se muestre
    }
}