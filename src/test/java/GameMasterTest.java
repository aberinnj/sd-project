import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

public class GameMasterTest extends TestCase{
    @Test
    public void testBotIdentity(){
        try {
            _GameMaster.props = new Props(System.getProperty("user.dir") + "/src/files/secrets_example.prop");
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        CommandsHandler ch = new CommandsHandler();
        String item = ch.getBotToken();
        assertEquals("KEY7", item);
        item = ch.getBotUsername();
        assertEquals("KEY8", item);

    }
}
