import java.util.Scanner;

public class GameManager {

    public static String base = System.getProperty("user.dir");

    /* Game static members */
    private static int playerSize;
    private static Player[] playerList;
    private static int[] playerTurnPattern;

    /**/
    public static void main(String[] args) {
        System.out.println("Game of Risk");

        System.out.println("------------------------\n| PlayerSetup\n------------------------");
        System.out.print("Number of Players: ");
        Scanner setup = new Scanner(System.in);
        playerSize = setup.nextInt();

        while(playerSize < 2 || playerSize > 6) {
        	System.out.println("This game mode only supports between 2 and 6 players, please enter a valid number of players");
        	playerSize = setup.nextInt();
        }
        
        setupPlayerList(playerSize);

        rollForSetup();
        BoardManager bm = new BoardManager(base + "\\src\\main\\java\\mapSource.json");
        initializeTerritories(bm);
	Deck deck = new Deck(base + "/main/java/deck.json");
        System.out.println("------------------------\n| Allocate the rest of your armies\n------------------------");
        shipAllArmies(bm);
    }

    /**/
    private static void setupPlayerList(int size) {
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

    /**/
    private static void rollForSetup(){
        playerTurnPattern = new int[playerSize];
        Dice turnSetupDice = new Dice();
        int highestID = -1;
        int highestNUM = 1;

        System.out.println("\n------------------------\n| BoardSetup\n" +
                "| Note: Whoever gets the first of the same highest\n" +
                "| number, is considered to have the highest number\n" +
                "| Fix: Refactor\n------------------------");

        for(int i=0; i<playerSize; i++)
        {
            turnSetupDice.roll();
            System.out.println("Rolling for Player#"+(i)+ "..."+turnSetupDice.getDiceValue());
            if (turnSetupDice.getDiceValue() > highestNUM)
            {
                highestNUM = turnSetupDice.getDiceValue();
                highestID = i;
            }
        }
        setupTurnPattern(highestID);

    }

    /* helper function for rollForSetup */
    private static void setupTurnPattern(int i){
        System.out.println("\nOrder of Turns:");
        for(int b=0; b<playerSize; b++)
        {
            playerTurnPattern[b] = (i+b)% playerSize;
            System.out.println((b+1)+ ". Player#" + playerTurnPattern[b]);
        }
    }

    /**/
    private static void initializeTerritories(BoardManager bm){
        while(!bm.isAllTerritoriesInitialized()) {
            for (int i : playerTurnPattern) {
                bm.seeUntakenTerritories();
                playerList[i].seeTerritories(bm);
                bm.setupInitialTerritories(playerList[i]);
            }
        }
    }

    /**/
    public static boolean arePlayersReady()
    {
        boolean allReady = true;
        for( Player k : playerList){
            allReady = allReady && k.isBaseEmpty();
        }
        return allReady;
    }

    /**/
    private static void shipAllArmies(BoardManager bm){
        boolean invalidTerritory;
        while(!arePlayersReady()){
            for( Player k: playerList){
                invalidTerritory = true;

                if (!k.isBaseEmpty()){
                    k.seeTerritories(bm);
                    System.out.println("Remaining armies: " + k.getRemainingArmies());
                    System.out.print("Select a territory to ship your Army to: ");
                    Scanner territoryScanner = new Scanner(System.in);
                    while(invalidTerritory){
                    try{
                        String territory = territoryScanner.nextLine();
                        if(!k.checkIfPlayerHasTerritory(territory))
                            throw new Exception("Error: "+ territory + " is not your territory");
                        invalidTerritory = false;

                        bm.addOccupantsTo(territory, 1, "INFANTRY");
                        k.shipArmy();

                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }

                    }
                }
            }
        }
    }


}
