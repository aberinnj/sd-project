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
                "NoTerritory\n"     +
                "ALASKA\n"          +
                "KAMCHATKA\n"       +
                "NORTH WEST TERRITORY\n"    +

                "ALASKA\n"          +
                "ALBERTA\n" +
                        "BRAZIL\n"    +
                        "ALASKA\n"          +
                        "ALBERTA\n" +

                        "ALASKA\n"  +
                        "ALBERTA\n" +
                        "ALBERTA\n" +

                        "ALBERTA\n" +
                        "INDIA").getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(System.in);

        BoardManager bm = new BoardManager();
        Player p1 = new Player(2, 25);
        bm.initializeTerritory(p1, "ALBERTA", 1);
        bm.initializeTerritory(p1, "INDIA", 25);
        bm.initializeTerritory(p1, "BRAZIL", 2);
        bm.initializeTerritory(p1, "VENEZUELA", 2);
        bm.initializeTerritory(p1, "PERU", 2);
        bm.initializeTerritory(p1, "ARGENTINA", 2);
        bm.initializeTerritory(p1, "NORTH AFRICA", 5);


        Player p2 = new Player(0, 25);
        bm.initializeTerritory(p2, "ALASKA", 25);
        bm.initializeTerritory(p2, "KAMCHATKA", 25);
        bm.initializeTerritory(p2, "NORTH WEST TERRITORY", 21);

        assertNull(bm.queryTerritory(scanner, "Territory: ", "NULL", p1, ""));
        assertNotNull(bm.queryTerritory(scanner, "Territory: ", "STRENGTHEN", p2, ""));
        assertNull(bm.queryTerritory(scanner, "Territory: ", "INITIALIZE", p1, ""));
        // territory not owned
        assertNull(bm.queryTerritory(scanner, "Territory: ", "ATTACK_FROM", p1, ""));

        // territory not
        assertNull(bm.queryTerritory(scanner, "Territory: ", "ATTACK_FROM", p1, ""));
        // territory not valid to attack from
        assertNull(bm.queryTerritory(scanner, "Territory: ", "ATTACK_FROM", p1, ""));
        // territory has no neighbors to attack
        assertNull(bm.queryTerritory(scanner, "Territory: ", "ATTACK_FROM", p1, ""));

        // territory  is not an enemy territory
        assertNull(bm.queryTerritory(scanner, "Territory: ", "ATTACK", p2, "INDIA"));
        // territory is not adjacent to origin
        assertNull(bm.queryTerritory(scanner, "Territory: ", "ATTACK", p1, "INDIA"));

        // territory not owned
        assertNull(bm.queryTerritory(scanner, "Territory: ", "FORTIFY_FROM", p1, ""));
        // territory not valid to fortify from
        assertNull(bm.queryTerritory(scanner, "Territory: ", "FORTIFY_FROM", p1, ""));
        // territory not valid to attack from
        assertNull(bm.queryTerritory(scanner, "Territory: ", "FORTIFY_FROM", p1, ""));

        // territory  is not an enemy territory
        assertNull(bm.queryTerritory(scanner, "Territory: ", "FORTIFY", p2, "INDIA"));
        // territory is not adjacent to origin
        assertNull(bm.queryTerritory(scanner, "Territory: ", "FORTIFY", p2, "ALBERTA"));

        System.setIn(System.in);

    }

    @Test
    public void testQueryCount() {
        ByteArrayInputStream in = new ByteArrayInputStream((
                "NAN\n3\n5\n3\n2\n1\n2\n4").getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(System.in);

        BoardManager bm = new BoardManager();
        Player p = new Player(2, 3);
        bm.initializeTerritory(p, "ALASKA", 2);
        bm.initializeTerritory(p, "ALBERTA", 5);
        bm.initializeTerritory(p, "INDIA", 1);

        assertEquals(0, bm.queryCount(scanner, "", "ATTACK", p, "ALASKA"));
        assertEquals(3, bm.queryCount(scanner, "", "ATTACK", p, "ALBERTA"));
        assertEquals(0, bm.queryCount(scanner, "", "DEFEND", p, "ALASKA"));
        assertEquals(2, bm.queryCount(scanner, "", "DEFEND", p, "ALBERTA"));
        assertEquals(2, bm.queryCount(scanner, "", "DEFEND", p, "ALASKA"));
        assertEquals(0, bm.queryCount(scanner, "", "FORTIFY", p, "INDIA"));
        assertEquals(0, bm.queryCount(scanner, "", "FORTIFY", p, "ALASKA"));
        assertEquals(0, bm.queryCount(scanner, "", "FORTIFY", p, "INDIA"));
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
