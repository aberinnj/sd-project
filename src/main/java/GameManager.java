import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.*;


public class GameManager {


    public static void main(String[] args) throws Exception {
        NewGame ng = new NewGame();

        // Game Setup
        System.out.println("Game of Risk");
        System.out.println("------------------------");
        System.out.println("PlayerSetup");
        System.out.println("------------------------");
        Scanner globalScanner = ng.getScanner();

        String base = ng.getBase();
        BoardManager bm = ng.getBoardManager(base);

        int turnNumber;

        System.out.println("Would you like to load a saved game?");
        String answer = globalScanner.nextLine();
        int numPlayers = 0;
        Player[] playerList;
        int[] playerTurnPattern;

        if (answer.equalsIgnoreCase("Yes")) {

            System.out.println("Which turn would you like to load from?");
            System.out.println("Enter -1 to begin from the beginning");
            turnNumber = Integer.parseInt(globalScanner.nextLine());

            LoadGame LG = new LoadGame();
            JsonObject loadedTurn = LG.LoadGame(turnNumber, bm);
            numPlayers = LG.getNumPlayers(loadedTurn);

            playerList = LG.setPlayers(bm, numPlayers, loadedTurn);

            playerTurnPattern = new int[numPlayers];

        }


        else {

            numPlayers = ng.getNumberPlayers(globalScanner);

            playerList = ng.setupPlayerList(numPlayers);

            playerTurnPattern = new int[numPlayers];
            Dice die = new Dice();

            System.out.println("-----------------------");
            System.out.println("BoardSetup");
            System.out.println("Note: Whoever gets the first of the same highest");
            System.out.println("number, is considered to have the highest number");
            System.out.println("------------------------");

            int highestDieValue = ng.rollForSetup(die, numPlayers);

            System.out.println("\nOrder of Turns:");
            for(int b=0; b<numPlayers; b++)
            {
                playerTurnPattern[b] = (highestDieValue+b)% numPlayers;
                System.out.println((b+1)+ ". Player#" + playerTurnPattern[b]);
            }

            ng.initializeTerritories(bm, globalScanner, playerTurnPattern, playerList);

            System.out.println("------------------------");
            System.out.println("Allocate the rest of your armies");
            System.out.println("------------------------");

            shipArmies(playerTurnPattern, playerList, bm, globalScanner);

            turnNumber = 0;
        }

        runGame(ng, bm, numPlayers, playerList, playerTurnPattern, globalScanner, base, turnNumber);

    }

    public static void shipArmies(int[] playerTurnPattern, Player[] playerList, BoardManager bm, Scanner globalScanner) {
        for (int i: playerTurnPattern) {
            playerList[i].shipArmies(bm, globalScanner);
        }
    }

    public static void runGame(NewGame ng, BoardManager bm, int numPlayers, Player[] playerList, int[] playerTurnPattern, Scanner scanner, String base, int turns) throws Exception {
        MoveManager MM = new MoveManager(base);
        JSONhandler JH = new JSONhandler(bm, playerList, playerTurnPattern);
        initialize(JH, ng, bm, MM, playerList, numPlayers, -1);

        // Game Start
        while(!isGameOver(bm, playerList)){
            int turnNumber = turns;
            Player playerCurrent = null;
            for (int player: playerTurnPattern)
            {
                playerCurrent = playerList[player];
                System.out.println("Player " + player + " turn");

                Turn turn = new Turn(MM, ng, bm, playerCurrent, playerList, scanner);
                JH.JSONwriter(turnNumber);
                turnNumber++;

                System.out.println("Save Game?");
                String ans = scanner.nextLine().toLowerCase();
                if (ans == "yes") {
                    JH.upload();
                }
            }
        }
    }

    public static void initialize(JSONhandler JH, NewGame ng, BoardManager bm, MoveManager MM, Player[] playerList, int numPlayers, int playerID) throws IOException {
        Move initialize = MM.addToMoveManager(bm, MM, playerList, numPlayers, -1);
        MM.addMove(initialize);
        JH.JSONinitializer(-1);
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method checks if the game is over, by passing the boardmanager
    returns true if a player has all the territories

    Also removes player from playerList if player has no territories
    *///////////////////////////////////////////////////////////////////////////////*/
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


