import junit.framework.TestCase;
import org.junit.Test;
import java.util.ArrayList;

public class UpdateHandlerTest extends TestCase {

    @Test
    public void testCommands(){

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
        assertEquals("Command NOE not found.\n\nRisk has the following commands available: \n" +
                "/create to create a new instance of the game\n" +
                "/join <gameID> to select an available session\n" +
                "/listAllGames to list all available game sessions\n" +
                "/listMyGames to list all your game sessions\n" +
                "/getStatus to get the status on the chat's current game\n" +
                "/load to replay a game.", response);


    }
}
