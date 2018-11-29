import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LoaderTest extends TestCase {

    @Test
    public void testLoader() throws IOException {
        _GameMaster _GameMaster = new _GameMaster();
        Loader load = new Loader("1");
        load.JH.fileName = load.JH.base + "/src/files/testRisk.json";
        load.loadGame();
    }

    @Test
    public void testUndo() {
        _GameMaster.gamesListing = new HashMap<>();
        _GameMaster.allPlayersAndTheirGames = new HashMap<>();

        ChatInput INPUT = new ChatInput();
        INPUT.command = "/join";
        INPUT.args = new ArrayList<String>(){{add("game");}};

        Responses.onCreate(0, "game", "bonnie", 1234567);
        Responses.onJoin(INPUT,1 , "clyde", (long)1234567 );
        _GameMaster.gamesListing.get("game").setPlayerList();

        Responses.onSkipClaim(_GameMaster.gamesListing.get("game"));
        Responses.onSkipReinforce( _GameMaster.gamesListing.get("game"));

        Turn turn = new Turn(_GameMaster.gamesListing.get("game").BM, CommandUtils.getPlayer(_GameMaster.gamesListing.get("game")), 1);
        turn.earnCards();
        turn.isPlayerEligibleToEarnCardsThisTurn();
        turn.getFreeArmiesFromTerritories();

        assertNotNull(turn.getAttackableTerritories());

        JSONhandler handler = new JSONhandler(_GameMaster.gamesListing.get("game"));
        handler.fileName = handler.base + "/src/files//testUndo.json";

        try {
            handler.JSONwriter();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        Game game = CommandUtils.getGame(0);
        int remaining_armies = game.playerDirectory.get(0).getNumberOfArmies();
        assertEquals(1, game.BM.getOccupantCount("YAKUTSK"));
        assertEquals(0, remaining_armies);

        // MAKE CHANGES
        INPUT.args = new ArrayList<String>(){{add("YAKUTSK");}};
        Responses.onBeginTurn(_GameMaster.gamesListing.get("game"));
        Responses.onReinforce(INPUT, 0, _GameMaster.gamesListing.get("game"));
        Responses.onReinforce(INPUT, 0, _GameMaster.gamesListing.get("game"));
        Responses.onReinforce(INPUT, 0, _GameMaster.gamesListing.get("game"));

        remaining_armies = game.playerDirectory.get(0).getNumberOfArmies();
        assertEquals(4, game.BM.getOccupantCount("YAKUTSK"));
        assertEquals(4, remaining_armies);


        // UNDO
        INPUT.args = new ArrayList<String>(){{add("game");}};

        _GameMaster.gamesListing.get("game").gameLoader = new Loader(INPUT.getArgs().get(0));
        _GameMaster.gamesListing.get("game").gameLoader.JH.fileName = _GameMaster.gamesListing.get("game").gameLoader.JH.base + "/src/files/testUndo.json";
        try {
            _GameMaster.gamesListing.put(INPUT.getArgs().get(0), _GameMaster.gamesListing.get("game").gameLoader.loadGame());
        } catch(IOException e)
        {
            e.printStackTrace();
        }

        // ASSERTIONS THAT UNDO WORKED, YAKUTSK HAS 1 again
        game = CommandUtils.getGame(0);
        remaining_armies = game.playerDirectory.get(0).getNumberOfArmies();
        assertEquals(1, game.BM.getOccupantCount("YAKUTSK"));
        assertEquals(0, remaining_armies);
    }
}
