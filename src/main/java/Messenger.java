import java.util.Vector;

public class Messenger {

    static final int MAXQUEUE = 10;
    private Vector messages = new Vector();

    public synchronized String getMessage()
            throws InterruptedException {
        notify();
        while ( messages.size() == 0 )
            wait();

        // Because declaring message as null doesn't work and not declaring message doesn't work
        String message = (String) messages.elementAt(0) + "\n";

        // read all messages in buffer queue
        for (int x = 1; messages.size()- 1 >= x; x++) {
            message += messages.elementAt(x) + "\n";
        }
        messages.clear();
        return message;
    }

    public synchronized void putMessage(String put)
            throws InterruptedException {

        while ( messages.size() == MAXQUEUE )
            wait();
        messages.addElement(put);
        notify();
    }
}