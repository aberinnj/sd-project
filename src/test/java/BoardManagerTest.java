
import junit.framework.TestCase;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

/*////////////////////////////////////////////////////////////////////////////////
BoardManager Class handles Territory and Continents, as well as the Deck
*///////////////////////////////////////////////////////////////////////////////*/
public class BoardManagerTest extends TestCase {
    private BoardManager bm;

    @Test
    public void testBoardManagerDeck() {
        BoardManager BM = new BoardManager();
        assertEquals(BM.boardMap, BM.getBoardMap());
        assertEquals(BM.gameDeck, BM.getGameDeck());

        assertNotNull(BM.gameDeck.draw());

        List<Card> deck = (List<Card>) BM.gameDeck.draw();

        BM.newDeck(deck);
        BM.gameDeck.draw();
        assertNull(BM.gameDeck);

        assertNotNull(BM.getContinentsMap("MONGOLIA"));
        assertNotNull(BM.getFreeTerritories());
    }

    @Test
    public void testBoardManager() {
        bm = new BoardManager();

        assertTrue(bm.getFreeTerritories().size() > 0);
        assertEquals(42, bm.getBoardMap().keySet().size());
        assertEquals(44, bm.getGameDeck().GameDeck.size());
    }

    // Not meant for CodeCoverage but to assure that there are no errors in board-data
    @Test
    public void testBoardMapCompletion(){
        bm = new BoardManager();

        assertEquals(42, bm.getBoardMap().size());
    }

    @Test
    public void testGetTerritories(){
        bm = new BoardManager();

        Player p1 = new Player(0, 5);
        p1.addTerritories("PERU");
        p1.addTerritories("ARGENTINA");
        p1.addTerritories("MADAGASCAR");

        List<String> playerTerritories = new ArrayList<String>();
        playerTerritories.addAll(bm.getAbleTerritories(p1, false));
        String[] expectedTerritories = new String[]{"PERU", "ARGENTINA", "MADAGASCAR"};
        for(String expected: expectedTerritories)
        {
            assertTrue(playerTerritories.contains(expected));
        }


        try{
            bm.getBoardMap().get("PERU").setTerritory(true, p1.getId(), new Army(5));
            bm.getBoardMap().get("ARGENTINA").setTerritory(true, p1.getId(), new Army(5));
            bm.getBoardMap().get("MADAGASCAR").setTerritory(true, p1.getId(), new Army(1));

            List<String> fightingPlayerTerritories = new ArrayList<String>();
            fightingPlayerTerritories.addAll(bm.getAbleTerritories(p1, true));
            String[] expectedFightingTerritories = new String[]{"PERU", "ARGENTINA"};
            for(String expected: expectedFightingTerritories)
            {
                assertTrue(fightingPlayerTerritories.contains(expected));
            }
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
    @Test
    public void testGetAdjacentTerritories(){
        bm = new BoardManager();

        Player p2 = new Player(0, 5);
        bm.initializeTerritory(p2, "PERU", 2);
        bm.initializeTerritory(p2, "ARGENTINA", 2);
        bm.initializeTerritory(p2, "VENEZUELA", 2);
        bm.initializeTerritory(p2, "BRAZIL", 2);

        List<String> playerTerritories = new ArrayList<String>();
        playerTerritories.addAll(bm.getAllAdjacentTerritories(p2.getId(), "PERU"));
        String[] expectedTerritories = new String[]{"ARGENTINA", "VENEZUELA", "BRAZIL"};
        for(String expected: expectedTerritories)
        {
            assertTrue(playerTerritories.contains(expected));
        }
    }

    @Test
    public void testGetAdjacentEnemyTerritories(){
        bm = new BoardManager();

        Player p1 = new Player(0, 5);
        p1.addTerritories("PERU");
        p1.addTerritories("ARGENTINA");
        bm.getBoardMap().get("PERU").setTerritory(true, p1.getId(), new Army(5));
        bm.getBoardMap().get("ARGENTINA").setTerritory(true, p1.getId(), new Army(5));

        List<String> playerTerritories = new ArrayList<String>();
        playerTerritories.addAll(bm.getAllAdjacentEnemyTerritories(p1.getId(), "PERU"));
        playerTerritories.addAll(bm.getAllAdjacentEnemyTerritories(p1.getId(), "ARGENTINA"));
        String[] expectedTerritories = new String[]{"VENEZUELA", "BRAZIL"};
        for(String expected: expectedTerritories)
        {
            assertTrue(playerTerritories.contains(expected));
        }
    }


    @Test
    public void testDisplayNeighborsAttacking(){
        bm = new BoardManager();

        bm.getBoardMap().get("ALASKA").setTerritory(true, 5, new Army(5));
        bm.getBoardMap().get("ALBERTA").setTerritory(true, 5, new Army(2));

        String[] expectedEnemyNeighbors = new String[]{"KAMCHATKA", "NORTH WEST TERRITORY"};

        assertTrue(bm.getAllAdjacentEnemyTerritories(5, "ALASKA").contains(expectedEnemyNeighbors[0]));
        assertTrue(bm.getAllAdjacentEnemyTerritories(5, "ALASKA").contains(expectedEnemyNeighbors[1]));

    }
}