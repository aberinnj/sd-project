import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

/*///////////////////////////////////////////////////////////////////////////////
Turn Class test cases
 *//////////////////////////////////////////////////////////////////////////////
public class TurnTest extends TestCase {

    // expects turn to only serve as proxy and, as a reference
    @Test
    public void testTurnInit(){
        GameManager GM = new GameManager(3);

        Turn k = new Turn(GM.getBM(), GM.getPlayer(2), 12);
        assertEquals(35, k.player.getRemainingArmies());
        GM.getPlayer(2).deployArmies(1);
        assertEquals(34, k.player.getRemainingArmies());
        assertEquals(12, k.turnId);
    }

    @Test
    public void testBasicQuery(){
        GameManager GM = new GameManager(3);

        ByteArrayInputStream in = new ByteArrayInputStream((
                "INVALID\n" +
                "INVALID\n" +
                "yno\n"+
                "no").getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(System.in);

        Turn k = new Turn(GM.getBM(), GM.getPlayer(2), 0);
        assertFalse(k.baseQuery("Yes/No", scanner));

        System.setIn(System.in);
    }


    @Test
    public void testFreeTerritories(){
        GameManager GM = new GameManager(3);
        Scanner placeholder = new Scanner(System.in);

        Turn k = new Turn(GM.getBM(), GM.getPlayer(2), 0);
        k.player.addTerritories("ALASKA");
        k.player.addTerritories("ALBERTA");
        k.player.addTerritories("ONTARIO");
        k.player.addTerritories("PERU");
        k.player.addTerritories("BRAZIL");
        k.player.addTerritories("ARGENTINA");
        assertEquals(3, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        k.player.addTerritories("CONGO");
        k.player.addTerritories("EGYPT");
        k.player.addTerritories("MADAGASCAR");
        k.player.addTerritories("INDIA");
        k.player.addTerritories("INDONESIA");
        k.player.addTerritories("SIAM");
        assertEquals(4, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        k.player.addTerritories("CHINA");
        assertEquals(4, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        k.player.addTerritories("VENEZUELA");
        assertEquals(6, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
    }

    @Test
    public void testFreeTerritoriesWithCards(){
        GameManager GM = new GameManager(3);
        ByteArrayInputStream in = new ByteArrayInputStream(("Yes").getBytes());
        System.setIn(in);
        Scanner placeholder = new Scanner(System.in);

        Turn k = new Turn(GM.getBM(), GM.getPlayer(2), 0);
        // total of 3
        k.player.addTerritories("PERU");
        k.player.addTerritories("BRAZIL");
        k.player.addTerritories("ARGENTINA");

        // No longer less-than 3
        k.player.addToHand(new Card("PERU", "CAVALRY"));
        k.player.addToHand(new Card("ARGENTINA", "CAVALRY"));
        k.player.addToHand(new Card("MIDDLE EAST", "CAVALRY"));

        // 3 cards minimum from territories + 4 from trade-in
        // NOTE: additions due to occupying a territory a card represents -- are automatically added to that territory
        assertEquals(7, k.getFreeArmiesFromTerritoriesAndCards(placeholder));

        assertEquals(0, k.player.getHand().size());

        // 3 cards minimum from territories + no trade-in + 2 for obtaining a continent
        k.player.addTerritories("VENEZUELA");
        assertEquals(5, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        System.setIn(System.in);
    }


    @Test
    public void testCardEligibilityCheck(){
        GameManager GM = new GameManager( 3);

        // player's previous territories
        GM.getPlayer(1).addTerritories("ALASKA");
        GM.getPlayer(1).addTerritories("GREENLAND");
        GM.getPlayer(1).addTerritories("ALBERTA");
        Turn k = new Turn(GM.getBM(), GM.getPlayer(2), 1);
        // player's new territories
        k.player.addTerritories("QUEBEC");
        k.player.addTerritories("PERU");
        assertTrue(k.isPlayerEligibleToEarnCardsThisTurn());


    }

    @Test
    public void testEarnCards(){
        GameManager GM = new GameManager( 3);

        // player's previous territories
        GM.getPlayer(1).addTerritories("ALASKA");
        GM.getPlayer(1).addTerritories("GREENLAND");
        GM.getPlayer(1).addTerritories("ALBERTA");
        Turn k = new Turn(GM.getBM(), GM.getPlayer(2), 1);
        // player's new territories
        k.player.addTerritories("QUEBEC");
        k.player.addTerritories("PERU");
        k.earnCards();

        assertEquals(1, k.player.getHand().size());

    }
}
