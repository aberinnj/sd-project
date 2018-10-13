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
    public void testBoardManager() {
        bm = new BoardManager();

        assertFalse(bm.isAllTerritoriesInitialized());
        assertEquals(42, bm.getNumberOfTerritories());

        assertFalse(bm.isTerritoryANeighborOf("INDIA", "ALASKA"));
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
        playerTerritories.addAll(bm.getTerritories(p1, false));
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
            fightingPlayerTerritories.addAll(bm.getTerritories(p1, true));
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
    public void testGetAdjacentEnemyTerritories(){
        bm = new BoardManager();

        Player p1 = new Player(0, 5);
        p1.addTerritories("PERU");
        p1.addTerritories("ARGENTINA");
        bm.getBoardMap().get("PERU").setTerritory(true, p1.getId(), new Army(5));
        bm.getBoardMap().get("ARGENTINA").setTerritory(true, p1.getId(), new Army(5));

        List<String> playerTerritories = new ArrayList<String>();
        playerTerritories.addAll(bm.getAllAdjacentEnemyTerritories(p1));
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

        assertTrue(bm.getEnemyNeighbors("ALASKA").contains(expectedEnemyNeighbors[0]));
        assertTrue(bm.getEnemyNeighbors("ALASKA").contains(expectedEnemyNeighbors[1]));

    }
}
