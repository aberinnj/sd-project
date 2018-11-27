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

        onCreate = Responses.onCreate(2, "G4m3IDs0Un1qu3", "bobby", 1234567);
        assertEquals("@bobby sorry! You are already playing a game.", onCreate);
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

    @Test
    public void testSkipClaim(){
        _GameMaster.gamesListing = new HashMap<>();
        _GameMaster.allPlayersAndTheirGames = new HashMap<>();
        ChatInput INPUT = new ChatInput();
        INPUT.command = "/join";
        INPUT.args = new ArrayList<String>(){{add("game");}};

        String onCreate = Responses.onCreate(0, "game", "her", 123);
        String onJoin = Responses.onJoin(INPUT, 1, "his", (long)123);
        String skipClaim = Responses.onSkipClaim(_GameMaster.gamesListing.get("game"));
        assertEquals("her chose YAKUTSK\n" +
                        "his chose SOUTH AFRICA\n" +
                        "her chose KAMCHATKA\n" +
                        "his chose ONTARIO\n" +
                        "her chose SIBERIA\n" +
                        "his chose ALASKA\n" +
                        "her chose NORTHERN EUROPE\n" +
                        "his chose ARGENTINA\n" +
                        "her chose GREAT BRITAIN\n" +
                        "his chose WESTERN EUROPE\n" +
                        "her chose SOUTHERN EUROPE\n" +
                        "his chose UKRAINE\n" +
                        "her chose WESTERN UNITED STATES\n" +
                        "his chose EGYPT\n" +
                        "her chose VENEZUELA\n" +
                        "his chose NEW GUINEA\n" +
                        "her chose JAPAN\n" +
                        "his chose GREENLAND\n" +
                        "her chose QUEBEC\n" +
                        "his chose MIDDLE EAST\n" +
                        "her chose PERU\n" +
                        "his chose CONGO\n" +
                        "her chose NORTH AFRICA\n" +
                        "his chose SIAM\n" +
                        "her chose IRKUTSK\n" +
                        "his chose SCANDINAVIA\n" +
                        "her chose INDONESIA\n" +
                        "his chose CHINA\n" +
                        "her chose NORTH WEST TERRITORY\n" +
                        "his chose BRAZIL\n" +
                        "her chose URAL\n" +
                        "his chose EAST AFRICA\n" +
                        "her chose MADAGASCAR\n" +
                        "his chose AFGHANISTAN\n" +
                        "her chose EASTERN AUSTRALIA\n" +
                        "his chose CENTRAL AMERICA\n" +
                        "her chose ALBERTA\n" +
                        "his chose WESTERN AUSTRALIA\n" +
                        "her chose EASTERN UNITED STATES\n" +
                        "his chose ICELAND\n" +
                        "her chose INDIA\n" +
                        "his chose MONGOLIA\n",
                skipClaim);
    }

    @Test
    public void testSkipReinforce(){
        _GameMaster.gamesListing = new HashMap<>();
        _GameMaster.allPlayersAndTheirGames = new HashMap<>();
        ChatInput INPUT = new ChatInput();
        INPUT.command = "/join";
        INPUT.args = new ArrayList<String>(){{add("game");}};

        String onCreate = Responses.onCreate(0, "game", "her", 123);
        String onJoin = Responses.onJoin(INPUT, 1, "his", (long)123);
        String skipClaim = Responses.onSkipClaim(_GameMaster.gamesListing.get("game"));
        _GameMaster.gamesListing.get("game").setPlayerList();

        String skipReinforce = Responses.onSkipReinforce(_GameMaster.gamesListing.get("game"));
        assertEquals("her reinforces EASTERN AUSTRALIA\n" +
                "his reinforces AFGHANISTAN\n" +
                "her reinforces URAL\n" +
                "his reinforces BRAZIL\n" +
                "her reinforces INDONESIA\n" +
                "his reinforces SCANDINAVIA\n" +
                "her reinforces NORTH AFRICA\n" +
                "his reinforces CONGO\n" +
                "her reinforces QUEBEC\n" +
                "his reinforces GREENLAND\n" +
                "her reinforces VENEZUELA\n" +
                "his reinforces EGYPT\n" +
                "her reinforces SOUTHERN EUROPE\n" +
                "his reinforces WESTERN EUROPE\n" +
                "her reinforces NORTHERN EUROPE\n" +
                "his reinforces ALASKA\n" +
                "her reinforces KAMCHATKA\n" +
                "his reinforces SOUTH AFRICA\n" +
                "her reinforces INDIA\n" +
                "his reinforces ICELAND\n" +
                "her reinforces ALBERTA\n" +
                "his reinforces CENTRAL AMERICA\n" +
                "her reinforces MADAGASCAR\n" +
                "his reinforces EAST AFRICA\n" +
                "her reinforces NORTH WEST TERRITORY\n" +
                "his reinforces CHINA\n" +
                "her reinforces IRKUTSK\n" +
                "his reinforces SIAM\n" +
                "her reinforces PERU\n" +
                "his reinforces MIDDLE EAST\n" +
                "her reinforces JAPAN\n" +
                "his reinforces NEW GUINEA\n" +
                "her reinforces WESTERN UNITED STATES\n" +
                "his reinforces UKRAINE\n" +
                "her reinforces GREAT BRITAIN\n" +
                "his reinforces ARGENTINA\n" +
                "her reinforces SIBERIA\n" +
                "his reinforces ONTARIO\n", skipReinforce);
    }
}
