import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/*////////////////////////////////////////////////////////////////////////////////
Dice class

note:  BoardManager must have territories initialized with SetTerritory
*///////////////////////////////////////////////////////////////////////////////*/
public class TurnManagerTest extends TestCase {
    final BoardManager BM = new BoardManager();
    // total of 42 infantry to init board
    final Player p1 = new Player(0, 15);
    final Player p2 = new Player(1, 15);
    final Player p3 = new Player(2, 12);


    // test DEEP copy
    @Test
    public void testTurnManager(){
        BM.initializeTerritory(p1, "ALASKA", 1);
        BM.initializeTerritory(p1, "NORTH WEST TERRITORY", 1);
        BM.initializeTerritory(p1, "GREENLAND", 1);
        BM.initializeTerritory(p1, "ALBERTA", 1);
        BM.initializeTerritory(p1, "ONTARIO", 1);
        BM.initializeTerritory(p1, "QUEBEC", 1);
        BM.initializeTerritory(p1, "WESTERN UNITED STATES", 1);
        BM.initializeTerritory(p1, "EASTERN UNITED STATES", 1);
        BM.initializeTerritory(p1, "CENTRAL AMERICA", 1);
        BM.initializeTerritory(p1, "VENEZUELA", 1);
        BM.initializeTerritory(p1, "PERU", 1);
        BM.initializeTerritory(p1, "BRAZIL", 1);
        BM.initializeTerritory(p1, "ARGENTINA", 1);
        BM.initializeTerritory(p1, "NORTH AFRICA", 1);
        BM.initializeTerritory(p1, "CONGO", 1);

        BM.initializeTerritory(p2, "EGYPT", 1);
        BM.initializeTerritory(p2, "EAST AFRICA", 1);
        BM.initializeTerritory(p2, "SOUTH AFRICA", 1);
        BM.initializeTerritory(p2, "MADAGASCAR", 1);
        BM.initializeTerritory(p2, "MIDDLE EAST", 1);
        BM.initializeTerritory(p2, "INDIA", 1);
        BM.initializeTerritory(p2, "SIAM", 1);
        BM.initializeTerritory(p2, "INDONESIA", 1);
        BM.initializeTerritory(p2, "WESTERN AUSTRALIA", 1);
        BM.initializeTerritory(p2, "EASTERN AUSTRALIA", 1);
        BM.initializeTerritory(p2, "NEW GUINEA", 1);
        BM.initializeTerritory(p2, "CHINA", 1);
        BM.initializeTerritory(p2, "AFGHANISTAN", 1);
        BM.initializeTerritory(p2, "URAL", 1);
        BM.initializeTerritory(p2, "SIBERIA", 1);


        BM.initializeTerritory(p3, "YAKUTSK", 1);
        BM.initializeTerritory(p3, "KAMCHATKA", 1);
        BM.initializeTerritory(p3, "IRKUTSK", 1);
        BM.initializeTerritory(p3, "MONGOLIA", 1);
        BM.initializeTerritory(p3, "JAPAN", 1);
        BM.initializeTerritory(p3, "UKRAINE", 1);
        BM.initializeTerritory(p3, "SCANDINAVIA", 1);
        BM.initializeTerritory(p3, "ICELAND", 1);
        BM.initializeTerritory(p3, "GREAT BRITAIN", 1);
        BM.initializeTerritory(p3, "NORTHERN EUROPE", 1);
        BM.initializeTerritory(p3, "SOUTHERN EUROPE", 1);
        BM.initializeTerritory(p3, "WESTERN EUROPE", 1);

        //40 cards left
        BM.getGameDeck().draw();
        BM.getGameDeck().draw();

        TurnManager TM = new TurnManager();

        // imagine this is the initial turn-state until TM.init() works
        Turn t1 = new Turn(BM, p1, 0);
        Turn t2 = new Turn(BM, p2, 1);
        Turn t3 = new Turn(BM, p3, 2);
        //TM.save(t1);
        //TM.save(t2);
        //TM.save(t3);

        /*///////////////////////////////////////////////////////////////////////////////
        Initial
        ///////////////////////////////////////////////////////////////////////////////*/
        // test if added
        assertEquals(3, TM.getTurnList().size());
        // test Boardmap
        assertEquals(1, TM.getTurnList().get(1).BM.getBoardMap().get("EGYPT").getOccupantID());
        assertEquals(2, TM.getTurnList().get(2).BM.getBoardMap().get("YAKUTSK").getOccupantID());
        // test Player territories
        assertEquals(15, TM.getTurnList().get(0).player.getTerritories().size());
        assertEquals(12, TM.getTurnList().get(2).player.getTerritories().size());
        // test Deck
        assertEquals(42, TM.getTurnList().get(0).BM.getGameDeck().GameDeck.size());
        // test player Hand
        assertEquals(0, TM.getTurnList().get(1).player.getHandListing().size());

        Card a = BM.getGameDeck().draw();

        p1.addArmies(1);
        p2.addArmies(1);
        BM.initializeTerritory(p1, "EGYPT", 1);
        p2.loseTerritories("EGYPT");
        p1.getHand().get(a.getUnit()).push(a);
        Turn t4 = new Turn(BM, p1, 4);

        Card b = BM.getGameDeck().draw();
        BM.initializeTerritory(p2, "YAKUTSK", 1);
        p3.loseTerritories("YAKUTSK");
        p2.getHand().get(a.getUnit()).push(b);
        Turn t5 = new Turn(BM, p2, 5);
        //TM.save(t4);
        //TM.save(t5);
        /*///////////////////////////////////////////////////////////////////////////////
        Changes made. Assume a player's turn changes the data, like above. Test if
        t1/t2/t3 stays the same by doing the same test as above
        ///////////////////////////////////////////////////////////////////////////////*/
        // test if added
        assertEquals(5, TM.getTurnList().size());
        // test Boardmap
        assertEquals(1, TM.getTurnList().get(1).BM.getBoardMap().get("EGYPT").getOccupantID());
        assertEquals(2, TM.getTurnList().get(2).BM.getBoardMap().get("YAKUTSK").getOccupantID());
        // test Player territories
        assertEquals(15, TM.getTurnList().get(0).player.getTerritories().size());
        assertEquals(12, TM.getTurnList().get(2).player.getTerritories().size());
        // test Deck
        assertEquals(42, TM.getTurnList().get(0).BM.getGameDeck().GameDeck.size());
        // test player Hand
        assertEquals(0, TM.getTurnList().get(1).player.getHandListing().size());

    }

}
