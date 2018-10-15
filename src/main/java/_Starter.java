import java.util.*;

/*////////////////////////////////////////////////////////////////////////////////
_Starter Class starts a _Starter by initializing GameManager and runs a game

todo: Broadcast to Twitter
todo: Do not import all of java.util
*///////////////////////////////////////////////////////////////////////////////*/
public class _Starter {
    Scanner scanner;
    String base;
    static int playerCount;

    /* Initialize Member Variables
     * Note that playerCount is initialized to 0. Call setNumberOfPlayers to initialize*/
    _Starter(){
        this.scanner = new Scanner(System.in);
        this.base = System.getProperty("user.dir");
        playerCount = 0;
    }

    /*//////////////////////////////////////////////////////////////////////////////////

    Main queries

    //////////////////////////////////////////////////////////////////////////////////*/
    public static void main(String[] args) {
        System.out.println(" __________________");
        System.out.println("|   Game of Risk   |");
        System.out.println(" '''''''''''''''''' ");


        _Starter NG = new _Starter();
        GameManager GM = new GameManager();

        if(GM.baseQuery("Would you like to load a game? (Yes/No) ", NG.scanner))
        {
            // Start Loading
        } else{
            while(NG.setNumberOfPlayers()){}
            GM.initializeAsNormal(playerCount);
            GM.runSetup(GM, NG.scanner);
        }

        GM.runGame(GM, NG.scanner);
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


    /*
    *
            // LOAD
            System.out.println("Which turn would you like to load from?");
            System.out.println("Enter -1 to begin from the beginning");
            turnNumber = Integer.parseInt(globalScanner.nextLine());

            LoadGame LG = new LoadGame();
            JsonObject loadedTurn = LG.LoadGame(turnNumber, bm);
            numPlayers = LG.getNumPlayers(loadedTurn);

            playerList = LG.setPlayers(bm, numPlayers, loadedTurn);
    *
    * */

}

