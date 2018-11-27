import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class ResponseTest extends TestCase {
    @Test
    public void testOnStart(){
        String onStart = Responses.onStart();
        assertEquals("Welcome! To play risk, you can either do the following:" +

                "\n/create your own session for this chat, if the chat is not playing" +

                "\n/join if the chat is playing and there's enough players." +

                "\n/help to view all the available commands.", onStart);
    }

    @Test
    public void testListAllGames(){
        _GameMaster.gamesListing = new HashMap<>();
        String onListAll;

        onListAll = Responses.onListAllGames();
        assertEquals("There are no current sessions. /create to create your own session.", onListAll);

        _GameMaster.gamesListing.put("GAME_ONE", new Game());
        onListAll = Responses.onListAllGames();
        assertEquals("The following game sessions have been found.\n0 player(s) \tGAME_ONE\n", onListAll);
    }

    @Test
    public void testListMyGames(){
        _GameMaster.gamesListing = new HashMap<>();
        String onListMine;

        onListMine = Responses.onListMyGames(2);
        assertEquals("There are no current sessions. /create to create your own session.", onListMine);

        _GameMaster.gamesListing.put("GAME_ONE", new Game());
        onListMine = Responses.onListMyGames(2);
        assertEquals("No games are found.", onListMine);


        _GameMaster.gamesListing.get("GAME_ONE").playerDirectory.put(2, new Player(2, "nobody", 1234567, 25));
        onListMine = Responses.onListMyGames(2);
        assertEquals("The following are your games: \n1 player(s) \tGAME_ONE\n", onListMine);



    }

    @Test
    public void testCreate(){
        _GameMaster.gamesListing = new HashMap<>();
        _GameMaster.allPlayersAndTheirGames = new HashMap<>();
        String onCreate = Responses.onCreate(2, "G4m3IDs0Un1qu3", "bobby", 1234567);
        assertEquals("Creating a new game session. \nGameID: G4m3IDs0Un1qu3", onCreate);
    }

    @Test
    public void testHelp(){
        String onHelp = Responses.onHelp();
        assertEquals("Risk has the following commands available: \n" +

                "/create to create a new instance of the game\n" +

                "/join <gameID> to select an available session\n" +

                "/listAllGames to list all available game sessions\n" +

                "/listMyGames to list all your game sessions\n" +

                "/getStatus to get the status on the chat's current game\n" +

                "/load to replay a game.",onHelp);

    }

    @Test
    public void testJoin(){
        _GameMaster.gamesListing = new HashMap<>();
        _GameMaster.allPlayersAndTheirGames = new HashMap<>();
        String onCreate = Responses.onCreate(2, "G4m3IDs0Un1qu3", "bobby", 1234567);

        ChatInput INPUT = new ChatInput();
        INPUT.command = "/join";
        INPUT.args = new ArrayList<>();
        String onJoin;

        onJoin = Responses.onJoin(INPUT, 1, "cherry", (long)1234567);
        assertEquals("You did not provide a gameID", onJoin);

        INPUT.args.add("DOES_NOT_EXIST");
        onJoin = Responses.onJoin(INPUT, 1, "cherry", (long)1234567);
        assertEquals("The game does not exist.", onJoin);

        INPUT.args = new ArrayList<>();
        INPUT.args.add("G4m3IDs0Un1qu3");
        onJoin = Responses.onJoin(INPUT, 1, "cherry", (long)1234567);
        assertEquals("You have successfully joined.", onJoin);

        INPUT.args = new ArrayList<>();
        INPUT.args.add("G4m3IDs0Un1qu3");
        onJoin = Responses.onJoin(INPUT, 1, "cherry", (long)1234567);
        assertEquals("You are already in this game.", onJoin);


    }
}
