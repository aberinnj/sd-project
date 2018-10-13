
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.*;


/*///////////////////////////////////////////////////////////////////////////////
GameManagerTest
 *//////////////////////////////////////////////////////////////////////////////
public class GameManagerTest extends TestCase {

    GameManager GM;

    @Test
    public void testGameManagerPlayerList() throws Exception {
        Player[] TestPlayerList1;
        Player[] TestPlayerList2;
        Player[] TestPlayerList3;
        Player[] TestPlayerList4;
        GM = new GameManager(3);

        TestPlayerList1 = GM.setPlayerList(2);
        assertEquals(40, TestPlayerList1[0].getRemainingArmies());
        TestPlayerList2 = GM.setPlayerList(4);
        assertEquals(30, TestPlayerList2[0].getRemainingArmies());
        TestPlayerList3 = GM.setPlayerList(5);
        assertEquals(25, TestPlayerList3[0].getRemainingArmies());
        TestPlayerList4 = GM.setPlayerList(6);
        assertEquals(20, TestPlayerList4[0].getRemainingArmies());
    }

    @Test
    public void testGameOver() {
        GM = new GameManager(3);

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
        GM = new GameManager(2);
        GM.runSetup(GM, thisScanner);
        // Test Below

        assertTrue(GM.getPlayer(0).isBaseEmpty());
        assertTrue(GM.getPlayer(1).isBaseEmpty());

        BoardManager BM = GM.getBM();
        assertEquals(0, BM.getFreeTerritories().size());

        System.setIn(System.in);
    }
}