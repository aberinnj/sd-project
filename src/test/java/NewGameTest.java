import junit.framework.TestCase;
import org.junit.Test;
import sun.tools.jar.Main;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


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
        assertNotNull(ng.gameID);
        assertEquals(0, ng.getPlayerCount());
        ng = null;
    }

    /*
    @Test
    public void testMain() throws Exception {
        ng = new NewGame();
        final InputStream old = System.in;
        final FileInputStream fis = new FileInputStream(new File(ng.base + "/src/main/java/input.txt"));
        System.setIn(fis);

        ng.main(null);

        System.setIn(old);
        ng = null;
    }
    */



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

    @Test
    public void testInvalid()
    {
        ByteArrayInputStream in = new ByteArrayInputStream("INVALID ".getBytes());
        System.setIn(in);
        ng = new NewGame();
        assertFalse(ng.setNumberOfPlayers());
        assertEquals(0, ng.getPlayerCount());
        System.setIn(System.in);
        ng = null;
    }

}
