import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*////////////////////////////////////////////////////////////////////////////////
BoardManager Class handles Territory and Continents, as well as the Deck
*///////////////////////////////////////////////////////////////////////////////*/
public class BoardManagerTest extends TestCase {
    private BoardManager bm;
    @Test
    public void testBoardManager() {
        String base = System.getProperty("user.dir");
        bm = new BoardManager(base + "/src/files/deck.json");

        assertFalse(bm.isAllTerritoriesInitialized());
        assertEquals(42, ((BoardManager) bm).getNumberOfTerritories());

        assertFalse(bm.isTerritoryANeighborOf("INDIA", "ALASKA"));
    }

    // Not meant for CodeCoverage but to assure that there are no errors in board-data
    @Test
    public void testBoardMapCompletion(){
        String base = System.getProperty("user.dir");
        bm = new BoardManager(base + "/src/files/deck.json");

        assertEquals(42, bm.getBoardMap().size());
    }

    @Test
    public void testDisplayNeighborsAttacking(){
        String base = System.getProperty("user.dir");
        bm = new BoardManager(base + "/src/files/deck.json");

        bm.getBoardMap().get("ALASKA").setTerritory(true, 5, new Army(5));
        bm.getBoardMap().get("ALBERTA").setTerritory(true, 5, new Army(2));

        String[] expectedEnemyNeighbors = new String[]{"KAMCHATKA", "NORTH WEST TERRITORY"};

        assertTrue(bm.getEnemyNeighbors("ALASKA").contains(expectedEnemyNeighbors[0]));
        assertTrue(bm.getEnemyNeighbors("ALASKA").contains(expectedEnemyNeighbors[1]));

    }
}
