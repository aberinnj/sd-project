import junit.framework.TestCase;
import org.junit.Test;

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
    public void testFreeTerritories(){
        GameManager GM = new GameManager(3);

        Turn k = new Turn(GM.getBM(), GM.getPlayer(2), 0);
        k.player.addTerritories("ALASKA");
        k.player.addTerritories("ALBERTA");
        k.player.addTerritories("ONTARIO");
        k.player.addTerritories("PERU");
        k.player.addTerritories("BRAZIL");
        k.player.addTerritories("ARGENTINA");
        assertEquals(3, k.getFreeArmiesFromTerritoriesAndCards());
        k.player.addTerritories("CONGO");
        k.player.addTerritories("EGYPT");
        k.player.addTerritories("MADAGASCAR");
        k.player.addTerritories("INDIA");
        k.player.addTerritories("INDONESIA");
        k.player.addTerritories("SIAM");
        assertEquals(4, k.getFreeArmiesFromTerritoriesAndCards());
        k.player.addTerritories("CHINA");
        assertEquals(4, k.getFreeArmiesFromTerritoriesAndCards());
        k.player.addTerritories("VENEZUELA");
        assertEquals(6, k.getFreeArmiesFromTerritoriesAndCards());
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
}
