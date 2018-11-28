import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;

public class MessengerTest extends TestCase {

    @Test
    public void testMessenger() {
        Messenger msg = new Messenger();

        ArrayList<String> list = new ArrayList<>();
        list.add("Hello");
        list.add("World");

        msg.putMessage(list);
        assertNotNull(msg.getMessage());
    }

}
