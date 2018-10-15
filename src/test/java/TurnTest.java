import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Scanner;

/*///////////////////////////////////////////////////////////////////////////////
Turn Class test cases

todo: clean placeNewArmies test below, read comments included.
 *//////////////////////////////////////////////////////////////////////////////
public class TurnTest extends TestCase {

    // expects turn to only serve as proxy and, as a reference
    @Test
    public void testTurnInit(){
        GameManager GM = new GameManager(3);

        Turn k = new Turn(GM.getBM(), GM.getPlayer(2), 12);
        assertEquals(35, k.player.getNumberOfArmies());
        GM.getPlayer(2).loseArmies(1);
        assertEquals(34, k.player.getNumberOfArmies());
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

        k.BM.getBoardMap().get("PERU").setTerritory(true, 2, new Army(2));
        // No longer less-than 3
        // 3 cards minimum from territories + NO to trade
        // NOTE: additions due to occupying a territory a card represents -- are automatically added to that territory
        k.player.getHand().get("CAVALRY").push(new Card("PERU", "CAVALRY"));
        k.player.getHand().get("CAVALRY").push(new Card("ARGENTINA", "CAVALRY"));
        k.player.getHand().get("CAVALRY").push(new Card("MIDDLE EAST", "CAVALRY"));
        assertEquals(3, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        assertEquals(7, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        assertEquals(4, k.BM.getOccupantCount("PERU"));
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
        assertEquals(0, k.player.getTotalCards());

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
        k.BM.initializeTerritory(k.player, "PERU", 8);
        k.BM.initializeTerritory(k.player, "BRAZIL", 10);
        k.BM.initializeTerritory(k.player, "ARGENTINA", 5);
        k.BM.initializeTerritory(k.player, "JAPAN", 5);

        // No longer less-than 3
        // NOTE: additions due to occupying a territory a card represents -- are automatically added to that territory
        // 3 cards minimum + 0 with no full-set to trade +
        k.player.getHand().get("ARTILLERY").push(new Card("ALASKA", "ARTILLERY"));
        k.player.getHand().get("ARTILLERY").push(new Card("NORTHERN EUROPE", "ARTILLERY"));
        k.player.getHand().get("CAVALRY").push(new Card("MIDDLE EAST", "CAVALRY"));
        assertEquals(3, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        k.player.getHand().get("INFANTRY").push(new Card("KAMCHATKA", "INFANTRY"));
        // 3+4 from trade
        assertEquals(7, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        assertEquals(1, k.player.getTotalCards());

        // 3+6 from trade
        // 2 extra armies goes to NORTHERN EUROPE for trading in a set with northern europe (currently occupying)
        k.player.getHand().get("ARTILLERY").push(new Card("ALASKA", "ARTILLERY"));
        k.player.getHand().get("ARTILLERY").push(new Card("JAPAN", "ARTILLERY"));
        assertEquals(9, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        assertEquals(7, k.BM.getOccupantCount("JAPAN"));

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

        // 3+15
        k.player.getHand().get("INFANTRY").push(new Card("ARGENTINA", "INFANTRY"));
        k.player.getHand().get("INFANTRY").push(new Card("NORTHERN EUROPE", "INFANTRY"));
        k.player.getHand().get("INFANTRY").push(new Card("MIDDLE EAST", "INFANTRY"));
        assertEquals(18, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        assertEquals(7, k.BM.getOccupantCount("ARGENTINA"));

        // 3+20
        k.player.getHand().get("WILD").push(new Card("BRAZIL", "WILD"));
        k.player.getHand().get("CAVALRY").push(new Card("NORTHERN EUROPE", "CAVALRY"));
        k.player.getHand().get("ARTILLERY").push(new Card("MIDDLE EAST", "ARTILLERY"));
        assertEquals(23, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        assertEquals(12, k.BM.getOccupantCount("BRAZIL"));

        // 3+25
        k.player.getHand().get("CAVALRY").push(new Card("ALASKA", "CAVALRY"));
        k.player.getHand().get("INFANTRY").push(new Card("PERU", "INFANTRY"));
        k.player.getHand().get("ARTILLERY").push(new Card("MIDDLE EAST", "ARTILLERY"));
        assertEquals(28, k.getFreeArmiesFromTerritoriesAndCards(placeholder));
        assertEquals(10, k.BM.getOccupantCount("PERU"));

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
        assertEquals(8, k.player.getTotalCards());
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

        assertEquals(1, k.player.getTotalCards());


        Turn m = new Turn(GM.getBM(), GM.getPlayer(1), 1);
        assertFalse(m.isPlayerEligibleToEarnCardsThisTurn());

    }


    // BELOW test is fragile. remove the k.player.addTerritories and k.BM.getBoardMap().get(country).setTerritory
    // method calls. Use k.BM.initializeTerritory instead, which does both
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
                        "CHINA\n").getBytes());
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
        k.BM.getBoardMap().get("NORTH AFRICA").setTerritory(true, 0, new Army(3));
        k.BM.getBoardMap().get("EAST AFRICA").setTerritory(true, 0, new Army(3));
        k.BM.getBoardMap().get("SOUTH AFRICA").setTerritory(true, 0, new Army(5));

        k.BM.getBoardMap().get("PERU").setTerritory(true, 0, new Army(1));
        k.BM.getBoardMap().get("BRAZIL").setTerritory(true, 0, new Army(1));
        k.BM.getBoardMap().get("ARGENTINA").setTerritory(true, 0, new Army(1));

        k.BM.getBoardMap().get("CONGO").setTerritory(true, 0, new Army(1));
        k.BM.getBoardMap().get("EGYPT").setTerritory(true, 0, new Army(1));
        k.BM.getBoardMap().get("MADAGASCAR").setTerritory(true, 0, new Army(1));

        k.BM.getBoardMap().get("INDIA").setTerritory(true, 0, new Army(2));
        k.BM.getBoardMap().get("INDONESIA").setTerritory(true, 0, new Army(1));
        k.BM.getBoardMap().get("SIAM").setTerritory(true, 0, new Army(1));
        k.BM.getBoardMap().get("CHINA").setTerritory(true, 0, new Army(1));

        // 20 default infantry
        // +4 for occupying 12 territories
        // +3 for occupying African continent
        // +4 for first-trade
        // 31 total infantry
        // DIRECTLY +2 to PERU
        k.placeNewArmies(GM, scanner);
        assertEquals(7, k.BM.getOccupantCount("NORTH AFRICA"));
        assertEquals(5, k.BM.getOccupantCount("EAST AFRICA"));
        assertEquals(12, k.BM.getOccupantCount("SOUTH AFRICA"));

        assertEquals(5, k.BM.getOccupantCount("INDIA"));
        assertEquals(5, k.BM.getOccupantCount("INDONESIA"));
        assertEquals(5, k.BM.getOccupantCount("SIAM"));
        assertEquals(5, k.BM.getOccupantCount("CHINA"));

        assertEquals(1, k.BM.getOccupantCount("CONGO"));
        assertEquals(1, k.BM.getOccupantCount("EGYPT"));
        assertEquals(1, k.BM.getOccupantCount("MADAGASCAR"));

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
        listing.addAll(k.BM.getAbleTerritories(k.player, true));
        for(String i: listing)
        {
            assertTrue(listing.contains(i));
        }

        assertTrue(k.player.getNumberOfArmies() == 0);
        System.setIn(System.in);
    }


    @Test
    public void testFortify(){
        ByteArrayInputStream in = new ByteArrayInputStream((
                "Yes\n" +
                "INDONESIA\n" +
                "NEW GUINEA\n" +
                "1\n").getBytes());
        System.setIn(in);
        Scanner sp = new Scanner(System.in);
        BoardManager BM = new BoardManager();
        Player p1 = new Player(1, 5);
        BM.initializeTerritory(p1, "INDONESIA", 2);
        BM.initializeTerritory(p1, "WESTERN AUSTRALIA", 2);
        BM.initializeTerritory(p1, "EASTERN AUSTRALIA", 2);
        BM.initializeTerritory(p1, "NEW GUINEA", 2);

        Turn k = new Turn(BM, p1, 2);
        k.fortifyTerritories(sp);

        assertEquals(1, BM.getOccupantCount("INDONESIA"));
        assertEquals(3, BM.getOccupantCount("NEW GUINEA"));
        System.setIn(System.in);
    }

    @Test
    public void testBattle(){
        GameManager GM = new GameManager(2);
        Turn k = new Turn(GM.getBM(), GM.getPlayer(1), 25);
        int attackerDice = 3;//count of dice -- 3 armies

        String defending = "NORTH AFRICA";
        String attacking = "CONGO";


        GM.getBM().initializeTerritory(GM.getPlayer(0), defending, 2);
        GM.getBM().initializeTerritory(GM.getPlayer(1), attacking, 4);

        ArrayList<Integer> attacker_dice = new ArrayList<Integer>(){{add(5); add(2); add(1);}}; // win
        ArrayList<Integer> defender_dice = new ArrayList<Integer>(){{add(1); add(3);}};

        // Attacker wins and claims territory
        k.battle(GM, attackerDice, defending, attacking, attacker_dice, defender_dice);
        assertTrue(k.player.getTerritories().contains(defending));
        assertEquals(3, GM.getBM().getOccupantCount(defending));
        // Defender no longer
        assertFalse(GM.getPlayer(0).getTerritories().contains(defending));

        /////////////////////////////////////////////////////////////////////////////////
        defending = "ALASKA";
        attacking = "KAMCHATKA";

        GM.getBM().initializeTerritory(GM.getPlayer(0), defending, 1);
        GM.getBM().initializeTerritory(GM.getPlayer(1), attacking, 2);
        attacker_dice = new ArrayList<Integer>(){{add(5);}}; // tie (lose 1 army) attack fails
        defender_dice = new ArrayList<Integer>(){{add(5);}};

        // Attacker fails, Defender still has defending territory
        k.battle(GM, 1, defending, attacking, attacker_dice, defender_dice);
        assertTrue(GM.getPlayer(0).getTerritories().contains(defending));
        // Attacker fails and should have less army in attacking territory
        assertEquals(1, GM.getBM().getOccupantCount(attacking));

        /////////////////////////////////////////////////////////////////////////////////
        defending = "ALASKA";
        attacking = "KAMCHATKA";

        GM.getBM().initializeTerritory(GM.getPlayer(0), defending, 1);
        GM.getBM().initializeTerritory(GM.getPlayer(1), attacking, 2);
        attacker_dice = new ArrayList<Integer>(){{add(2);}}; // tie (lose 1 army) attack fails
        defender_dice = new ArrayList<Integer>(){{add(5);}};

        // Attacker fails, Defender still has defending territory
        k.battle(GM, 1, defending, attacking, attacker_dice, defender_dice);
        assertTrue(GM.getPlayer(0).getTerritories().contains(defending));
        // Attacker fails and should have less army in attacking territory
        assertEquals(1, GM.getBM().getOccupantCount(attacking));

    }

    // testing Attack requires a deeper knowledge of what is being changed under the hood
    @Test
    public void testAttack(){
        ByteArrayInputStream in = new ByteArrayInputStream((
                "yes\n" +
                        "ALASKA\n" + // failQuery
                        "SIAM\n" +  // failQuery
                        "INDONESIA\n" +  // passQuery

                        "EASTERN AUSTRALIA\n" + // failQuery
                        "INDIA\n"+  // failQuery
                        "NEW GUINEA\n" +    // passQuery
                        "45\n"+ // fail
                        "2\n"+ // fail
                        "1\n"+ // pass
                        "3\n"+ //fail
                        "1\n"+ // pass
                        "NO\n"  // end iteration
        ).getBytes());
        System.setIn(in);
        Scanner sp = new Scanner(System.in);
        GameManager GM = new GameManager(2);
        Player p1 = new Player(1, 5);
        Player p2 = new Player(0, 5);
        GM.getBM().initializeTerritory(p1, "INDONESIA", 2);
        GM.getBM().initializeTerritory(p1, "WESTERN AUSTRALIA", 2);
        GM.getBM().initializeTerritory(p1, "EASTERN AUSTRALIA", 2);
        GM.getBM().initializeTerritory(p2, "NEW GUINEA", 1);
        GM.getBM().initializeTerritory(p2, "SIAM", 2);
        GM.getBM().initializeTerritory(p2, "INDIA", 2);


        Turn t1 = new Turn(GM.getBM(), p1, 22);
        t1.attack(GM, sp);

        System.out.println(GM.getBM().getOccupantCount("INDONESIA"));
        assertEquals(1, GM.getBM().getOccupantCount("INDONESIA"));
        System.setIn(in);
    }
}
