
import junit.framework.TestCase;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class GameManagerTest extends TestCase {

    public static String base = System.getProperty("user.dir");

    Object bm = new BoardManager(base + "/src/main/java/mapSource.json", base + "/src/main/java/deck.json");
    Object dice = new Dice();
    Object player = new Player(0, 40);

    @Test
    public void testPlayer() throws Exception {

        assertEquals(false, ((Player) player).isBaseEmpty());
        assertEquals(0, ((Player) player).getId());

        ((Player) player).addTerritories("TEXAS");
        assertEquals(true, ((Player) player).ifPlayerHasTerritory("TEXAS"));
        assertEquals(false,((Player) player).isPlayerTheWinner(((BoardManager) bm)));

        ((Player) player).addTerritories("OKLAHOMA");
        assertEquals(true, ((Player) player).ifPlayerHasTerritory("OKLAHOMA"));


        ((BoardManager) bm).addOccupantsTo("TEXAS", 2, "INFANTRY");
        ((Player) player).shipArmy();

        ((Player) player).fortifyTerritory(((BoardManager) bm), "TEXAS", "OKLAHOMA", 2);

        ((Player) player).loseTerritories("TEXAS");
        assertEquals(false, ((Player) player).ifPlayerHasTerritory("TEXAS"));

        assertEquals(false, ((Player) player).isBaseEmpty());

        ((Player) player).shipArmy(); //armies = 39
        assertNotSame(20, ((Player) player).getRemainingArmies());

        List<String> test = new ArrayList<String>();
        test.add("OKLAHOMA");

        //assertEquals(((Player) player).territoriesCopy(), test);

        assertEquals(1, ((Player) player).numOfTerritories());
    }

    @Test(expected = FileNotFoundException.class)
    public void testBoardFail() {
        BoardManager fail = new BoardManager("failpath1", "failpath2");
    }

    @Test
    public void testBoardManager() {

        assertEquals(false, ((BoardManager) bm).isAllTerritoriesInitialized());
        assertEquals(42, ((BoardManager) bm).getNumberOfTerritories());

        Army larmy = new Army(10);
        Army rarmy = new Army(10);

        List<String> l = new ArrayList<String>();
        l.add("Right");

        List<String> r = new ArrayList<String>();
        r.add("Right");

        Territory Left = new Territory(true, 0, ((Army) larmy), l);
        Territory Right = new Territory(true, 1, ((Army) rarmy), r);

        assertEquals(false, ((BoardManager) bm).isTerritoryANeighborOf("INDIA", "ALASKA"));

        List<String> neighbors = new ArrayList<String>();
        neighbors.add("KAMCHATKA");
        neighbors.add("NORTH WEST TERRITORY");
        neighbors.add("ALBERTA");

        BoardManager test = new BoardManager(base + "/src/main/java/mapSource.json", base + "/src/main/java/deck.json");
        HashMap<String, Territory> map = (HashMap<String, Territory>) test.getBoardMap();
        assertEquals(map, test.getBoardMap());

        test.setBoardMap(map);

    }

    @Test
    public void testGameManager() throws Exception {
        GameManager gm = new GameManager();

        String[] mainArgs= {"This Game SUCKS!!!"};
    }

    @Test
    public void testDeck() {
        Deck deck = new Deck(base + "/src/main/java/deck.json");
        Deck fail = new Deck(base + "thisaintnodeck.json");
        assertNotNull(deck.drawCard());
        assertNotNull(deck.drawCard());
        assertNotNull(deck.drawCard());
        assertNotNull(deck.drawCard());
        assertNotNull(deck.drawCard());
        assertNotSame(deck.drawCard(), Deck.drawCard());
    }

    @Test
    public void testDice() throws Exception {

        ((Dice) dice).roll();
        int roll = ((Dice) dice).getDiceValue();
        int high = 7;
        int low = 0;
        assertTrue("Error, random is too high", high >= roll);
        assertTrue("Error, random is too low",  low  <= roll);
    }

    @Test
    public void testArmy() {
        Object army = new Army(10);
        int armies = ((Army) army).getInfantryCount();
        assertEquals(10, ((Army) army).getInfantryCount());

        ((Army) army).addInfantryCount(20);
        armies = ((Army) army).getInfantryCount();
        assertEquals(30, ((Army) army).getInfantryCount());

        ((Army) army).loseInfantry(20);
        assertEquals(10, ((Army) army).getInfantryCount());
    }
}