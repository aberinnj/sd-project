import java.util.InputMismatchException;
import java.util.Scanner;

public class GameManager {

    public static String base = System.getProperty("user.dir");                                                         // sets the base
    private static int playerSize;                                                                                      // size of players playing
    private static Player[] playerList;                                                                                 // list of players
    private static int[] playerTurnPattern;                                                                             // list of turns to loop through

    /*////////////////////////////////////////////////////////////////////////////////
    main function executing all game functions from setup to winning a game .
    Segments
        1. Introduction and setting up the scanner
        2. Setting up playerslist and pattern of turns by auto-rolling (rollForSetup)
        3. Sets up the board by reading mapsource and letting players pick territories
    *///////////////////////////////////////////////////////////////////////////////*/
    public static void main(String[] args) {

        // Game Setup
        System.out.println("Game of Risk");
        System.out.println("------------------------");
        System.out.println("PlayerSetup");
        System.out.println("------------------------");
        Scanner setup = new Scanner(System.in);

        while(true){
            try{
                System.out.print("Number of Players: ");
                playerSize = setup.nextInt();
                setupPlayerList(playerSize);
                System.out.println();
                break;
            }catch (Exception e){
                System.out.println(e.getMessage());
                continue;
            }
        }
        rollForSetup();

        BoardManager bm = new BoardManager(base + "\\src\\main\\java\\mapSource.json", base + "\\src\\main\\java\\deck.json");
        initializeTerritories(bm);
        System.out.println("------------------------");
        System.out.println("Allocate the rest of your armies");
        System.out.println("------------------------");
        shipAllArmies(bm);

        // Game Start
        while(!isGameOver(bm)){

            for (int i: playerTurnPattern)
            {
                // access playerList[i]
                // 1. place new Armies
                // 2. attacking
                // 3. fortifying position
                fortifyPlayersTerritory(bm, i);
            }
            // break for now;
        }

    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method for setting up list of players playing the game, throws an exception on
    invalid player-size. Setups up playerList size with corresponding settings
    *///////////////////////////////////////////////////////////////////////////////*/
    private static void setupPlayerList(int size) throws Exception{
        if(size < 2 || size > 6)
            throw new Exception("Error: This game mode only supports 2-6 players.");

        playerList = new Player[size];
        int default_infantry;

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
            default:
                default_infantry=0;
                break;
        }

        for(int a=0; a<size; a++){
            playerList[a] = new Player(a, default_infantry);
        }
    }

    /*///////////////////////////////////////////////////////////////////////
    Method used to determine turn-pattern for players by rolling a dice called
    turnSetupDice. Player who assumes the first highest number is considered
    to play first
    *//////////////////////////////////////////////////////////////////////*/
    private static void rollForSetup(){
        playerTurnPattern = new int[playerSize];
        Dice turnSetupDice = new Dice();
        int highestID = -1;
        int highestNUM = 1;

        System.out.println("-----------------------");
        System.out.println("BoardSetup");
        System.out.println("Note: Whoever gets the first of the same highest");
        System.out.println("number, is considered to have the highest number");
        System.out.println("------------------------");

        for(int i=0; i<playerSize; i++)
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
        setupTurnPattern(highestID);
    }

    /*///////////////////////////////////////////////////////////////////////
    Method serves to setup static member, playerTurnPattern. Argument i is the
    index to start the pattern (which is considered the id of the player)
     *//////////////////////////////////////////////////////////////////////*/
    private static void setupTurnPattern(int i){
        System.out.println("\nOrder of Turns:");
        for(int b=0; b<playerSize; b++)
        {
            playerTurnPattern[b] = (i+b)% playerSize;
            System.out.println((b+1)+ ". Player#" + playerTurnPattern[b]);
        }
    }

    /*///////////////////////////////////////////////////////////////////////
    Method serves to setup the BoardManager. Method runs while the boardmanager
    has an unoccupied territory and allows the users to select a territory
    per turn.
     *//////////////////////////////////////////////////////////////////////*/
    private static void initializeTerritories(BoardManager bm){
        while(!bm.isAllTerritoriesInitialized()) {
            for (int i : playerTurnPattern) {
                bm.displayUntakenTerritories();                                                                         //BoardManager's displayUntakenTerritories to display untaken territories
                playerList[i].displayPlayerTerritories(bm);                                                             //player's displayPlayerTerritories to display player's territories
                bm.setInitialTerritory(playerList[i]);                                                                  //BoardManager queries user for which territory to occupy
            }
        }
    }

