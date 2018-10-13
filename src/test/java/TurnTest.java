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
                "yno\n"     +
                "no\n"      +
                "yes\n"     +
                "Y\n"       +
                "N\n").getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(System.in);

        Turn k = new Turn(GM.getBM(), GM.getPlayer(2), 0);
        assertFalse(k.baseQuery("Yes/No", scanner));
        assertTrue(k.baseQuery("Yes/No", scanner));
        assertTrue(k.baseQuery("Yes/No", scanner));
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
        ByteArrayInputStream in = new ByteArrayInputStream((
                "No\n" + // does not trade
                "Yes\n" // trades
                ).getBytes());
        System.setIn(in);
        Scanner placeholder = new Scanner(System.in);

        Turn k = new Turn(GM.getBM(), GM.getPlayer(2), 0);
        // total of 3 territories
        k.player.addTerritories("PERU");
        k.player.addTerritories("BRAZIL");
        k.player.addTerritories("ARGENTINA");

        k.bm.getBoardMap().get("PERU").setTerritory(true, 2, new Army(2));
        // No longer less-than 3
        // 3 cards minimum from territories + NO to trade
        // NOTE: additions due to occupying a territory a card represents -- are automatically added to that territory
        k.player.getHand().get("CAVALRY").push(new Card("PERU", "CAVALRY"));
        k.player.getHand().get("CAVALRY").push(new Card("ARGENTINA", "CAVALRY"));
        k.player.getHand().get("CAVALRY").push(new Card("MIDDLE EAST", "CAVALRY"));
        assertEquals(3, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        assertEquals(7, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        assertEquals(4, k.bm.getOccupantCount("PERU"));
        System.setIn(System.in);
    }

    @Test
    public void testFreeTerritoriesWithCards2() {
        GameManager GM = new GameManager(3);
        ByteArrayInputStream in = new ByteArrayInputStream((
                "Yes\n").getBytes());
        System.setIn(in);
        Scanner placeholder = new Scanner(System.in);

        Turn k = new Turn(GM.getBM(), GM.getPlayer(2), 0);
        // total of 3 territories
        k.player.addTerritories("PERU");
        k.player.addTerritories("BRAZIL");
        k.player.addTerritories("ARGENTINA");
        k.player.addTerritories("VENEZUELA");
        // No longer less-than 3
        // NOTE: additions due to occupying a territory a card represents -- are automatically added to that territory
        // 3 cards minimum + 4 with trade + 2 owning continent
        k.player.getHand().get("INFANTRY").push(new Card("ALASKA", "INFANTRY"));
        k.player.getHand().get("INFANTRY").push(new Card("NORTHERN EUROPE", "INFANTRY"));
        k.player.getHand().get("INFANTRY").push(new Card("MIDDLE EAST", "INFANTRY"));
        assertEquals(9, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        assertEquals(0, k.getTotalCards());

        System.setIn(System.in);
    }

    @Test
    public void testFreeTerritoriesWithCards3() {
        GameManager GM = new GameManager(3);
        ByteArrayInputStream in = new ByteArrayInputStream((
                "Yes\nYes\nYes\nYes\nYes\n").getBytes());
        System.setIn(in);
        Scanner placeholder = new Scanner(System.in);

        Turn k = new Turn(GM.getBM(), GM.getPlayer(2), 0);
        // total of 3 territories
        k.player.addTerritories("PERU");
        k.player.addTerritories("BRAZIL");
        k.player.addTerritories("ARGENTINA");
        // No longer less-than 3
        // NOTE: additions due to occupying a territory a card represents -- are automatically added to that territory
        // 3 cards minimum + 0 with no full-set to trade +
        k.player.getHand().get("ARTILLERY").push(new Card("ALASKA", "ARTILLERY"));
        k.player.getHand().get("ARTILLERY").push(new Card("NORTHERN EUROPE", "ARTILLERY"));
        k.player.getHand().get("CAVALRY").push(new Card("MIDDLE EAST", "CAVALRY"));
        assertEquals(3, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        k.player.getHand().get("INFANTRY").push(new Card("JAPAN", "INFANTRY"));
        // 3+4 from trade
        assertEquals(7, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        assertEquals(1, k.getTotalCards());

        System.setIn(System.in);


        // 3+6 from trade
        k.player.getHand().get("ARTILLERY").push(new Card("ALASKA", "ARTILLERY"));
        k.player.getHand().get("ARTILLERY").push(new Card("NORTHERN EUROPE", "ARTILLERY"));
        assertEquals(9, k.getFreeArmiesFromTerritoriesAndCards(placeholder));

        // 3+8
        k.player.getHand().get("WILD").push(new Card("ALASKA", "WILD"));
        k.player.getHand().get("INFANTRY").push(new Card("NORTHERN EUROPE", "INFANTRY"));
        k.player.getHand().get("INFANTRY").push(new Card("MIDDLE EAST", "INFANTRY"));
        assertEquals(11, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        // 3+10
        k.player.getHand().get("WILD").push(new Card("ALASKA", "WILD"));
        k.player.getHand().get("INFANTRY").push(new Card("NORTHERN EUROPE", "INFANTRY"));
        k.player.getHand().get("INFANTRY").push(new Card("MIDDLE EAST", "INFANTRY"));
        assertEquals(13, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
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
    public void testTotalCards(){
        GameManager GM = new GameManager(3);
        Turn k = new Turn(GM.getBM(), GM.getPlayer(2), 0);

        k.player.getHand().get("WILD").push(new Card("PERU", "WILD"));
        k.player.getHand().get("CAVALRY").push(new Card("ARGENTINA", "CAVALRY"));

        k.player.getHand().get("INFANTRY").push(new Card("PERU", "INFANTRY"));
        k.player.getHand().get("ARTILLERY").push(new Card("ARGENTINA", "ARTILLERY"));
        k.player.getHand().get("CAVALRY").push(new Card("MIDDLE EAST", "CAVALRY"));

        k.player.getHand().get("INFANTRY").push(new Card("PERU", "INFANTRY"));
        k.player.getHand().get("CAVALRY").push(new Card("ARGENTINA", "CAVALRY"));
        k.player.getHand().get("CAVALRY").push(new Card("MIDDLE EAST", "CAVALRY"));
        assertEquals(8, k.getTotalCards());
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

        assertEquals(1, k.getTotalCards());

    }
}
