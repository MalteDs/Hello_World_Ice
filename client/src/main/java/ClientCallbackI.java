class ClientCallbackI implements ClientCallback {
    @Override
    public void receiveMessage(String sender, String message, com.zeroc.Ice.Current current) {
        System.out.println("Received message from " + sender + ": " + message);
    }
}