import java.util.*;


/*///////////////////////////////////////////////////////////////////////
GameManager sets up the game by Initializing Players and the Board
as well as the Deck
todo: create a more robust way of reading these files that aren't dependent on path naming conventions
*//////////////////////////////////////////////////////////////////////*/
public class GameManager {
    static Player[] playerList;
    static int[] playerTurnPattern;
    static TurnManager TM;
    private static BoardManager BM;

    // Sets up ALL Game Variables, which must be testable upon initialization
    GameManager(int playerCount) {
        playerList = setPlayerList(playerCount);

        System.out.println("\n__Order of Turns:__");
        playerTurnPattern = getTurnPattern(playerCount, getIndexOfHighestRollIn(new Dice(), playerCount));

        System.out.println("__BoardSetup__");
        BM = new BoardManager();

        TM = new TurnManager();
    }

    public Player getPlayer(int playerID) {
        return playerList[playerID];
    }

    public BoardManager getBM() {return BM;}

    // Initializes PlayerList Array with the right amount of Infantry
    public Player[] setPlayerList(int size){
        Player[] playerList = new Player[size];
        int default_infantry = 0;

        switch(size){
            case 2:
                default_infantry=40;
                break;
            case 3:
                default_infantry=35;
                break;
            case 4:
                default_infantry=30;
                break;
            case 5:
                default_infantry=25;
                break;
            case 6:
                default_infantry=20;
                break;
        }

        for(int a=0; a<size; a++){
            playerList[a] = new Player(a, default_infantry);
        }

        return playerList;

    }

    // Get Highest Roll
    public int getIndexOfHighestRollIn(Dice die1,int iterations){
        int indexOfHighestRoll = -1;
        int valueOfHighestRoll = 1;
        for(int i=0; i<iterations; i++)
        {
            die1.roll();
            if (die1.getDiceValue() > valueOfHighestRoll)
            {
                valueOfHighestRoll = die1.getDiceValue();
                indexOfHighestRoll = i;
            }
        }
        return indexOfHighestRoll;
    }

    // Setup UserTurnPattern and display (optional)
    public int[] getTurnPattern(int size, int highest) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = (highest + i) % size;
            System.out.println((i+1)+ ". Player#" + array[i]);
        }
        return array;
    }


    // Run setup to finish Game setup for all players
    public static void runSetup(GameManager GM, Scanner scanner)
    {
        initializeTerritories(scanner);

        System.out.println("__Allocate the rest of your armies__");
        for (int i: playerTurnPattern) {
            GM.getPlayer(i).deployInfantry(BM, scanner);
        }
    }

    public static void runGame(GameManager GM, Scanner scanner){
        int turnID = 1;
        while(!GM.isGameOver()){

            for (int playerID: playerTurnPattern) {
                System.out.println("Player " + playerID + " turn: ");
                TM.save(makeTurn(scanner, playerList[playerID], turnID));
                turnID++;
            }
        }
    }

    // make a turn
    public static Turn makeTurn(Scanner scanner, Player p, int id) {
        Turn k = new Turn(BM, p, id);
        k.turnFunction(scanner);
        return k;
    }

    // Display Free territories -- removed displayPlayerTerritories for simplicity and less console clutter
    public static void initializeTerritories(Scanner setup){
        while(!BM.isAllTerritoriesInitialized()) {
            for (int i : playerTurnPattern) {
                for(String k : BM.getFreeTerritories()) System.out.println(k);
                BM.setInitialTerritory(playerList[i], setup);
            }
        }
    }

    // Player list only contains Players, and you can freely check if players have all the territories
    public boolean isGameOver(){
        for(Player i: playerList){
            if (i.isPlayerTheWinner(BM)){
                System.out.println("Someone won!?");
                return true;
            }
        }
        return false;
    }
}


