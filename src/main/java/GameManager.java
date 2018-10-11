import java.util.*;


/*///////////////////////////////////////////////////////////////////////
GameManager sets up the game by Initializing Players and the Board
as well as the Deck
todo: create a more robust way of reading these files that aren't dependent on path naming conventions
*//////////////////////////////////////////////////////////////////////*/
public class GameManager {
    static Dice die1;
    static Dice die2;
    static Dice die3;
    static Player[] playerList;
    static int[] playerTurnPattern;
    static BoardManager bm;

    // Sets up ALL Game Variables, which must be testable upon initialization
    GameManager(Scanner scanner, String base, int playerCount) {

        die1 = new Dice();
        die2 = new Dice();
        die3 = new Dice();

        System.out.println("__Game of Risk__");
        playerList = setPlayerList(playerCount);

        System.out.println("\n__Order of Turns:__");
        playerTurnPattern = getTurnPattern(playerCount, getIndexOfHighestRollIn(playerCount));

        System.out.println("__BoardSetup__");
        bm = new BoardManager(base + "/src/files/deck.json");
    }

    // Initializes PlayerList Array with the right amount of Infantry
    public static Player[] setPlayerList(int size){
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
    public int getIndexOfHighestRollIn(int iterations){
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
    public static int[] getTurnPattern(int size, int highest) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = (highest + i) % size;
            System.out.println((i+1)+ ". Player#" + array[i]);
        }
        return array;
    }


    // Run setup to finish Game setup
    public static void runSetup(Scanner scanner)
    {
        initializeTerritories(scanner);

        System.out.println("__Allocate the rest of your armies__");
        for (int i: playerTurnPattern) {
            playerList[i].deployInfantry(bm, scanner);
        }
    }

    public static void runGame(Scanner scanner){
        MoveManager MM = new MoveManager();
        initialize(bm, MM, playerList, playerList.length, -1);

        // Game Start
        while(!isGameOver(bm, playerList)){
            int turnNumber = 0;
            Player playerCurrent = null;
            for (int player: playerTurnPattern)
            {
                playerCurrent = playerList[player];
                System.out.println("Player " + player + " turn");

                Turn turn = new Turn(MM, bm, playerCurrent, playerList, scanner);

                turnNumber++;
            }
        }
    }

    public static void initialize(BoardManager bm, MoveManager MM, Player[] playerList, int numPlayers, int playerID) {
        Move initialize = MM.addToMoveManager(bm, MM, playerList, numPlayers, -1);
        MM.addMove(initialize);
    }

    // Display Free territories -- removed displayPlayerTerritories for simplicity and less console clutter
    public static void initializeTerritories(Scanner setup){
        while(!bm.isAllTerritoriesInitialized()) {
            for (int i : playerTurnPattern) {
                bm.displayFreeTerritories();
                bm.setInitialTerritory(playerList[i], setup);
            }
        }
    }

    public static boolean isGameOver(BoardManager bm, Player[] playerList){
        Player[] tempList = playerList.clone();
        for(Player i: tempList){
            if (i.isPlayerTheWinner(bm)){
                System.out.println("Someone won!?");
                return true;
            }
            //todo: remove player method
            //if (i.hasPlayerLost(bm)) {
            //    System.out.println("Player " + i + " is no longer in the game");
            //    for (Player j = i; j<playerList.length - 1; j++) {
            //        playerList[j] = playerList[j+1]
            //    }
            //}
        }
        return false;
    }
}


