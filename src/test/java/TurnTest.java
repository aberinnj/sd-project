import junit.framework.TestCase;
import org.junit.Test;

import java.util.Stack;

public class TurnTest extends TestCase {

    @Test
    public void testTurn() throws InterruptedException {
        Player player = new Player(1,200);
        Player player2 = new Player(2,200);
        BoardManager BM = new BoardManager();
        Stack<Card> deck = new Stack<>();
        for (int i = 0; i < 20; i++) {
            Card c = BM.gameDeck.draw();
            deck.push(c);
        }

        player.setCardStack(deck);
        Turn turn = new Turn(BM,player,1);
        turn.calculateTradeableCard();


        player.addTerritories("MONGOLIA");
        player2.addTerritories("CHINA");
        BM.initializeTerritory(player,"MONGOLIA", 20);
        BM.initializeTerritory(player2,"CHINA", 20);
        assertNotNull(turn.getAttackableTerritories());
        turn.battle("MONGOLIA", "CHINA", 3, 2);
        assertNotNull(turn.getArmiesFromCards());

    }
}
