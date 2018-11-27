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
        _GameMaster.gamesListing.get("game").setPlayerList();
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
        _GameMaster.gamesListing.get("game").setPlayerList();
        String skipClaim = Responses.onSkipClaim(_GameMaster.gamesListing.get("game"));

        String skipReinforce = Responses.onSkipReinforce(_GameMaster.gamesListing.get("game"));
        assertEquals("her reinforces EASTERN UNITED STATES\n" +
                "his reinforces ICELAND\n" +
                "her reinforces ALBERTA\n" +
                "his reinforces WESTERN AUSTRALIA\n" +
                "her reinforces EASTERN AUSTRALIA\n" +
                "his reinforces CENTRAL AMERICA\n" +
                "her reinforces MADAGASCAR\n" +
                "his reinforces AFGHANISTAN\n" +
                "her reinforces URAL\n" +
                "his reinforces EAST AFRICA\n" +
                "her reinforces NORTH WEST TERRITORY\n" +
                "his reinforces BRAZIL\n" +
                "her reinforces INDONESIA\n" +
                "his reinforces CHINA\n" +
                "her reinforces IRKUTSK\n" +
                "his reinforces SCANDINAVIA\n" +
                "her reinforces NORTH AFRICA\n" +
                "his reinforces SIAM\n" +
                "her reinforces PERU\n" +
                "his reinforces CONGO\n" +
                "her reinforces QUEBEC\n" +
                "his reinforces MIDDLE EAST\n" +
                "her reinforces JAPAN\n" +
                "his reinforces GREENLAND\n" +
                "her reinforces VENEZUELA\n" +
                "his reinforces NEW GUINEA\n" +
                "her reinforces WESTERN UNITED STATES\n" +
                "his reinforces EGYPT\n" +
                "her reinforces SOUTHERN EUROPE\n" +
                "his reinforces UKRAINE\n" +
                "her reinforces GREAT BRITAIN\n" +
                "his reinforces WESTERN EUROPE\n" +
                "her reinforces NORTHERN EUROPE\n" +
                "his reinforces ARGENTINA\n" +
                "her reinforces SIBERIA\n" +
                "his reinforces ALASKA\n" +
                "her reinforces KAMCHATKA\n" +
                "his reinforces ONTARIO\n", skipReinforce);
    }

    @Test
    public void testPick()
    {
        _GameMaster.gamesListing = new HashMap<>();
        _GameMaster.allPlayersAndTheirGames = new HashMap<>();

        ChatInput INPUT = new ChatInput();
        INPUT.command = "/join";
        INPUT.args = new ArrayList<String>(){{add("game");}};

        String onCreate = Responses.onCreate(0, "game", "her", 123);
        String onJoin = Responses.onJoin(INPUT, 1, "his", (long)123);

        INPUT.command = "/join";
        INPUT.args = new ArrayList<String>(){{add("");}};

        String onPick = Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        assertEquals("You did not put a country to claim.", onPick);

        onPick = Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        assertEquals("Uh Oh! It is not your turn player#1, it is player#0's turn.", onPick);

        INPUT.args = new ArrayList<String>(){{add("YAKUTSK");}};
        onPick = Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        assertEquals("@her chose YAKUTSK.\n" +
                        "\nIt is now player @his's turn\n" +
                        "The following territories are still available\n" +
                        "SOUTH AFRICA\n" +
                        "KAMCHATKA\n" +
                        "ONTARIO\n" +
                        "SIBERIA\n" +
                        "ALASKA\n" +
                        "NORTHERN EUROPE\n" +
                        "ARGENTINA\n" +
                        "GREAT BRITAIN\n" +
                        "WESTERN EUROPE\n" +
                        "SOUTHERN EUROPE\n" +
                        "UKRAINE\n" +
                        "WESTERN UNITED STATES\n" +
                        "EGYPT\n" +
                        "VENEZUELA\n" +
                        "NEW GUINEA\n" +
                        "JAPAN\n" +
                        "GREENLAND\n" +
                        "QUEBEC\n" +
                        "MIDDLE EAST\n" +
                        "PERU\n" +
                        "CONGO\n" +
                        "NORTH AFRICA\n" +
                        "SIAM\n" +
                        "IRKUTSK\n" +
                        "SCANDINAVIA\n" +
                        "INDONESIA\n" +
                        "CHINA\n" +
                        "NORTH WEST TERRITORY\n" +
                        "BRAZIL\n" +
                        "URAL\n" +
                        "EAST AFRICA\n" +
                        "MADAGASCAR\n" +
                        "AFGHANISTAN\n" +
                        "EASTERN AUSTRALIA\n" +
                        "CENTRAL AMERICA\n" +
                        "ALBERTA\n" +
                        "WESTERN AUSTRALIA\n" +
                        "EASTERN UNITED STATES\n" +
                        "ICELAND\n" +
                        "INDIA\n" +
                        "MONGOLIA\n",
                onPick);

        INPUT.args = new ArrayList<String>(){{add("SOUTH AFRICA");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("KAMCHATKA");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("ONTARIO");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("SIBERIA");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("ALASKA");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("NORTHERN EUROPE");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("ARGENTINA");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("GREAT BRITAIN");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("WESTERN EUROPE");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("SOUTHERN EUROPE");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("NORTHERN EUROPE");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("ARGENTINA");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("GREAT BRITAIN");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("WESTERN EUROPE");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("SOUTHERN EUROPE");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("UKRAINE");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("WESTERN UNITED STATES");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("EGYPT");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("VENEZUELA");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("NEW GUINEA");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("JAPAN");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("GREENLAND");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("QUEBEC");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("MIDDLE EAST");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("PERU");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("CONGO");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("NORTH AFRICA");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("SIAM");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("IRKUTSK");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("SCANDINAVIA");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("INDONESIA");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("CHINA");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("NORTH WEST TERRITORY");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("BRAZIL");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("URAL");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("EAST AFRICA");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("MADAGASCAR");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("AFGHANISTAN");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("EASTERN AUSTRALIA");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("CENTRAL AMERICA");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("ALBERTA");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("WESTERN AUSTRALIA");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("EASTERN UNITED STATES");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("ICELAND");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("INDIA");}};
        Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("MONGOLIA");}};
        Responses.onPick(INPUT, 0, _GameMaster.gamesListing.get("game"));
        INPUT.args = new ArrayList<String>(){{add("UKRAINE");}};
        onPick = Responses.onPick(INPUT, 1, _GameMaster.gamesListing.get("game"));
        assertEquals("@his chose UKRAINE.\n", onPick);
        assertEquals(0, _GameMaster.gamesListing.get("game").nextTurnUserID);
    }

    @Test
    public void testOnReinforce(){
        _GameMaster.gamesListing = new HashMap<>();
        _GameMaster.allPlayersAndTheirGames = new HashMap<>();
        ChatInput INPUT = new ChatInput();
        INPUT.command = "/join";
        INPUT.args = new ArrayList<String>(){{add("game");}};

        String onCreate = Responses.onCreate(0, "game", "her", 123);
        String onJoin = Responses.onJoin(INPUT, 1, "his", (long)123);
        _GameMaster.gamesListing.get("game").setPlayerList();
        String skipClaim = Responses.onSkipClaim(_GameMaster.gamesListing.get("game"));
        String onReinforce;

        _GameMaster.gamesListing.get("game").playerDirectory.get(0).loseArmies(19);
        onReinforce = Responses.onReinforce(INPUT, 0, _GameMaster.gamesListing.get("game"));
        assertEquals("You already have dispatched all available armies", onReinforce);

        INPUT.args = new ArrayList<String>(){{add("YAKUTSK");}};
        _GameMaster.gamesListing.get("game").playerDirectory.get(0).addArmies(19);
        onReinforce = Responses.onReinforce(INPUT, 1, _GameMaster.gamesListing.get("game"));
        assertEquals("Uh Oh! It is not your turn player #1, it is player #0's turn.", onReinforce);


        onReinforce = Responses.onReinforce(INPUT, 0, _GameMaster.gamesListing.get("game"));
        assertEquals("@her reinforces YAKUTSK\n" +
                "@her you have 18 armies left\n", onReinforce);

        INPUT.args = new ArrayList<String>(){{add("SOUTH AFRICA");}};
        onReinforce = Responses.onReinforce(INPUT, 0, _GameMaster.gamesListing.get("game"));
        assertEquals("You do not own this territory.", onReinforce);

        INPUT.args = new ArrayList<String>(){{add("");}};
        onReinforce = Responses.onReinforce(INPUT, 0, _GameMaster.gamesListing.get("game"));
        assertEquals("You did not put a country to reinforce.", onReinforce);

    }

    @Test
    public void testFollowUpJoin(){
        _GameMaster.gamesListing = new HashMap<>();
        _GameMaster.allPlayersAndTheirGames = new HashMap<>();

        Responses.onCreate(0, "game", "her", 123);
        _GameMaster.gamesListing.get("game").addUser(1, "his", 123);

        String onFollowUpJoin = Responses.onFollowUpJoin("game");
        String expected = "Your game game is now starting. Order:\n";

        for (int user_id : _GameMaster.gamesListing.get("game").playerDirectory.keySet()) {
            expected += "@";
            expected += _GameMaster.gamesListing.get("game").playerDirectory.get(user_id).username;
            expected += "\n";
        }

        expected += "\n" +
                "\n" +
                "To begin claiming your initial territories, enter /listFreeTerritories to get the list of available territories again. " +
                "The list is automatically shown below. \n" +
                "\n" +
                "__AVAILABLE TERRITORIES__\n" +
                "YAKUTSK\n" +
                "SOUTH AFRICA\n" +
                "KAMCHATKA\n" +
                "ONTARIO\n" +
                "SIBERIA\n" +
                "ALASKA\n" +
                "NORTHERN EUROPE\n" +
                "ARGENTINA\n" +
                "GREAT BRITAIN\n" +
                "WESTERN EUROPE\n" +
                "SOUTHERN EUROPE\n" +
                "UKRAINE\n" +
                "WESTERN UNITED STATES\n" +
                "EGYPT\n" +
                "VENEZUELA\n" +
                "NEW GUINEA\n" +
                "JAPAN\n" +
                "GREENLAND\n" +
                "QUEBEC\n" +
                "MIDDLE EAST\n" +
                "PERU\n" +
                "CONGO\n" +
                "NORTH AFRICA\n" +
                "SIAM\n" +
                "IRKUTSK\n" +
                "SCANDINAVIA\n" +
                "INDONESIA\n" +
                "CHINA\n" +
                "NORTH WEST TERRITORY\n" +
                "BRAZIL\n" +
                "URAL\n" +
                "EAST AFRICA\n" +
                "MADAGASCAR\n" +
                "AFGHANISTAN\n" +
                "EASTERN AUSTRALIA\n" +
                "CENTRAL AMERICA\n" +
                "ALBERTA\n" +
                "WESTERN AUSTRALIA\n" +
                "EASTERN UNITED STATES\n" +
                "ICELAND\n" +
                "INDIA\n" +
                "MONGOLIA\n" +
                "\n";
        expected += "It is now player @"+ CommandUtils.getFirstPlayer(_GameMaster.gamesListing.get("game").playerDirectory).username + "'s turn\n";
        expected += "Enter /pick <country> to select and capture your initial territories. ";

        assertEquals(expected, onFollowUpJoin);
    }

    @Test
    public void testFollowUpPick(){
        _GameMaster.gamesListing = new HashMap<>();
        _GameMaster.allPlayersAndTheirGames = new HashMap<>();
        ChatInput INPUT = new ChatInput();
        INPUT.command = "/join";
        INPUT.args = new ArrayList<String>(){{add("game");}};

        Responses.onCreate(0, "game", "her", 123);
        Responses.onJoin(INPUT, 1, "his", (long)123);
        _GameMaster.gamesListing.get("game").setPlayerList();
        Responses.onSkipClaim(_GameMaster.gamesListing.get("game"));

        String onInitPick = Responses.onFollowUpInitPick(_GameMaster.gamesListing.get("game"));
        assertEquals("Initial territory claiming is complete." +
                "\nPlayers have remaining armies to dispatch to their territories. Please select a territory to dispatch your remaining armies to and fortify. \n" +
                "\n/reinforce <country> to select a country to dispatch one(1) army to." +
                "\n/listMyTerritories to view your territories and their status. (not implemented yet)" +
                "\n\nIt is now player @her's turn:" +
                "\nYour territories you can reinforce" +
                "\nYAKUTSK\n" +
                "KAMCHATKA\n" +
                "SIBERIA\n" +
                "NORTHERN EUROPE\n" +
                "GREAT BRITAIN\n" +
                "SOUTHERN EUROPE\n" +
                "WESTERN UNITED STATES\n" +
                "VENEZUELA\n" +
                "JAPAN\n" +
                "QUEBEC\n" +
                "PERU\n" +
                "NORTH AFRICA\n" +
                "IRKUTSK\n" +
                "INDONESIA\n" +
                "NORTH WEST TERRITORY\n" +
                "URAL\n" +
                "MADAGASCAR\n" +
                "EASTERN AUSTRALIA\n" +
                "ALBERTA\n" +
                "EASTERN UNITED STATES\n" +
                "INDIA", onInitPick);
        assertEquals(GameState.CLAIMING, _GameMaster.gamesListing.get("game").state);

    }

    @Test
    public void testFoollowUpReinforce(){
        _GameMaster.gamesListing = new HashMap<>();
        _GameMaster.allPlayersAndTheirGames = new HashMap<>();
        ChatInput INPUT = new ChatInput();
        INPUT.command = "/join";
        INPUT.args = new ArrayList<String>(){{add("game");}};

        Responses.onCreate(0, "game", "her", 123);
        Responses.onJoin(INPUT, 1, "his", (long)123);
        _GameMaster.gamesListing.get("game").setPlayerList();
        Responses.onSkipClaim(_GameMaster.gamesListing.get("game"));
        _GameMaster.gamesListing.get("game").state = GameState.CLAIMING;

        // this follow up triggers after the first person makes a turn, therefore, it is the 2nd player's turn
        String onFollowUpReinforce = Responses.onFollowUpReinforce(_GameMaster.gamesListing.get("game"));
        assertEquals("\nIt is now player @his's turn\n" +
                "Your territories are:\n" +
                "SOUTH AFRICA\n" +
                "ONTARIO\n" +
                "ALASKA\n" +
                "ARGENTINA\n" +
                "WESTERN EUROPE\n" +
                "UKRAINE\n" +
                "EGYPT\n" +
                "NEW GUINEA\n" +
                "GREENLAND\n" +
                "MIDDLE EAST\n" +
                "CONGO\n" +
                "SIAM\n" +
                "SCANDINAVIA\n" +
                "CHINA\n" +
                "BRAZIL\n" +
                "EAST AFRICA\n" +
                "AFGHANISTAN\n" +
                "CENTRAL AMERICA\n" +
                "WESTERN AUSTRALIA\n" +
                "ICELAND\n" +
                "MONGOLIA", onFollowUpReinforce);


        Responses.onSkipReinforce(_GameMaster.gamesListing.get("game"));
        onFollowUpReinforce = Responses.onFollowUpReinforce(_GameMaster.gamesListing.get("game"));
        assertEquals("Initial territory reinforcing is complete." +
                "\nPlayers can now begin making turns\n" +
                        "\n/beginTurn to begin your turn" +
                        "\n/endTurn to end your turn" +
                        "\n\nIt is now player @her's turn:", onFollowUpReinforce);
        assertEquals(GameState.ON_TURN, _GameMaster.gamesListing.get("game").state);

        Responses.onBeginTurn(_GameMaster.gamesListing.get("game"));

        INPUT.args = new ArrayList<String>(){{add("JAPAN");}};
        Responses.onReinforce(INPUT, 0, _GameMaster.gamesListing.get("game"));
        onFollowUpReinforce = Responses.onFollowUpReinforce(_GameMaster.gamesListing.get("game"));
        assertEquals(
                "\nCurrently, your territories are:\n" +
                "1 armies -- SOUTH AFRICA\n" +
                "2 armies -- ONTARIO\n" +
                "2 armies -- ALASKA\n" +
                "2 armies -- ARGENTINA\n" +
                "2 armies -- WESTERN EUROPE\n" +
                "2 armies -- UKRAINE\n" +
                "2 armies -- EGYPT\n" +
                "2 armies -- NEW GUINEA\n" +
                "2 armies -- GREENLAND\n" +
                "2 armies -- MIDDLE EAST\n" +
                "2 armies -- CONGO\n" +
                "2 armies -- SIAM\n" +
                "2 armies -- SCANDINAVIA\n" +
                "2 armies -- CHINA\n" +
                "2 armies -- BRAZIL\n" +
                "2 armies -- EAST AFRICA\n" +
                "2 armies -- AFGHANISTAN\n" +
                "2 armies -- CENTRAL AMERICA\n" +
                "2 armies -- WESTERN AUSTRALIA\n" +
                "2 armies -- ICELAND\n" +
                "1 armies -- MONGOLIA", onFollowUpReinforce);



    }

    @Test
    public void testBeginTurn(){
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
        String onBeginTurn = Responses.onBeginTurn(_GameMaster.gamesListing.get("game"));
        assertEquals("Player @her has begin their turn. You may: \n" +
                "\n/tradecards to trade your cards if you have pairs" +
                "\n/reinforce <country> to reinforce new free armies to your territory" +
                "\n/attack <invading> <defending> <number of armies to attack with MAX.3> <number of armies to defend with MAX.2>" +
                "\n/fortify <fortify from> <fortify neighbor> <number of armies to transfer>" +
                "\n/buycredit <amount> to buy credit" +
                "\n/buystuff <# of undos> <# of cards> to buy stuff with your credits" +
                "\n/endturn to finally end your turn" +
                "\n\nYou have 7 available armies to reinforce\n" +
                "\n" +
                "\n" +
                "For attacking, you have the following able territories: \n" +
                "KAMCHATKA: 2 armies, CAN ATTACK\n" +
                "SIBERIA: 2 armies, CAN ATTACK\n" +
                "NORTHERN EUROPE: 2 armies, CAN ATTACK\n" +
                "GREAT BRITAIN: 2 armies, CAN ATTACK\n" +
                "SOUTHERN EUROPE: 2 armies, CAN ATTACK\n" +
                "WESTERN UNITED STATES: 2 armies, CAN ATTACK\n" +
                "VENEZUELA: 2 armies, CAN ATTACK\n" +
                "JAPAN: 2 armies, CAN ATTACK\n" +
                "QUEBEC: 2 armies, CAN ATTACK\n" +
                "PERU: 2 armies, CAN ATTACK\n" +
                "NORTH AFRICA: 2 armies, CAN ATTACK\n" +
                "IRKUTSK: 2 armies, CAN ATTACK\n" +
                "INDONESIA: 2 armies, CAN ATTACK\n" +
                "NORTH WEST TERRITORY: 2 armies, CAN ATTACK\n" +
                "URAL: 2 armies, CAN ATTACK\n" +
                "MADAGASCAR: 2 armies, CAN ATTACK\n" +
                "EASTERN AUSTRALIA: 2 armies, CAN ATTACK\n" +
                "ALBERTA: 2 armies, CAN ATTACK\n" +
                "EASTERN UNITED STATES: 2 armies, CAN ATTACK\n" +
                "\n" +
                "__YOU HAVE___\n" +
                "\n" +
                "No Cards\n" +
                "\n" +
                "\t0 undos\n" +
                "\t0.0 credit", onBeginTurn);


        Player player = CommandUtils.getPlayer(_GameMaster.gamesListing.get("game"));
        player.getHand().get("INFANTRY").push(new Card("CHINA", "INFANTRY"));
        onBeginTurn = Responses.onBeginTurn(_GameMaster.gamesListing.get("game"));

        assertEquals("Player @her has begin their turn. You may: \n" +
                "\n/tradecards to trade your cards if you have pairs" +
                "\n/reinforce <country> to reinforce new free armies to your territory" +
                "\n/attack <invading> <defending> <number of armies to attack with MAX.3> <number of armies to defend with MAX.2>" +
                "\n/fortify <fortify from> <fortify neighbor> <number of armies to transfer>" +
                "\n/buycredit <amount> to buy credit" +
                "\n/buystuff <# of undos> <# of cards> to buy stuff with your credits" +
                "\n/endturn to finally end your turn" +
                "\n\nYou have 14 available armies to reinforce\n" +
                "\n" +
                "\n" +
                "For attacking, you have the following able territories: \n" +
                "KAMCHATKA: 2 armies, CAN ATTACK\n" +
                "SIBERIA: 2 armies, CAN ATTACK\n" +
                "NORTHERN EUROPE: 2 armies, CAN ATTACK\n" +
                "GREAT BRITAIN: 2 armies, CAN ATTACK\n" +
                "SOUTHERN EUROPE: 2 armies, CAN ATTACK\n" +
                "WESTERN UNITED STATES: 2 armies, CAN ATTACK\n" +
                "VENEZUELA: 2 armies, CAN ATTACK\n" +
                "JAPAN: 2 armies, CAN ATTACK\n" +
                "QUEBEC: 2 armies, CAN ATTACK\n" +
                "PERU: 2 armies, CAN ATTACK\n" +
                "NORTH AFRICA: 2 armies, CAN ATTACK\n" +
                "IRKUTSK: 2 armies, CAN ATTACK\n" +
                "INDONESIA: 2 armies, CAN ATTACK\n" +
                "NORTH WEST TERRITORY: 2 armies, CAN ATTACK\n" +
                "URAL: 2 armies, CAN ATTACK\n" +
                "MADAGASCAR: 2 armies, CAN ATTACK\n" +
                "EASTERN AUSTRALIA: 2 armies, CAN ATTACK\n" +
                "ALBERTA: 2 armies, CAN ATTACK\n" +
                "EASTERN UNITED STATES: 2 armies, CAN ATTACK\n" +
                "\n" +
                "__YOU HAVE___" +
                "\nCHINA: INFANTRY"+
                "\n\n" +
                "\t0 undos\n" +
                "\t0.0 credit", onBeginTurn);
    }



}
