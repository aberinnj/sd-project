import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.*;

/*////////////////////////////////////////////////////////////////////////////////
NewGame Class starts a NewGame by initializing GameManager and runs a game
todo: Write test case for main method by providing File
*///////////////////////////////////////////////////////////////////////////////*/
public class NewGameTest extends TestCase{

    private NewGame ng;

    @Test
    public void testNewGame() throws Exception {
        ng = new NewGame();
        assertNotNull(ng.scanner);
        assertNotNull(ng.base);
        assertEquals(0, ng.getPlayerCount());
        ng = null;
    }

    @Test
    public void testPass()
    {
        ByteArrayInputStream in = new ByteArrayInputStream("2".getBytes());
        System.setIn(in);
        ng = new NewGame();
        ng.setNumberOfPlayers();
        assertEquals(2, ng.getPlayerCount());
        System.setIn(System.in);
        ng = null;
    }

    @Test
    public void testFail()
    {
        ByteArrayInputStream in = new ByteArrayInputStream("9".getBytes());
        System.setIn(in);
        ng = new NewGame();
        ng.setNumberOfPlayers();
        assertEquals(0, ng.getPlayerCount());
        System.setIn(System.in);
        ng = null;
    }

}
