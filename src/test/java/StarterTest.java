import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;


/*////////////////////////////////////////////////////////////////////////////////
_GameStarter Class starts a _GameStarter by initializing GameManager and runs a game
*///////////////////////////////////////////////////////////////////////////////*/
public class StarterTest extends TestCase{

    private _GameStarter ng;

    @Test
    public void testNewGame() throws Exception {
        ng = new _GameStarter();
        assertNotNull(ng.scanner);
        assertNotNull(ng.base);
        assertEquals(0, ng.getPlayerCount());
        ng = null;
    }

    @Test
    public void testDefaultStart() {
        ByteArrayInputStream in = new ByteArrayInputStream(("1\n2\n" +
                "ALASKA\n" +
                "NORTH WEST TERRITORY\n" +
                "GREENLAND\n" +
                "ALBERTA\n" +
                "ONTARIO\n" +
                "QUEBEC\n" +
                "WESTERN UNITED STATES\n" +
                "EASTERN UNITED STATES\n" +
                "CENTRAL AMERICA\n" +
                "VENEZUELA\n" +
                "PERU\n" +
                "BRAZIL\n" +
                "ARGENTINA\n" +
                "NORTH AFRICA\n" +
                "CONGO\n" +
                "EGYPT\n" +
                "EAST AFRICA\n" +
                "SOUTH AFRICA\n" +
                "MADAGASCAR\n" +
                "MIDDLE EAST\n" +
                "INDIA\n" +
                "SIAM\n" +
                "INDONESIA\n" +
                "WESTERN AUSTRALIA\n" +
                "EASTERN AUSTRALIA\n" +
                "NEW GUINEA\n" +
                "CHINA\n" +
                "AFGHANISTAN\n" +
                "URAL\n" +
                "SIBERIA\n" +
                "YAKUTSK\n" +
                "KAMCHATKA\n" +
                "IRKUTSK\n" +
                "MONGOLIA\n" +
                "JAPAN\n" +
                "UKRAINE\n" +
                "SCANDINAVIA\n" +
                "ICELAND\n" +
                "GREAT BRITAIN\n" +
                "NORTHERN EUROPE\n" +
                "SOUTHERN EUROPE\n" +
                "WESTERN EUROPE\n"+

                "ALASKA\n" +
                "ALASKA\n" +
                "ALASKA\n" +
                "ALASKA\n" +
                "ALASKA\n" +

                "ALASKA\n" +
                "ALASKA\n" +
                "ALASKA\n" +
                "ALASKA\n" +
                "ALASKA\n" +

                "ALASKA\n" +
                "ALASKA\n" +
                "ALASKA\n" +
                "ALASKA\n" +
                "ALASKA\n" +

                "ALASKA\n" +
                "ALASKA\n" +
                "ALASKA\n" +
                "ALASKA\n" +

                "ALBERTA\n" +
                "ALBERTA\n" +
                "ALBERTA\n" +
                "ALBERTA\n" +
                "ALBERTA\n" +

                "ALBERTA\n" +
                "ALBERTA\n" +
                "ALBERTA\n" +
                "ALBERTA\n" +
                "ALBERTA\n" +

                "ALBERTA\n" +
                "ALBERTA\n" +
                "ALBERTA\n" +
                "ALBERTA\n" +
                "ALBERTA\n" +

                "ALBERTA\n" +
                "ALBERTA\n" +
                "ALBERTA\n" +
                "ALBERTA\n").getBytes());
        System.setIn(in);
        GameManager GM = new GameManager();
        ng = new _GameStarter();

        ng.defaultStart("anything");


        assertNotNull(GM.getPlayer(0));
        assertNotNull(GM.getPlayer(1));
        assertEquals(2, GM.playerTurnPattern.length);
        assertEquals(0, GM.getPlayer(1).getNumberOfArmies());

        assertEquals(GM.playerTurnPattern[0], GM.getBM().getBoardMap().get("ALASKA").getOccupantID());
    }

    /*
    // This test requires a complete stream of inputs for a complete game
    @Test
    public void testMain() throws Exception {
        ng = new _GameStarter();
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
        ng = new _GameStarter();
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
        ng = new _GameStarter();
        ng.setNumberOfPlayers();
        assertEquals(0, ng.getPlayerCount());

        ByteArrayInputStream in2 = new ByteArrayInputStream("1".getBytes());
        System.setIn(in2);
        ng = new _GameStarter();
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
        ng = new _GameStarter();
        assertFalse(ng.setNumberOfPlayers());
        assertEquals(0, ng.getPlayerCount());
        System.setIn(System.in);
        ng = null;
    }

    @Test
    public void testQueryGameChecker(){
        ByteArrayInputStream in = new ByteArrayInputStream("CANCEL\ngamma-not-even-a-game-key-1\ngame-2".getBytes());
        System.setIn(in);
        ArrayList<String> games1 = new ArrayList<>();

        _GameStarter st = new _GameStarter();
        assertFalse(st.queryGameChecker(games1));

        games1.add("game-1");
        games1.add("game-2");

        assertFalse(st.queryGameChecker(games1));
        assertTrue(st.queryGameChecker(games1));
        System.setIn(System.in);


    }
    /*
    @Test
    public void testProperties() throws IOException {
        String base = System.getProperty("user.dir");
        String configPath = base + "/Risk.prop";
        Properties props = new Properties();
        props.load(new FileInputStream(configPath));

        String appVersion = props.getProperty("twitter_apiKey");
        String appName = props.getProperty("twitter_apiSecretKey");
        String appGroup = props.getProperty("twitter_accessToken");
        String appDownloadAddr = props.getProperty("twitter_accessTokenSecret");


        System.out.println(appDownloadAddr);
    }*/


    @Test
    public void testUpload() {
        GameManager GM = new GameManager();
        GM.upload();
    }

    @Test
    public void testDownload() throws IOException {
        GameManager GM = new GameManager();
        GM.download();
    }

}
