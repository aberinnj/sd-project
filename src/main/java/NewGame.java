import java.util.*;

/*////////////////////////////////////////////////////////////////////////////////
NewGame Class starts a NewGame by initializing GameManager and runs a game
todo: AWS S3 Integration
todo: Do not import all of java.util
*///////////////////////////////////////////////////////////////////////////////*/
public class NewGame {
    Scanner scanner;
    String base;
    static int playerCount;

    /* Initialize Member Variables
     * If number of players is invalid or out-of-bounds, scan again */
    NewGame(){
        this.scanner = new Scanner(System.in);
        this.base = System.getProperty("user.dir");
        while(setNumberOfPlayers(this.scanner));
    }

    // Make a new game, setup and run
    public static void main(String[] args) {
        NewGame NG = new NewGame();
        GameManager GM = new GameManager(NG.scanner, NG.base, playerCount);
        //GM.runSetup();
        //GM.runGame(NG.scanner);
    }

    // Query for number of players for this new game
    public static boolean setNumberOfPlayers(Scanner scanner) {
        System.out.println("Number of Players: ");
        try {
            int size = Integer.parseInt(scanner.nextLine());
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

    public static void initializeTerritories(BoardManager bm, Scanner setup, int[] playerTurnPattern, Player[] playerList){
        while(!bm.isAllTerritoriesInitialized()) {
            // FIX ERROR WHERE MORE THAN TWO PLAYERS GETS STUCK IN LOOP
            for (int i : playerTurnPattern) {
                bm.displayUntakenTerritories();                                                                         //BoardManager's displayUntakenTerritories to display untaken territories
                playerList[i].displayPlayerTerritories(bm);                                                             //player's displayPlayerTerritories to display player's territories
                bm.setInitialTerritory(playerList[i], setup);                                                                  //BoardManager queries user for which territory to occupy
            }
        }
    }
}

