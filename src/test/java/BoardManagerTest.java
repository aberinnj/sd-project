import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*////////////////////////////////////////////////////////////////////////////////
BoardManager Class handles Territory and Continents, as well as the Deck
*///////////////////////////////////////////////////////////////////////////////*/
public class BoardManagerTest extends TestCase {
    private BoardManager bm;
    @Test
    public void testBoardManager() {
        bm = new BoardManager();

        assertTrue(bm.getFreeTerritories().size() > 0);
        assertEquals(42, bm.getBoardMap().keySet().size());
        assertEquals(44, bm.getGameDeck().GameDeck.size());
    }

    @Test
    public void testQueryTerritory(){
        ByteArrayInputStream in = new ByteArrayInputStream((
                "FAILURE\nALBERTA\nINDIA\nBRAZIL\nPERU\nALASKA\nALBERTA\nVENEZUELA\nARGENTINA\nJAPAN\nPERU\nINDIA\nALBERTA\nBRAZIL\nALASKA"
                ).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(System.in);

        BoardManager bm = new BoardManager();
        Player p1 = new Player(2, 25);
        bm.initializeTerritory(p1, "ALBERTA", 1);
        bm.initializeTerritory(p1, "INDIA", 25);
        bm.initializeTerritory(p1, "BRAZIL", 2);
        bm.initializeTerritory(p1, "VENEZUELA", 1);
        bm.initializeTerritory(p1, "PERU", 2);
        bm.initializeTerritory(p1, "ARGENTINA", 2);
        bm.initializeTerritory(p1, "NORTH AFRICA", 5);
        bm.initializeTerritory(p1, "MONGOLIA", 1);
        bm.initializeTerritory(p1, "KAMCHATKA", 1);


        Player p2 = new Player(0, 25);
        bm.initializeTerritory(p2, "ALASKA", 25);
        bm.initializeTerritory(p2, "NORTH WEST TERRITORY", 21);
        bm.initializeTerritory(p2, "JAPAN", 25);


        // Testing NULLs (queryTerritory returns null for invalid inputs) for ALL query types

        //Error territory not found
        assertNull(bm.queryTerritory(scanner, "", "FAILS-ANYWAY", p2, "ORIGIN IS ONLY FOR FORTIFY AND ATTACK"));
        //Error: territory not owned - ALBERTA is P1's
        assertNull(bm.queryTerritory(scanner, "", "STRENGTHEN", p2, ""));
        //INDIA is P1's
        assertNull(bm.queryTerritory(scanner, "", "ATTACK_FROM", p2, ""));
        //BRAZIL is P1's
        assertNull(bm.queryTerritory(scanner, "", "FORTIFY_FROM", p2, ""));
        //PERU is P1's
        assertNull(bm.queryTerritory(scanner, "", "FORTIFY", p2, ""));
        //Error: Territory already occupied - ALASKA already claimed
        assertNull(bm.queryTerritory(scanner, "", "INITIALIZE", p1, ""));
        //Error: Not a valid territory to attack from - ALBERTA only has 1 army
        assertNull(bm.queryTerritory(scanner, "", "ATTACK_FROM", p1, ""));
        //Error: Not a valid territory to attack from - VENEZUELA only has 1 army
        assertNull(bm.queryTerritory(scanner, "", "FORTIFY_FROM", p1, ""));
        //Error: Territory has no adjacent enemies - ARGENTINA
        assertNull(bm.queryTerritory(scanner, "", "ATTACK_FROM", p1, ""));
        //Error: Territory has no adjacent friendly territories -- JAPAN is surrounded by enemies
        assertNull(bm.queryTerritory(scanner, "", "FORTIFY_FROM", p2, ""));
        //Error: Not an enemy territory - PERU
        assertNull(bm.queryTerritory(scanner, "", "ATTACK", p1, "BRAZIL"));
        //Error: Not adjacent - INDIA
        assertNull(bm.queryTerritory(scanner, "", "ATTACK", p2, "ALASKA"));
        //Error: Not adjacent - ALBERTA
        assertNull(bm.queryTerritory(scanner, "", "FORTIFY", p1, "PERU"));
        // NOT NULL - BRAZIL is adj to PERU
        assertNotNull(bm.queryTerritory(scanner, "", "FORTIFY", p1 , "PERU"));
        assertNull(bm.queryTerritory(scanner, "", "UNKNOWN-FAILURE", p2 , "PERU"));

        System.setIn(System.in);

    }

    @Test
    public void testQueryCount() {
        ByteArrayInputStream in = new ByteArrayInputStream((
                "NaN\n3\n4\n2\n4\n1\n4\n2\n1\n2\n"
        ).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(System.in);


        BoardManager bm = new BoardManager();
        Player p = new Player(2, 3);
        bm.initializeTerritory(p, "ALASKA", 1);
        bm.initializeTerritory(p, "ALBERTA", 5);
        bm.initializeTerritory(p, "INDIA", 3);
        bm.initializeTerritory(p, "JAPAN", 2);
        //Error, cannot convert to number
        assertEquals(0, bm.queryCount(scanner, "", "ATTACK", p, ""));
        //Error, not enough armies
        assertEquals(0, bm.queryCount(scanner, "", "ATTACK", p, "INDIA"));
        // Warning defaulted to 3
        assertEquals(3, bm.queryCount(scanner, "", "ATTACK", p, "ALBERTA"));
        //Error, not enough armies
        assertEquals(0, bm.queryCount(scanner, "", "DEFEND", p, "ALASKA"));
        // Warning defaulted to 2
        assertEquals(2, bm.queryCount(scanner, "", "DEFEND", p, "ALBERTA"));
        // Error, Not enough for fortifying
        assertEquals(0, bm.queryCount(scanner, "", "FORTIFY", p, "ALASKA"));
        assertEquals(4, bm.queryCount(scanner, "", "FORTIFY", p, "ALBERTA"));
        assertEquals(2, bm.queryCount(scanner, "", "ATTACK", p, "INDIA"));
        assertEquals(1, bm.queryCount(scanner, "", "DEFEND", p, "ALASKA"));
        assertEquals(0, bm.queryCount(scanner, "", "DEFAULTED-TO-ZERO", p, "ALBERTA"));
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
