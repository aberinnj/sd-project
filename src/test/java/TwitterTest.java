import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Mockito;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TwitterTest extends TestCase{

    @Test
    public void testBroadcast() throws TwitterException
    {
        twitter4j.Twitter twitter = mock(twitter4j.Twitter.class);
        twitter4j.Status status = mock(twitter4j.Status.class);
        when(status.getText()).thenReturn("Turn(81):Player 0 captured 2 territories.");
        when(twitter.updateStatus("Turn(81):Player 0 captured 2 territories.")).thenReturn(status);

        // check if it works
        assertEquals("Turn(81):Player 0 captured 2 territories.", twitter.updateStatus("Turn(81):Player 0 captured 2 territories.").getText());

        // setup
        _GameMaster.gamesListing = new HashMap<>();
        _GameMaster.allPlayersAndTheirGames = new HashMap<>();
        ChatInput INPUT = new ChatInput();
        INPUT.command = "/join";
        INPUT.args = new ArrayList<String>(){{add("game");}};

        Responses.onCreate(0, "game", "her", 123);
        Responses.onJoin(INPUT, 1, "his", (long)123);
        _GameMaster.gamesListing.get("game").setPlayerList();

        Responses.onSkipClaim(_GameMaster.gamesListing.get("game"));
        Responses.onSkipReinforce(_GameMaster.gamesListing.get("game"));

        Twitter.twitter = twitter;

        Player player = CommandUtils.getPlayer(_GameMaster.gamesListing.get("game")); // her
        // previousTerritories defined here
        Turn turn = new Turn(_GameMaster.gamesListing.get("game").BM, player, 81);

        // no captures for this broadcast
        String response = Twitter.broadcastToTwitter(turn, player);
        assertEquals("\nTurn Summary: Turn(81):Player 0 captured no territories this turn.", response);

        // add captures here
        player.addTerritories("BRAZIL");
        player.addTerritories("ALASKA");

        // some captures for this broadcast
        response = Twitter.broadcastToTwitter(turn, player);
        assertEquals("\nTurn Summary: Turn(81):Player 0 captured 2 territories.", response);

    }
}
