import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class TurnTest extends TestCase {

    @Test
    public void testTurn() {

        _GameMaster.gamesListing = new HashMap<>();
        _GameMaster.allPlayersAndTheirGames = new HashMap<>();

        ChatInput INPUT = new ChatInput();
        INPUT.command = "/join";
        INPUT.args = new ArrayList<String>(){{add("game");}};

        Responses.onCreate(2, "game", "bobby", 1234567);
        Responses.onJoin(INPUT, 1, "dobby", (long)123);

        Game game = CommandUtils.getGame(2);
        game.setPlayerList();

        Responses.onSkipClaim(_GameMaster.gamesListing.get("game"));

        Stack<Card> deck = new Stack<>();
        for (int i = 0; i < 20; i++) {
            Card c = game.BM.gameDeck.draw();
            deck.push(c);
        }

        Player player = CommandUtils.getPlayer(game);
        player.setCardStack(deck);
        Turn turn = new Turn(game.BM, player,game.turn);
        turn.calculateTradeableCard();

        assertNotNull(turn.getAttackableTerritories());

        turn.battle("MONGOLIA", "CHINA", 3, 2);
        assertNotNull(turn.getArmiesFromCards());

    }

    @Test
    public void testIndexOfHighestRoll() {
        _GameMaster.gamesListing = new HashMap<>();
        _GameMaster.allPlayersAndTheirGames = new HashMap<>();

        String onCreate = Responses.onCreate(2, "game", "bobby", 1234567);

        ArrayList<Integer> listing = new ArrayList<Integer>(){{add(7); add(2); add(5);}};

        Game thisGame = CommandUtils.getGame(2);
        Player k = CommandUtils.getPlayer(thisGame);
        Turn m = new Turn(thisGame.BM, k, 1);

        int index = m.getIndexOfHighestRollIn(listing, 3);
        assertEquals(0, index);

        ArrayList<Integer> listing2 = new ArrayList<Integer>(){{add(7); add(2); add(15);}};
        index = m.getIndexOfHighestRollIn(listing2, 3);
        assertEquals(2, index);

    }
}
