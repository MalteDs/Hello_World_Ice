import com.zeroc.Ice.Current;

public interface ClientCallback {
    void receiveMessage(String sender, String message, Current current);
}