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

        int numPlayers = ng.getNumberPlayers(globalScanner);

        Player[] playerList = ng.setupPlayerList(numPlayers);

        int[] playerTurnPattern = new int[numPlayers];
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

        BoardManager bm = ng.getBoardManager(base);

        ng.initializeTerritories(bm, globalScanner, playerTurnPattern, playerList);

        System.out.println("------------------------");
        System.out.println("Allocate the rest of your armies");
        System.out.println("------------------------");

        for (int i: playerTurnPattern) {
            playerList[i].shipArmies(bm, globalScanner);
        }

        runGame(ng, bm, numPlayers, playerList, playerTurnPattern, globalScanner);

    }

    public static void runGame(NewGame ng, BoardManager bm, int numPlayers, Player[] playerList, int[] playerTurnPattern, Scanner scanner) {
        //PlayManager PM = new PlayManager();
        MoveManager MM = new MoveManager();
        initialize(ng, bm, MM, playerList, numPlayers, -1);

        // Game Start
        while(!isGameOver(bm, playerList)){
            int turnNumber = 0;
            Player playerCurrent = null;
            for (int player: playerTurnPattern)
            {
                playerCurrent = playerList[player];
                System.out.println("Player " + player + " turn");

                Turn turn = new Turn(ng, bm, playerCurrent, scanner);

                // undo recent section
                System.out.println("\n____________________________\nUNDO actions? Yes or No");
                String commitQuestion = scanner.nextLine();

                if (commitQuestion.toLowerCase().equals("yes")) {
                    player = undo(bm, MM, playerList, player);
                }
                else {
                    Move current = MM.addToMoveManager(bm, MM, playerList, numPlayers, player);
                    MM.addMove(current);
                    //upload to S3
                    //add turn integer to the move manager to indicate which turn
                }

                turnNumber++;
            }
        }
    }

    public static void initialize(NewGame ng, BoardManager bm, MoveManager MM, Player[] playerList, int numPlayers, int playerID) {
        Move initialize = MM.addToMoveManager(bm, MM, playerList, numPlayers, -1);
        MM.addMove(initialize);
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

    public static int undo(BoardManager bm, MoveManager MM, Player[] playerList) {
        // THEN UNDO
        Move last = MM.getLastMove();
        // Set Territories of player i to previous state
        for (Player player: playerList) {
            player.setTerritories(last.playerTerritories.get(player));
        }
        // Set BoardMap Territories to previous state
        bm.setBoardMap(last.CurrentTerritoryStatus);
        return player--;
    }
}


