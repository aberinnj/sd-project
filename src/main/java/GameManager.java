public class GameManager{

    private static int playerSize;                                                                                      // size of players playing
    private static int playerTurnPattern[];                                                                             // list of turns to loop through
    private static Player[] playerList;                                                                                 // list of players
    private static BoardManager bm;

    GameManager(){
        playerSize = 0;
        playerTurnPattern = null;
        playerList = null;
        bm = null;
    }

    GameManager(int count, String base, int[] pattern){
        playerSize = count;
        playerTurnPattern = pattern.clone();
        playerList = new Player[count];
        bm = new BoardManager(
                base + "\\src\\main\\java\\mapSource.json",
                base + "\\src\\main\\java\\deck.json"
        );

    }

    protected void initPlayers(int armySize){
        for(int a=0; a<playerSize; a++){
            playerList[a] = new Player(a, armySize);
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method for setting up list of players playing the game, throws an exception on
    invalid player-size. Setups up playerList size with corresponding settings
    *///////////////////////////////////////////////////////////////////////////////*/
    protected int getDefaultArmySize(int size){
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
        return default_infantry;
    }

    /*///////////////////////////////////////////////////////////////////////
    Method serves to setup the BoardManager. Method runs while the boardmanager
    has an unoccupied territory and allows the users to select a territory
    per turn.
     *//////////////////////////////////////////////////////////////////////*/
    protected void initTerritories(Utilities utils){
        do{
            for (int i : playerTurnPattern) {
                bm.displayUntakenTerritories();                                                                         //BoardManager's displayUntakenTerritories to display untaken territories
                playerList[i].displayPlayerTerritories(bm);                                                             //player's displayPlayerTerritories to display player's territories
                bm.setInitialTerritory(playerList[i], setup);                                                                  //BoardManager queries user for which territory to occupy
            }
        }while(!bm.isAllTerritoriesInitialized());
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
        if ((current.territoriesCopy()).containsAll((bm.getContinentsMap("AUSTRALIA")))){
            moreArmies =+ 2;
            ownedContinents += "AUSTRALIA, ";
        }
        if ((current.territoriesCopy()).containsAll((bm.getContinentsMap("ASIA")))){
            moreArmies += 7;
            ownedContinents += "ASIA, ";
        }
        if ((current.territoriesCopy()).containsAll((bm.getContinentsMap("NORTH AMERICA")))){
            moreArmies += 5;
            ownedContinents += "NORTH AMERICA, ";
        }
        if ((current.territoriesCopy()).containsAll((bm.getContinentsMap("EUROPE")))){
            moreArmies += 5;
            ownedContinents += "EUROPE, ";
        }
        if ((current.territoriesCopy()).containsAll((bm.getContinentsMap("AFRICA")))){
            moreArmies += 3;
            ownedContinents += "AFRICA, ";
        }
        if ((current.territoriesCopy()).containsAll((bm.getContinentsMap("SOUTH AMERICA")))){
            moreArmies += 2;
            ownedContinents += "SOUTH AMERICA, ";
        }
        if(moreArmies!=0){
            System.out.println("You get " + moreArmies + "armies because you own " + ownedContinents + "Congratulations!");
        }
        return moreArmies;
    }

    private int queryForPlayerSize(){
        int size;
        while(true){
            try{
                size = super.getScanAsInt("Player Setup -- Number of Players: ");
                break;
            }catch (Exception e){
                System.out.println(e.getMessage());
                continue;
            }
        }
        return size;
    }


}


