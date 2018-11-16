import java.io.IOException;

import java.util.*;

/*////////////////////////////////////////////////////////////////////////////////
_GameStarter Class starts _GameStarter by calling Loader methods to get game-data and
calls GameManager methods to store them.

or by initializing GameManager and runs a game
*///////////////////////////////////////////////////////////////////////////////*/
public class _GameStarter {
    Scanner scanner;
    String base;
    String bucketName;
    String objectOfGameInstance;
    static int playerCount;
    static GameManager GM;

    /* Initialize Member Variables
     * Note that playerCount is initialized to 0. Call setNumberOfPlayers to initialize*/
    _GameStarter(){
        this.scanner = new Scanner(System.in);
        this.base = System.getProperty("user.dir");
        bucketName = "risk-game-team-one";
        playerCount = 0;

        GM = new GameManager();
    }

    /*//////////////////////////////////////////////////////////////////////////////////
    Main function
    //////////////////////////////////////////////////////////////////////////////////*/
    public void initGame(String id) throws IOException {
        defaultStart(id);
        GM.runGame(GM, scanner);
    }

    public void loadGame(String id) throws IOException {
        Loader loader = new Loader(bucketName);
        GM.loadGame(0, loader);
        GM.runGame(GM, scanner);
    }

    public void defaultStart(String id) {
        while (setNumberOfPlayers()) {}
        GM.initializeAsNormal(playerCount);
        GameManager.fileObjKeyName = id;
        GameManager.runSetup(GM, scanner);
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

