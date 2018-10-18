
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.*;


/*///////////////////////////////////////////////////////////////////////////////
GameManagerTest
todo: complete test below, should test MakeTurn AND setgame. Undo once. Check if game is setup as expected
 *//////////////////////////////////////////////////////////////////////////////
public class GameManagerTest extends TestCase {

    GameManager GM;

    @Test
    public void testGameManagerInit(){
        GM = new GameManager();

        assertNotNull(GM.getBM());
        assertNotNull(GM.TM);

        GM.initializeAsNormal(3);
        assertNotNull(GM.getPlayer(2));
        assertEquals(35, GM.getPlayer(2).getNumberOfArmies());
    }

    @Test
    public void testGameManagerPlayerList() throws Exception {
        Player[] TestPlayerList1;
        Player[] TestPlayerList2;
        Player[] TestPlayerList3;
        Player[] TestPlayerList4;
        GM = new GameManager();
        GM.initializeAsNormal(3);

        TestPlayerList1 = GM.setPlayerList(2);
        assertEquals(40, TestPlayerList1[0].getNumberOfArmies());
        TestPlayerList2 = GM.setPlayerList(4);
        assertEquals(30, TestPlayerList2[0].getNumberOfArmies());
        TestPlayerList3 = GM.setPlayerList(5);
        assertEquals(25, TestPlayerList3[0].getNumberOfArmies());
        TestPlayerList4 = GM.setPlayerList(6);
        assertEquals(20, TestPlayerList4[0].getNumberOfArmies());
    }

    @Test
    public void testGameOver() {
        GM = new GameManager();
        GM.initializeAsNormal(3);

        GM.getPlayer(2).addTerritories("ALASKA");
        assertFalse(GM.isGameOver());
        GM.getPlayer(2).addTerritories("NORTH WEST TERRITORY");
        GM.getPlayer(2).addTerritories("GREENLAND");
        GM.getPlayer(2).addTerritories("ALBERTA");
        GM.getPlayer(2).addTerritories("ONTARIO");
        GM.getPlayer(2).addTerritories("QUEBEC");
        GM.getPlayer(2).addTerritories("WESTERN UNITED STATES");
        GM.getPlayer(2).addTerritories("EASTERN UNITED STATES");
        GM.getPlayer(2).addTerritories("CENTRAL AMERICA");
        GM.getPlayer(2).addTerritories("VENEZUELA");
        GM.getPlayer(2).addTerritories("PERU");
        GM.getPlayer(2).addTerritories("BRAZIL");
        GM.getPlayer(2).addTerritories("ARGENTINA");
        GM.getPlayer(2).addTerritories("NORTH AFRICA");
        GM.getPlayer(2).addTerritories("CONGO");
        GM.getPlayer(2).addTerritories("EGYPT");
        GM.getPlayer(2).addTerritories("EAST AFRICA");
        GM.getPlayer(2).addTerritories("SOUTH AFRICA");
        GM.getPlayer(2).addTerritories("MADAGASCAR");
        GM.getPlayer(2).addTerritories("MIDDLE EAST");
        GM.getPlayer(2).addTerritories("INDIA");
        GM.getPlayer(2).addTerritories("SIAM");
        GM.getPlayer(2).addTerritories("INDONESIA");
        GM.getPlayer(2).addTerritories("WESTERN AUSTRALIA");
        GM.getPlayer(2).addTerritories("EASTERN AUSTRALIA");
        GM.getPlayer(2).addTerritories("NEW GUINEA");
        GM.getPlayer(2).addTerritories("CHINA");
        GM.getPlayer(2).addTerritories("AFGHANISTAN");
        GM.getPlayer(2).addTerritories("URAL");
        GM.getPlayer(2).addTerritories("SIBERIA");
        GM.getPlayer(2).addTerritories("YAKUTSK");
        GM.getPlayer(2).addTerritories("KAMCHATKA");
        GM.getPlayer(2).addTerritories("IRKUTSK");
        GM.getPlayer(2).addTerritories("MONGOLIA");
        GM.getPlayer(2).addTerritories("JAPAN");
        GM.getPlayer(2).addTerritories("UKRAINE");
        GM.getPlayer(2).addTerritories("SCANDINAVIA");
        GM.getPlayer(2).addTerritories("ICELAND");
        GM.getPlayer(2).addTerritories("GREAT BRITAIN");
        GM.getPlayer(2).addTerritories("NORTHERN EUROPE");
        GM.getPlayer(2).addTerritories("SOUTHERN EUROPE");
        GM.getPlayer(2).addTerritories("WESTERN EUROPE");
        assertTrue(GM.isGameOver());
    }


