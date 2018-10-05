import java.util.*;


public class GameManager {

    public static String base = System.getProperty("user.dir");                                                         // sets the base
    private static int playerSize;                                                                                      // size of players playing
    private static Player[] playerList;                                                                                 // list of players
    private static int[] playerTurnPattern;                                                                             // list of turns to loop through

    private static PlayManager PM;
    private static MoveManager MM;
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
                playerSize = Integer.parseInt(setup.nextLine());
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
        initializeTerritories(bm, setup);
        System.out.println("------------------------");
        System.out.println("Allocate the rest of your armies");
        System.out.println("------------------------");
        for (int i: playerTurnPattern) {
            //shipAllArmies(bm, setup);
            playerList[i].shipArmies(bm, setup);
        }

        PM = new PlayManager();
        MM = new MoveManager();
        addToMoveManager(bm, playerList, playerSize, -1);
        boolean undolooper = true;
        // Game Start
        while(!isGameOver(bm)){

            for (int i: playerTurnPattern)
            {
                undolooper = true;
                do {
                    System.out.println("Player " + i + " turn");
                    // 1. place new Armies
                    //playerList[i].addArmies(bm, setup);
                    // 2. attacking
                    //playerList[i].attack(bm, setup);
                    // 3. fortifying position
                    fortifyPlayersTerritory(bm, i);

                    // undo recent section
                    System.out.println("\n____________________________\nUNDO actions? Yes or No");
                    String commitQuestion = setup.nextLine();

                    if (commitQuestion.toLowerCase().equals("yes")) {
                        // THEN UNDO
                        Move last = MM.getLastMove();
                        // Set Territories of player i to previous state
                        playerList[i].setTerritories(last.playerTerritories.get(i));
                        // Set BoardMap Territories to previous state
                        bm.setBoardMap(last.CurrentTerritoryStatus);
                    } else {
                        undolooper = false;
                        addToMoveManager(bm, playerList, playerSize, i);
                        Move last = MM.getLastMove();
                        PM.createFileForMove(last, base);
                    }
                }while(undolooper);

            }
        }

    }

    private static void addToMoveManager(BoardManager bm, Player[] list, int size, int playerID){
        HashMap<String, Territory> moveMap = new HashMap<String, Territory>();
        HashMap<String, Territory> boardMap = bm.getBoardMap();
        HashMap<Integer, List<String>> playerTerritories = new HashMap<Integer, List<String>>();
        for(String key: boardMap.keySet())
        {
            List<String> neighbors = boardMap.get(key).getNeighbors();
            int occ = boardMap.get(key).getOccupantID();
            boolean isOcc = boardMap.get(key).isOccupied();
            int count = boardMap.get(key).getArmy().getInfantryCount();
            moveMap.put(key, new Territory(isOcc, occ, new Army(count), neighbors));

        }
        for(int i=0; i<size; i++)
        {
            List<String> territoryList = new ArrayList<String>(list[i].getTerritories());
            playerTerritories.put(i, territoryList);
        }
        MM.addMove(new Move(playerID, moveMap, playerTerritories));
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
        playerTurnPattern = new int[playerSize]; //Is this array necessary?
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
    private static void initializeTerritories(BoardManager bm, Scanner setup){
        while(!bm.isAllTerritoriesInitialized()) {
            // FIX ERROR WHERE MORE THAN TWO PLAYERS GETS STUCK IN LOOP
            for (int i : playerTurnPattern) {
                bm.displayUntakenTerritories();                                                                         //BoardManager's displayUntakenTerritories to display untaken territories
                playerList[i].displayPlayerTerritories(bm);                                                             //player's displayPlayerTerritories to display player's territories
                bm.setInitialTerritory(playerList[i], setup);                                                                  //BoardManager queries user for which territory to occupy
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

    /*////////////////////////////////////////////////////////////////////////////////
    Method checks if the game is over, by passing the boardmanager
    returns true if a player has all the territories
    *///////////////////////////////////////////////////////////////////////////////*/
    private static boolean isGameOver(BoardManager bm){
        for(Player i: playerList){
            if (i.isPlayerTheWinner(bm)){
                System.out.println("Someone won!?");
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
                    playerList[id].displayPlayerNeighboringTerritories(bm, origin, false);
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

    /*////////////////////////////////////////////////////////////////////////////////
   Method checks the number of continents a player owns and designates more armies
   *///////////////////////////////////////////////////////////////////////////////*/
    private static int continentsOwned(Player current, BoardManager bm){
        int moreArmies= 0;
        String ownedContinents = " ";
        if (current.numOfTerritories()<4)
            return 0;
        if ((current.getTerritories()).containsAll((bm.getContinentsMap("AUSTRALIA")))){
            moreArmies =+ 2;
            ownedContinents += "AUSTRALIA, ";
        }
        if ((current.getTerritories()).containsAll((bm.getContinentsMap("ASIA")))){
            moreArmies += 7;
            ownedContinents += "ASIA, ";
        }
        if ((current.getTerritories()).containsAll((bm.getContinentsMap("NORTH AMERICA")))){
            moreArmies += 5;
            ownedContinents += "NORTH AMERICA, ";
        }
        if ((current.getTerritories()).containsAll((bm.getContinentsMap("EUROPE")))){
            moreArmies += 5;
            ownedContinents += "EUROPE, ";
        }
        if ((current.getTerritories()).containsAll((bm.getContinentsMap("AFRICA")))){
            moreArmies += 3;
            ownedContinents += "AFRICA, ";
        }
        if ((current.getTerritories()).containsAll((bm.getContinentsMap("SOUTH AMERICA")))){
            moreArmies += 2;
            ownedContinents += "SOUTH AMERICA, ";
        }
        if(moreArmies!=0){
            System.out.println("You get " + moreArmies + "armies because you own " + ownedContinents + "Congratulations!");
        }
        return moreArmies;
    }


}
