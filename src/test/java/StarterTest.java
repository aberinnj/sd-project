import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;



/*////////////////////////////////////////////////////////////////////////////////
_Starter Class starts a _Starter by initializing GameManager and runs a game
todo: Write test case for main method by providing File
*///////////////////////////////////////////////////////////////////////////////*/
public class StarterTest extends TestCase{

    private _Starter ng;

    @Test
    public void testNewGame() throws Exception {
        ng = new _Starter();
        assertNotNull(ng.scanner);
        assertNotNull(ng.base);
        assertEquals(0, ng.getPlayerCount());
        ng = null;
    }

    /*
    // This test requires a complete stream of inputs for a complete game
    @Test
    public void testMain() throws Exception {
        ng = new _Starter();
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
        ng = new _Starter();
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
        ng = new _Starter();
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
        ng = new _Starter();
        assertFalse(ng.setNumberOfPlayers());
        assertEquals(0, ng.getPlayerCount());
        System.setIn(System.in);
        ng = null;
    }

}
