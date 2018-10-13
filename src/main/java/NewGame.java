import java.util.*;

/*////////////////////////////////////////////////////////////////////////////////
NewGame Class starts a NewGame by initializing GameManager and runs a game
todo: AWS S3 Integration
todo: Broadcast to Twitter
todo: Do not import all of java.util
*///////////////////////////////////////////////////////////////////////////////*/
public class NewGame {
    Scanner scanner;
    String base;
    String gameID;
    static int playerCount;

    /* Initialize Member Variables
     * Note that playerCount is initialized to 0. Call setNumberOfPlayers to initialize*/
    NewGame(){
        this.scanner = new Scanner(System.in);
        this.base = System.getProperty("user.dir");
        this.gameID = "risk_"+ UUID.randomUUID();
        playerCount = 0;
    }

    // Make a new game, setup and run, the shorter, the better
    public static void main(String[] args) {
        System.out.println("__Game of Risk__");
        NewGame NG = new NewGame();
        while(NG.setNumberOfPlayers()){}
        GameManager GM = new GameManager(NG.base, NG.getPlayerCount());
        GM.runSetup(GM, NG.scanner);
        GM.runGame(GM, NG.scanner);
    }

    // Query for number of players for this new game
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

}

