import com.google.gson.JsonObject;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

public class JSONhandlerTest extends TestCase {

    @Test
    public void testJSONhandler() throws IOException {
        Game game = new Game();
        JSONhandler handler = new JSONhandler(game);
        handler.fileName = handler.base + "/src/files//testRisk2.json";
        JsonObject junk = handler.JSONreader();
        handler.JSONwriter();
    }

    @Test
    public void testJSONplayers() throws InterruptedException, IOException {
        Game game = new Game();

        game.addUser(1, "testDude", 231251561);
        Player thatGuy = game.playerDirectory.get(0);

        game.start();

        Turn turn = new Turn(game.BM, thatGuy, 1);
        turn.earnCards();
        turn.isPlayerEligibleToEarnCardsThisTurn();
        turn.getFreeArmiesFromTerritories();

        game.BM.initializeTerritory(thatGuy,"MONGOLIA", 1);

        assertNotNull(turn.getAttackableTerritories());
        assertNotNull(turn.calculateTradeableCard());
        assertNotNull(turn.getArmiesFromCards());

        JSONhandler handler = new JSONhandler(game);
        handler.fileName = handler.base + "/src/files//testRisk2.json";

        handler.JSONwriter();

        CommandUtils cutils = new CommandUtils();
        assertNotNull(cutils.getInput("Hello World"));
        assertNotNull(cutils.getPlayer(game));
        assertNotNull(cutils.isReinforcingOver(game));
    }
}
