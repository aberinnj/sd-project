import junit.framework.TestCase;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;

public class UpdateHandlerTest extends TestCase {

    @Test
    public void testCommands(){

        _GameMaster.gamesListing = new HashMap<>();
        _GameMaster.allPlayersAndTheirGames = new HashMap<>();

        // SETUP
        ChatInput input = new ChatInput();
        input.command = "/start";
        input.args = new ArrayList<>();
        input.args.add("");
        int id = 1;
        long chat_id = 1234567890;
        String username = "bobbyFreshKnowsHowToTest";

        String response = UpdateHandler.commandReader(input, id, chat_id, username);
        assertEquals("Welcome! To play risk, you can either do the following:" +
                "\n/create your own session for this chat, if the chat is not playing" +
                "\n/join if the chat is playing and there's enough players." +
                "\n/help to view all the available commands.", response);

        input.command = "/NOPE";
        response = UpdateHandler.commandReader(input, id, chat_id, username);
        assertEquals("Command /NOPE not found.\n\nRisk has the following commands available: \n" +
                "/create to create a new instance of the game\n" +
                "/join <gameID> to select an available session\n" +
                "/listAllGames to list all available game sessions\n" +
                "/listMyGames to list all your game sessions\n" +
                "/getStatus to get the status on the chat's current game\n" +
                "/load to replay a game.", response);

        input.command = "/help";
        response = UpdateHandler.commandReader(input, id, chat_id, username);
        assertEquals("Risk has the following commands available: \n" +
        "/create to create a new instance of the game\n" +
                "/join <gameID> to select an available session\n" +
                "/listAllGames to list all available game sessions\n" +
                "/listMyGames to list all your game sessions\n" +
                "/getStatus to get the status on the chat's current game\n" +
                "/load to replay a game.", response);


        // CREATING
        input.command = "/create";
        response = UpdateHandler.commandReader(input, id, chat_id, username);
        ArrayList<String> k = new ArrayList<String>(){{addAll(_GameMaster.gamesListing.keySet());}};
        String gameID = k.get(0);
        assertEquals("Creating a new game session. \n"+
                "GameID: "+gameID, response);
        assertTrue(_GameMaster.gamesListing.containsKey(gameID));

        // JOINING
        input.command = "/join";
        input.args = new ArrayList<>();
        input.args.add(gameID);
        int user = 0;
        username = "ClydeFreshKnowsHowToTest";

        response = UpdateHandler.commandReader(input, id, chat_id, username);
        assertEquals("You have successfully joined.", response);
        assertTrue(_GameMaster.gamesListing.get(gameID).playerDirectory.containsKey(1));
        assertTrue(_GameMaster.gamesListing.get(gameID).playerDirectory.containsKey(0));

        _GameMaster.gamesListing.get(gameID).setPlayerList();

        // STARTING GAME
        input.command = "/pick";
        input.args = new ArrayList<>();
        input.args.add("YAKUTSK");
        user = 1;
        username = "bobbyFreshKnowsHowToTest";
        response = UpdateHandler.commandReader(input, id, chat_id, username);
        assertNotNull(response);

        // STARTING GAME
        input.command = "/skipClaim";
        input.args = new ArrayList<>();
        response = UpdateHandler.commandReader(input, id, chat_id, username);
        assertNotNull(response);

        // STARTING GAME
        input.command = "/listAllGames";
        input.args = new ArrayList<>();
        response = UpdateHandler.commandReader(input, id, chat_id, username);
        assertNotNull(response);

        // STARTING GAME
        input.command = "/listMyGames";
        input.args = new ArrayList<>();
        response = UpdateHandler.commandReader(input, id, chat_id, username);
        assertNotNull(response);

        // STARTING GAME
        input.command = "/reinforce";
        input.args = new ArrayList<>();
        input.args.add("YAKUTSK");
        user = 0;
        username = "ClydeFreshKnowsHowToTest";
        response = UpdateHandler.commandReader(input, id, chat_id, username);
        assertNotNull(response);

        input.command = "/skipReinforce";
        response = UpdateHandler.commandReader(input, id, chat_id, username);
        assertNotNull(response);



    }
}
