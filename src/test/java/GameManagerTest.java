
import junit.framework.TestCase;
import org.junit.Test;

import java.util.*;


public class GameManagerTest extends TestCase {

    GameManager GM;

    @Test
    public void testGameManagerPlayerList() throws Exception {
        Player[] TestPlayerList1;
        Player[] TestPlayerList2;
        Player[] TestPlayerList3;
        Player[] TestPlayerList4;
        GM = new GameManager(System.getProperty("user.dir"), 3);

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
        GM = new GameManager(System.getProperty("user.dir"), 3);

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

}