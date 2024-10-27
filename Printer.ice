module Demo
{
    interface Callback {
        void receiveMessage(string sender, string message);
    }  
    class Response{
        long responseTime;
        string value;
    }
    interface Printer
    {
        Response printString(string s);
        void subscribe(string hostname, Callback* callback);
        void unsubscribe(string hostname);
    }
}