    /*///////////////////////////////////////////////////////////////////////
    Method checks if all players have finished setting up (dispatching armies)
    to their territories, by checking if all users have no remaining unassigned
    infantry pieces
     *//////////////////////////////////////////////////////////////////////*/
    public static boolean arePlayersReady()
    {
        boolean allReady = true;
        for( Player k : playerList){
            allReady = allReady && k.isBaseEmpty();
        }
        return allReady;
    }

    /*///////////////////////////////////////////////////////////////////////
    Method prompts user to assign all of their armies to a territory per turn

    While players have not finished placing all their infantry pieces
    loop through turn-pattern, for each player, who still has unused infantry pieces,
    select a territory and update relevant information

    Refactor.
     *//////////////////////////////////////////////////////////////////////*/
    private static void shipAllArmies(BoardManager bm){
        boolean invalidTerritory;
        while(!arePlayersReady()){
            for ( int i: playerTurnPattern) {
                invalidTerritory = true;

                if (!playerList[i].isBaseEmpty()) {
                    playerList[i].displayPlayerTerritories(bm);

                    System.out.println("Remaining armies: " + playerList[i].getRemainingArmies());
                    System.out.print("Select a territory to ship your Army to: ");
                    Scanner territoryScanner = new Scanner(System.in);

                    while(invalidTerritory) {
                        try {
                            // prompts user for a territory
                            String territory = territoryScanner.nextLine();
                            if (!playerList[i].ifPlayerHasTerritory(territory))
                                throw new Exception("Error: " + territory + " is not your territory");
                            invalidTerritory = false;

                            // transfer infantry to territory
                            bm.addOccupantsTo(territory, 1, "INFANTRY");
                            playerList[i].shipArmy();

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method checks if the game is over, by passing the boardmanager
    returns true if a player has all the territories
    *///////////////////////////////////////////////////////////////////////////////*/
    private static boolean isGameOver(BoardManager bm){
        for(Player i: playerList){
            if (i.isPlayerTheWinner(bm)){
                return true;
            }
        }
        return false;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method below executes the third action a player can do in their turn.
    Method calls fortify, showTerritories functions for player
    Method requires index or Player Id as an argument, BoardManager passed

    Refactor. Add Exception to transfer count.
    *///////////////////////////////////////////////////////////////////////////////*/
    private static void fortifyPlayersTerritory(BoardManager bm, int id)
    {
        Scanner scan = new Scanner(System.in);
        System.out.println("------------------------");
        System.out.println("Fortify your territories");
        System.out.println("by moving armies from one");
        System.out.println("territory to another. ");
        System.out.println("------------------------");
        boolean invalid = false;
        do {
            try {
                playerList[id].displayPlayerTerritories(bm);

                System.out.print("\nMoveFrom: ");
                String origin = scan.nextLine();
                System.out.println();
                if(playerList[id].ifPlayerHasTerritory(origin)){
                    if (bm.getOccupantCount(origin) <= 1) {
                        throw new Exception("Uh Oh! This territory only has one army. You cannot transfer the defending army of a territory.");
                    }
                    System.out.println("Neighboring Territories: ");
                    playerList[id].displayPlayerNeighboringTerritories(bm, origin);
                    System.out.println("Army Count: " + bm.getOccupantCount(origin));
                } else
                    throw new Exception("Player does not own territory " + origin);


                int army_count = queryTransferCount();
                if(bm.getOccupantCount(origin) > (army_count))
                {
                    System.out.print("\nMoveTo: ");
                    String destination = scan.nextLine();
                    System.out.println();
                    if(playerList[id].ifPlayerHasTerritory(destination)){
                        if(!bm.isTerritoryANeighborOf(destination, origin)) {
                            throw new Exception("Uh Oh! The given destination territory is not adjacent to the given origin " + origin);
                        }
                        playerList[id].fortifyTerritory(bm, origin, destination, army_count);
                    } else
                        throw new Exception("Player does not own territory " + destination);

                }else
                    throw new Exception("Uh Oh! This territory does not have the given amount of armies to transfer");

                invalid = false;
            } catch (InputMismatchException k){
                System.out.println("Error: Invalid input.");
                invalid = true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                invalid = true;
            }
        }while(invalid);
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Helper method for querying player for number of army to transfer
    *///////////////////////////////////////////////////////////////////////////////*/
    private static int queryTransferCount(){
        Scanner intScanner = new Scanner(System.in);
        System.out.print("\nTransfer army: ");
        int army_count = intScanner.nextInt();
        System.out.println();
        return army_count;
    }
}
