import org.junit.Test;
import junit.framework.TestCase;
import java.util.ArrayList;
import java.util.List;

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
        cooper.setTerritories(new ArrayList<String>(){{add("KAMCHATKA"); add("MONGOLIA");}});

        String[] m =  new String[]{"KAMCHATKA", "MONGOLIA"};

        for(String m_member: m)
        {
            assertTrue(cooper.getTerritories().contains(m_member));
        }
        assertFalse(cooper.getTerritories().contains("ONTARIO"));
        assertFalse(cooper.getTerritories().contains("ALBERTA"));
        assertFalse(cooper.getTerritories().contains("BRAZIL"));
        assertFalse(cooper.getTerritories().contains("CONGO"));
    }


    @Test
    public void testPlayer() throws Exception {
        String base = System.getProperty("user.dir");
        BoardManager bm = new BoardManager(base + "/src/files/deck.json");
        Player michaels;


        michaels = new Player(0, 25);

        assertFalse(michaels.isBaseEmpty());
        assertEquals(0, michaels.getId());

        michaels.addTerritories("ALASKA");
        assertTrue(michaels.ifPlayerHasTerritory("ALASKA"));
        assertFalse(michaels.isPlayerTheWinner(((BoardManager) bm)));

        michaels.addTerritories("ARGENTINA");
        michaels.loseTerritories("WESTERN EUROPE");
        assertTrue( michaels.ifPlayerHasTerritory("ARGENTINA"));


        bm.addOccupantsTo("ALASKA", 5, "INFANTRY");
        michaels.shipArmy();

        michaels.fortifyTerritory(bm, "ALASKA", "ALBERTA", 2);

        michaels.loseTerritories("ALASKA");
        assertFalse(michaels.ifPlayerHasTerritory("ALASKA"));

        assertFalse(michaels.isBaseEmpty());

        michaels.shipArmy(); //armies = 39
        assertNotSame(20, ((Player) michaels).getRemainingArmies());

        assertEquals(1, ((Player) michaels).numOfTerritories());
    }

    /*
    @Test
    public void testDisplayNeighborsAttacking(){
        String base = System.getProperty("user.dir");
        BoardManager bm = new BoardManager(base + "/src/files/deck.json");
        Player hobson;

        hobson = new Player(5, 27);
        hobson.addTerritories("ALASKA");
        hobson.addTerritories("BRAZIL");
        hobson.addTerritories("JAPAN");

    }
    */

}
