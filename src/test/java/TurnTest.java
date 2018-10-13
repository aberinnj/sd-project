import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
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
                "Yes\nYes\nYes\nYes\nYes\nYes\nYes\nYes\nYes\n").getBytes());
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

        // 3+12
        k.player.getHand().get("WILD").push(new Card("ALASKA", "WILD"));
        k.player.getHand().get("INFANTRY").push(new Card("NORTHERN EUROPE", "INFANTRY"));
        k.player.getHand().get("INFANTRY").push(new Card("MIDDLE EAST", "INFANTRY"));
        assertEquals(15, k.getFreeArmiesFromTerritoriesAndCards(placeholder));

        k.bm.getBoardMap().get("ARGENTINA").setTerritory(true, 2, new Army(5));
        // 3+15
        k.player.getHand().get("INFANTRY").push(new Card("ARGENTINA", "INFANTRY"));
        k.player.getHand().get("INFANTRY").push(new Card("NORTHERN EUROPE", "INFANTRY"));
        k.player.getHand().get("INFANTRY").push(new Card("MIDDLE EAST", "INFANTRY"));
        assertEquals(18, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        assertEquals(7, k.bm.getOccupantCount("ARGENTINA"));

        // 3+20
        k.bm.getBoardMap().get("BRAZIL").setTerritory(true, 2, new Army(10));
        k.player.getHand().get("WILD").push(new Card("BRAZIL", "WILD"));
        k.player.getHand().get("CAVALRY").push(new Card("NORTHERN EUROPE", "CAVALRY"));
        k.player.getHand().get("ARTILLERY").push(new Card("MIDDLE EAST", "ARTILLERY"));
        assertEquals(23, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        assertEquals(12, k.bm.getOccupantCount("BRAZIL"));

        // 3+25
        k.bm.getBoardMap().get("PERU").setTerritory(true, 2, new Army(8));
        k.player.getHand().get("CAVALRY").push(new Card("ALASKA", "CAVALRY"));
        k.player.getHand().get("INFANTRY").push(new Card("PERU", "INFANTRY"));
        k.player.getHand().get("ARTILLERY").push(new Card("MIDDLE EAST", "ARTILLERY"));
        assertEquals(28, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        assertEquals(10, k.bm.getOccupantCount("PERU"));

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


        Turn m = new Turn(GM.getBM(), GM.getPlayer(1), 1);
        assertFalse(m.isPlayerEligibleToEarnCardsThisTurn());

    }


    @Test
    public void testPlaceNewArmies(){
        GameManager GM = new GameManager(6);
        ByteArrayInputStream in = new ByteArrayInputStream((
                "YEs\n"             +
                        "NORTH AFRICA\n"    +
                        "NORTH AFRICA\n"    +
                        "NORTH AFRICA\n"    +
                        "NORTH AFRICA\n"    +

                        "EAST AFRICA\n"     +
                        "EAST AFRICA\n"     +

                        "SOUTH AFRICA\n"    +
                        "SOUTH AFRICA\n"    +
                        "SOUTH AFRICA\n"    +
                        "SOUTH AFRICA\n"    +
                        "SOUTH AFRICA\n"    +
                        "SOUTH AFRICA\n"    +
                        "SOUTH AFRICA\n"    +

                        "PERU\n"    +

                        "BRAZIL\n"    +

                        "ARGENTINA\n"    +

                        "INDIA\n"    +
                        "INDIA\n"    +
                        "INDIA\n"    +

                        "INDONESIA\n"    +
                        "INDONESIA\n"    +
                        "INDONESIA\n"    +
                        "INDONESIA\n"    +

                        "SIAM\n"    +
                        "SIAM\n"    +
                        "SIAM\n"    +
                        "SIAM\n"    +

                        "CHINA\n"    +
                        "CHINA\n"    +
                        "CHINA\n"    +
                        "CHINA\n" ).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(System.in);

        Turn k = new Turn(GM.getBM(), GM.getPlayer(0), 1);
        k.player.addTerritories("NORTH AFRICA");
        k.player.addTerritories("EAST AFRICA");
        k.player.addTerritories("SOUTH AFRICA");

        k.player.addTerritories("PERU");
        k.player.addTerritories("BRAZIL");
        k.player.addTerritories("ARGENTINA");

        k.player.addTerritories("CONGO");
        k.player.addTerritories("EGYPT");
        k.player.addTerritories("MADAGASCAR");

        k.player.addTerritories("INDIA");
        k.player.addTerritories("INDONESIA");
        k.player.addTerritories("SIAM");
        k.player.addTerritories("CHINA");

        k.player.getHand().get("INFANTRY").push(new Card("PERU", "INFANTRY"));
        k.player.getHand().get("ARTILLERY").push(new Card("INDIA", "ARTILLERY"));
        k.player.getHand().get("CAVALRY").push(new Card("AFGHANISTAN", "CAVALRY"));

        ///////
        k.bm.getBoardMap().get("NORTH AFRICA").setTerritory(true, 0, new Army(3));
        k.bm.getBoardMap().get("EAST AFRICA").setTerritory(true, 0, new Army(3));
        k.bm.getBoardMap().get("SOUTH AFRICA").setTerritory(true, 0, new Army(5));

        k.bm.getBoardMap().get("PERU").setTerritory(true, 0, new Army(1));
        k.bm.getBoardMap().get("BRAZIL").setTerritory(true, 0, new Army(1));
        k.bm.getBoardMap().get("ARGENTINA").setTerritory(true, 0, new Army(1));

        k.bm.getBoardMap().get("CONGO").setTerritory(true, 0, new Army(1));
        k.bm.getBoardMap().get("EGYPT").setTerritory(true, 0, new Army(1));
        k.bm.getBoardMap().get("MADAGASCAR").setTerritory(true, 0, new Army(1));

        k.bm.getBoardMap().get("INDIA").setTerritory(true, 0, new Army(2));
        k.bm.getBoardMap().get("INDONESIA").setTerritory(true, 0, new Army(1));
        k.bm.getBoardMap().get("SIAM").setTerritory(true, 0, new Army(1));
        k.bm.getBoardMap().get("CHINA").setTerritory(true, 0, new Army(1));


        // 20 default infantry
        // +4 for occupying 12 territories
        // +3 for occupying African continent
        // +4 for first-trade
        // 31 total infantry
        // DIRECTLY +2 to PERU
        k.placeNewArmies(scanner);
        assertEquals(7, k.bm.getOccupantCount("NORTH AFRICA"));
        assertEquals(5, k.bm.getOccupantCount("EAST AFRICA"));
        assertEquals(12, k.bm.getOccupantCount("SOUTH AFRICA"));

        assertEquals(5, k.bm.getOccupantCount("INDIA"));
        assertEquals(5, k.bm.getOccupantCount("INDONESIA"));
        assertEquals(5, k.bm.getOccupantCount("SIAM"));
        assertEquals(5, k.bm.getOccupantCount("CHINA"));

        assertEquals(1, k.bm.getOccupantCount("CONGO"));
        assertEquals(1, k.bm.getOccupantCount("EGYPT"));
        assertEquals(1, k.bm.getOccupantCount("MADAGASCAR"));

        ArrayList<String> expectedAbleTerritories = new ArrayList<String>(){{
            add("NORTH AFRICA");
            add("EAST AFRICA");
            add("SOUTH AFRICA");
            add("PERU");
            add("BRAZIL");
            add("ARGENTINA");
            add("INDIA");
            add("INDONESIA");
            add("SIAM");
            add("CHINA");}};

        ArrayList<String> listing = new ArrayList<String>();
        listing.addAll(k.bm.getTerritories(k.player, true));
        for(String i: listing)
        {
            assertTrue(listing.contains(i));
        }

        assertTrue(k.player.isBaseEmpty());
        System.setIn(System.in);
    }
}
