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
        setupPlayerList(playerSize);

        rollForSetup();
        BoardManager bm = new BoardManager(base + "\\src\\main\\java\\mapSource.json");
        initializeTerritories(bm);
    }

    /**/
    private static void setupPlayerList(int size) {
        playerList = new Player[size];
        int cavalry;
        /*
        * change calvary to some sort of token representation so later when we implement purchasing or trading new units we don't 
        * have to redefine anything and can base any new units on this currency that a player will already hold
        */
        switch(size){
            case 2:
                cavalry=40;
                break;
            case 3:
                cavalry=35;
                break;
            case 4:
                cavalry=30;
                break;
            case 5:
                cavalry=25;
                break;
            case 6:
                cavalry=20;
                break;
            default:
                cavalry=0;
                break;
        }

        for(int a=0; a<size; a++){
            playerList[a] = new Player(a, cavalry);
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
    private  static void initializeTerritories(BoardManager bm){
        while(!bm.isAllTerritoriesInitialized()) {
            for (int i : playerTurnPattern) {

                bm.setupInitialTerritories(playerList[i]);
            }
        }
    }
}