    //Assumptions:
    // There are 42 territories as listed below
    // A 2-player game gives 40 infantry -- yielding 19 infantry left to divide among player territories

    @Test
    public void testRunSetup(){
        ByteArrayInputStream in = new ByteArrayInputStream(("" +
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
                "ALBERTA\n"

                ).getBytes());
        System.setIn(in);

        Scanner thisScanner = new Scanner(System.in);
        GM = new GameManager();
        GM.initializeAsNormal(2);
        GM.runSetup(GM, thisScanner);
        // Test Below

        assertTrue(GM.getPlayer(0).getNumberOfArmies() == 0);
        assertTrue(GM.getPlayer(1).getNumberOfArmies()==0);

        BoardManager BM = GM.getBM();
        assertEquals(0, BM.getFreeTerritories().size());

        System.setIn(System.in);
    }


    // TEST INCOMPLETE
    @Test
    public void testSetGame(){
        GameManager GM = new GameManager();
        // total of 42 infantry to init board
        GM.initializeAsNormal(2);
        Player p1 = GM.getPlayer(0);
        Player p2 = GM.getPlayer(1);

        GM.getBM().initializeTerritory(p1, "ALASKA", 1);
        GM.getBM().initializeTerritory(p1, "NORTH WEST TERRITORY", 1);
        GM.getBM().initializeTerritory(p1, "GREENLAND", 1);
        GM.getBM().initializeTerritory(p1, "ALBERTA", 1);
        GM.getBM().initializeTerritory(p1, "ONTARIO", 1);
        GM.getBM().initializeTerritory(p1, "QUEBEC", 1);
        GM.getBM().initializeTerritory(p1, "WESTERN UNITED STATES", 1);
        GM.getBM().initializeTerritory(p1, "EASTERN UNITED STATES", 1);
        GM.getBM().initializeTerritory(p1, "CENTRAL AMERICA", 1);
        GM.getBM().initializeTerritory(p1, "VENEZUELA", 1);
        GM.getBM().initializeTerritory(p1, "PERU", 1);
        GM.getBM().initializeTerritory(p1, "BRAZIL", 1);
        GM.getBM().initializeTerritory(p1, "ARGENTINA", 1);
        GM.getBM().initializeTerritory(p1, "NORTH AFRICA", 1);
        GM.getBM().initializeTerritory(p1, "CONGO", 1);

        GM.getBM().initializeTerritory(p2, "EGYPT", 1);
        GM.getBM().initializeTerritory(p2, "EAST AFRICA", 1);
        GM.getBM().initializeTerritory(p2, "SOUTH AFRICA", 1);
        GM.getBM().initializeTerritory(p2, "MADAGASCAR", 1);
        GM.getBM().initializeTerritory(p2, "MIDDLE EAST", 1);
        GM.getBM().initializeTerritory(p2, "INDIA", 1);
        GM.getBM().initializeTerritory(p2, "SIAM", 1);
        GM.getBM().initializeTerritory(p2, "INDONESIA", 1);
        GM.getBM().initializeTerritory(p2, "WESTERN AUSTRALIA", 1);
        GM.getBM().initializeTerritory(p2, "EASTERN AUSTRALIA", 1);
        GM.getBM().initializeTerritory(p2, "NEW GUINEA", 1);
        GM.getBM().initializeTerritory(p2, "CHINA", 1);
        GM.getBM().initializeTerritory(p2, "AFGHANISTAN", 1);
        GM.getBM().initializeTerritory(p2, "URAL", 1);
        GM.getBM().initializeTerritory(p2, "SIBERIA", 1);


        GM.getBM().initializeTerritory(p1, "YAKUTSK", 1);
        GM.getBM().initializeTerritory(p1, "KAMCHATKA", 1);
        GM.getBM().initializeTerritory(p1, "IRKUTSK", 1);
        GM.getBM().initializeTerritory(p1, "MONGOLIA", 1);
        GM.getBM().initializeTerritory(p1, "JAPAN", 1);
        GM.getBM().initializeTerritory(p1, "UKRAINE", 1);
        GM.getBM().initializeTerritory(p2, "SCANDINAVIA", 1);
        GM.getBM().initializeTerritory(p2, "ICELAND", 1);
        GM.getBM().initializeTerritory(p2, "GREAT BRITAIN", 1);
        GM.getBM().initializeTerritory(p2, "NORTHERN EUROPE", 1);
        GM.getBM().initializeTerritory(p2, "SOUTHERN EUROPE", 1);
        GM.getBM().initializeTerritory(p2, "WESTERN EUROPE", 1);


    }
}