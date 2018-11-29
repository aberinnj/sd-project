import org.junit.Test;
import junit.framework.TestCase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/*////////////////////////////////////////////////////////////////////////////////
PlayerTest
*///////////////////////////////////////////////////////////////////////////////*/
public class PlayerTest extends TestCase{

    @Test
    public void testSetTerritories()
    {
        Player cooper;
        cooper = new Player(0, 5);
        cooper.addTerritories("ALBERTA");
        cooper.addTerritories("ONTARIO");
        cooper.addTerritories("BRAZIL");
        cooper.addTerritories("CONGO");

        assertTrue(cooper.getTerritories().contains("ONTARIO"));
        assertTrue(cooper.getTerritories().contains("ALBERTA"));
        assertTrue(cooper.getTerritories().contains("BRAZIL"));
        assertTrue(cooper.getTerritories().contains("CONGO"));
    }


    @Test
    public void testPlayer() throws Exception {
        BoardManager bm = new BoardManager();
        Player michaels;


        michaels = new Player(0, 25);

        assertFalse(michaels.getNumberOfArmies() == 0);
        assertEquals(0, michaels.getId());

        bm.initializeTerritory(michaels, "ALASKA", 2);
        assertTrue(michaels.getTerritories().contains("ALASKA"));
        assertFalse(michaels.isPlayerTheWinner(((BoardManager) bm)));

        bm.initializeTerritory(michaels, "ALBERTA", 5);
        assertTrue( michaels.getTerritories().contains("ALBERTA"));


        bm.initializeTerritory(michaels, "INDIA", 5);
        bm.addOccupantsTo("INDIA", 5);
        assertEquals(10, bm.getOccupantCount("INDIA"));
        michaels.loseTerritories("INDIA");
        michaels.loseArmies(1);

        bm.fortifyTerritory("ALASKA", "ALBERTA", 2);
        michaels.loseTerritories("ALASKA");
        assertFalse(michaels.getTerritories().contains("ALASKA"));

        assertFalse(michaels.getNumberOfArmies()==0);

        michaels.loseArmies(1);
        assertNotSame(20, ((Player) michaels).getNumberOfArmies());

        assertEquals(1, ((Player) michaels).getTerritories().size());
    }

    @Test
    public void testContinentsOwned(){
        BoardManager bm = new BoardManager();
        Player p1 = new Player(1, 20);

        p1.getTerritories().add("ALASKA");
        p1.getTerritories().add("NORTH WEST TERRITORY");
        p1.getTerritories().add("ALBERTA");
        assertEquals(0, p1.getContinentsOwned(bm));

        p1.getTerritories().add("ONTARIO");
        p1.getTerritories().add("QUEBEC");
        p1.getTerritories().add("GREENLAND");
        p1.getTerritories().add("WESTERN UNITED STATES");
        p1.getTerritories().add("EASTERN UNITED STATES");
        p1.getTerritories().add("CENTRAL AMERICA");

        assertEquals(5, p1.getContinentsOwned(bm));
        p1.getTerritories().add("VENEZUELA");
        p1.getTerritories().add("BRAZIL");
        p1.getTerritories().add("PERU");
        p1.getTerritories().add("ARGENTINA");
        assertEquals(7, p1.getContinentsOwned(bm));
        p1.getTerritories().add("WESTERN AUSTRALIA");
        p1.getTerritories().add("INDONESIA");
        p1.getTerritories().add("EASTERN AUSTRALIA");
        p1.getTerritories().add("NEW GUINEA");
        assertEquals(9, p1.getContinentsOwned(bm));
        p1.getTerritories().add("SIAM");
        p1.getTerritories().add("INDIA");
        p1.getTerritories().add("AFGHANISTAN");
        p1.getTerritories().add("URAL");
        p1.getTerritories().add("SIBERIA");
        p1.getTerritories().add("MONGOLIA");
        p1.getTerritories().add("CHINA");
        p1.getTerritories().add("MIDDLE EAST");
        p1.getTerritories().add("JAPAN");
        p1.getTerritories().add("YAKUTSK");
        p1.getTerritories().add("IRKUTSK");
        p1.getTerritories().add("KAMCHATKA");
        assertEquals(16, p1.getContinentsOwned(bm));
        p1.getTerritories().add("NORTH AFRICA");
        p1.getTerritories().add("EGYPT");
        p1.getTerritories().add("CONGO");
        p1.getTerritories().add("EAST AFRICA");
        p1.getTerritories().add("SOUTH AFRICA");
        p1.getTerritories().add("MADAGASCAR");
        assertEquals(19, p1.getContinentsOwned(bm));
        p1.getTerritories().add("WESTERN EUROPE");
        p1.getTerritories().add("GREAT BRITAIN");
        p1.getTerritories().add("ICELAND");
        p1.getTerritories().add("SCANDINAVIA");
        p1.getTerritories().add("NORTHERN EUROPE");

        p1.getTerritories().add("SOUTHERN EUROPE");
        p1.getTerritories().add("UKRAINE");
        assertEquals(24, p1.getContinentsOwned(bm));
    }

    @Test
    public void testOtherPlayerStuff() {
        _GameMaster.gamesListing = new HashMap<>();
        _GameMaster.allPlayersAndTheirGames = new HashMap<>();

        ChatInput INPUT = new ChatInput();
        INPUT.command = "/join";
        INPUT.args = new ArrayList<String>(){{add("game");}};

        Responses.onCreate(2, "game", "bobby", 1234567);
        Responses.onJoin(INPUT, 1, "dobby", (long)123);

        Game game = CommandUtils.getGame(2);
        game.setPlayerList();

        Player player = CommandUtils.getPlayer(game);

        Stack<Card> deck = new Stack<>();
        Card c = game.BM.gameDeck.draw();
        deck.push(c);

        player.setCardStack(deck);

        assertEquals("bobby", player.getUsername());
        assertEquals(1234567, player.getChat_id());
        assertEquals(1, player.getTotalCards());

    }

}