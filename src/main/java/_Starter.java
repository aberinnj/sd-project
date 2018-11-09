import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import java.io.IOException;

import java.util.*;

/*////////////////////////////////////////////////////////////////////////////////
_Starter Class starts _Starter by calling Loader methods to get game-data and
calls GameManager methods to store them.

or by initializing GameManager and runs a game


todo: Broadcast to Twitter
todo: Do not import all of java.util
*///////////////////////////////////////////////////////////////////////////////*/
public class _Starter {
    Scanner scanner;
    String base;
    String bucketName;
    String objectOfGameInstance;
    static int playerCount;

    /* Initialize Member Variables
     * Note that playerCount is initialized to 0. Call setNumberOfPlayers to initialize*/
    _Starter(){
        System.out.println(" __________________");
        System.out.println("|   Game of Risk   |");
        System.out.println(" '''''''''''''''''' ");

        this.scanner = new Scanner(System.in);
        this.base = System.getProperty("user.dir");
        bucketName = "risk-game-team-one";
        playerCount = 0;
    }

    /*//////////////////////////////////////////////////////////////////////////////////
    Main function
    //////////////////////////////////////////////////////////////////////////////////*/
    public static void main(String[] args) throws IOException {

        _Starter NG = new _Starter();
        GameManager GM = new GameManager();

        if(GM.baseQuery("Would you like to load a game? (Yes/No) ", NG.scanner))
        {
            // init
            Loader loader = new Loader(NG.bucketName);
            // get game
            if(NG.queryGameChecker(loader.listObjects(NG.bucketName))){
                // download game
                // Need to choose which turn to retrieve
                int turnToRetrieve = 0;

                GM.loadGame(turnToRetrieve, loader);


                // queryForTurn or cancel (if cancel call DefaultStart(GM)) should still be testable) see GameManagerTest for example
                // if selected, for each important data, get/pull data from TurnManager

                // default setPlayerList(id is -1, hand is empty, etc.) these default values are changed below
                // then call GameManager.setTurnPattern
                // then call GameManager.setDeck(DeckData), GameManager.setPlayer(DeckData, TerritoryData, ArmyData) for each player),
                // then call GameManager.setBoard(BoardHashMap) to set Territories
                // run game below
            } else NG.defaultStart(GM);
        }
        else {
            NG.defaultStart(GM);
        }
        GM.runGame(GM, NG.scanner);
    }

    public void defaultStart(GameManager GM) {
        while (setNumberOfPlayers()) {}
        GM.initializeAsNormal(playerCount);
        GM.runSetup(GM, scanner);
        GameManager.fileObjKeyName = "risk-game-" + UUID.randomUUID().toString();
    }

    // Query for number of players for this new game
    // Returns true if it succeeds, Returns false otherwise
    public boolean setNumberOfPlayers() {
        System.out.println("Number of Players: ");
        try {
            int size = Integer.parseInt(scanner.nextLine());
            System.out.println(size);
            if (size < 2 || size > 6) {
                return true;
            } else{
                playerCount = size;
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // made mainly for testing, playerCount can be accessed directly
    public int getPlayerCount(){
        return playerCount;
    }

    public boolean queryGameChecker(ArrayList<String> games)
    {
        String name;
        if (games.size() == 0){
            System.out.println("No previous games are found. Hence, none can be loaded!");
            System.out.println("Starting default start! Setting up a new game. \n");
            return false;
        } else {
            do {
                name = queryGame(games);
            } while(name == null);

            if(name.toLowerCase().equals("cancel")) return false;
        }
        // set Object for AWS S3
        GameManager.fileObjKeyName = name;
        return true;
    }

    public String queryGame(ArrayList<String> games)
    {
        System.out.println("Loadable games: ");
        for(String i: games) System.out.println(i);
        System.out.println("CANCEL\n");

        String select = scanner.nextLine();
        if(select.toLowerCase().equals("cancel")) return select;
        else if(games.contains(select.toLowerCase())) {
            return select;
        } else{
            System.out.println("Error: Cannot find game " + select);
            return null;
        }
    }

}

