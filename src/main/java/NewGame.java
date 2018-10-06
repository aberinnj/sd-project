import java.util.*;

public class NewGame {

    public static Scanner getScanner() {
        Scanner scanner = new Scanner(System.in);
        return scanner;
    }

    public static String getBase() {
        String base = System.getProperty("user.dir");
        return base;
    }

    public static int getNumberPlayers(Scanner globalScanner) {
        System.out.println("Number of Players: ");
        int numPlayers = Integer.parseInt(globalScanner.nextLine());
        return numPlayers;

    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method for setting up list of players playing the game, throws an exception on
    invalid player-size. Setups up playerList size with corresponding settings
    *///////////////////////////////////////////////////////////////////////////////*/
    public static Player[] setupPlayerList(int size) throws Exception{
        if(size < 2 || size > 6)
            throw new Exception("Error: This game mode only supports 2-6 players.");

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

    /*///////////////////////////////////////////////////////////////////////
    Method used to determine turn-pattern for players by rolling a dice called
    turnSetupDice. Player who assumes the first highest number is considered
    to play first
    *//////////////////////////////////////////////////////////////////////*/
    public static int rollForSetup(Dice turnSetupDice, int numPlayers) {

        int highestID = -1;
        int highestNUM = 1;

        for(int i=0; i<numPlayers; i++)
        {
            turnSetupDice.roll();
            System.out.print("Rolling for Player#");
            System.out.print(i + "..."+turnSetupDice.getDiceValue());
            System.out.println();
            if (turnSetupDice.getDiceValue() > highestNUM)
            {
                highestNUM = turnSetupDice.getDiceValue();
                highestID = i;
            }
        }
        return highestID;
    }

    /*//////////////////////////////////////////////////////////
    Method to initialize the board manager
     *//////////////////////////////////////////////////////////
    public static BoardManager getBoardManager(String base) { //todo creaete a more robust way of reading these files that aren't dependent on path naming conventions
        BoardManager bm = new BoardManager(base + "/src/main/java/mapSource.json", base + "/src/main/java/deck.json");
        return bm;
    }

    /*///////////////////////////////////////////////////////////////////////
    Method serves to setup the BoardManager. Method runs while the boardmanager
    has an unoccupied territory and allows the users to select a territory
    per turn.
     *//////////////////////////////////////////////////////////////////////*/
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

