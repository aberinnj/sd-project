import java.io.IOException;

import java.util.*;

/*////////////////////////////////////////////////////////////////////////////////
_GameStarter Class starts _GameStarter by calling Loader methods to get game-data and
calls GameManager methods to store them.

or by initializing GameManager and runs a game
*///////////////////////////////////////////////////////////////////////////////*/
public class _GameStarter {
    Messenger messenger;
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
    todo: enable functions below
    //////////////////////////////////////////////////////////////////////////////////*/
    public void initGame(Game thisGame) throws IOException, InterruptedException {
        this.messenger = thisGame.messenger;
        playerCount = thisGame.playerDirectory.size();
        GM.setMessenger(thisGame);
        defaultStart(thisGame);
        System.out.println("Init done. \n");
        //GM.runGame(GM, thisGame.messenger, thisGame);
    }

    public void loadGame(Game thisGame) throws IOException, InterruptedException {
        Loader loader = new Loader();
        GM.loadGame(0, loader);
        GM.runGame(GM, thisGame.messenger, thisGame);
    }

    public void defaultStart(Game thisGame) throws InterruptedException {
        //while (setNumberOfPlayers()) {}
        GM.initializeAsNormal(playerCount, thisGame);
        GameManager.fileObjKeyName = thisGame.gameID;
    }

    /* NOT CALLED WHEN USING TELEGRAM
    Query for number of players for this new game
    Returns true if it succeeds, Returns false otherwise
    */
    public boolean setNumberOfPlayers() throws InterruptedException {
        //System.out.println("Number of Players: ");
        messenger.putMessage("Enter Number of Players: ");
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

