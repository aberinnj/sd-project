
import junit.framework.TestCase;
import org.junit.Test;

import java.util.*;


public class GameManagerTest extends TestCase {

    GameManager GM;

    @Test
    public void testGameManagerPlayerList() throws Exception {
        Scanner scanner = new Scanner(System.in);
        Player[] TestPlayerList1;
        Player[] TestPlayerList2;
        Player[] TestPlayerList3;
        Player[] TestPlayerList4;
        GM = new GameManager(scanner, System.getProperty("user.dir"), 3);

        TestPlayerList1 = GM.setPlayerList(2);
        assertEquals(40, TestPlayerList1[0].getRemainingArmies());
        TestPlayerList2 = GM.setPlayerList(4);
        assertEquals(30, TestPlayerList2[0].getRemainingArmies());
        TestPlayerList3 = GM.setPlayerList(5);
        assertEquals(25, TestPlayerList3[0].getRemainingArmies());
        TestPlayerList4 = GM.setPlayerList(6);
        assertEquals(20, TestPlayerList4[0].getRemainingArmies());
    }

}