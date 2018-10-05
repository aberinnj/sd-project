import java.util.Scanner;

class Utilities {
    /*////////////////////////////////////////////////////////////////////////////////
    Class handles all scan methods only for now
    *///////////////////////////////////////////////////////////////////////////////*/
    private Scanner scan;

    //
    Utilities(){
        scan = new Scanner(System.in);
    }

    //
    public String getInput(String prompt) {
        System.out.print(prompt);
        return scan.nextLine();
    }

    //
    private int getInputNumber(String prompt)throws NumberFormatException{
        return Integer.parseInt(getInput(prompt));
    }

    //
    private int getInputNumberButLimit(String prompt, int min, int max, String err) throws Exception{
        int value = getInputNumber(prompt);
        if (value < min || value > max)
            throw new Exception(err);
        else
            return value;
    }

    //
    public int getSize(String prompt) {
        int value = -1;
        do {
            try {
                value = getInputNumber(prompt);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } while (value < 0);
        return value;
    }

    //
    public int getSize(String prompt, int min, int max, String err){
        int value = 0;
        do {
            try{
                value = getInputNumberButLimit(prompt, min, max, err);
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        } while (value < min || value > max);
        return value;
    }
}

public class PlayManager extends GameManager {
    /*////////////////////////////////////////////////////////////////////////////////
    PlayManager acts as the controller for the entire game
    *///////////////////////////////////////////////////////////////////////////////*/

    //
    private static int[] getTurnPattern(int size, int highest) {
        int[] array = new int[size];

        for (int i = 0; i < size; i++) {
            array[i] = (highest + i) % size;
        }
        return array;
    }

    //
    public static void main(String[] args) {

        Utilities utils = new Utilities();
        Dice dice_one = new Dice();
        String base = System.getProperty("user.dir");
        System.out.println("Game of Risk");

        // Set Player Size
        int size = utils.getSize("Player Setup -- Number of Players: ", 2, 5, "(!) Error: This game mode only supports 2-6 players.");
        int rollForFirstPlayer = dice_one.getIndexOfHighestRollIn(size);
        int[] turn_pattern = getTurnPattern(size, rollForFirstPlayer);
        GameManager GM = new GameManager(size, base, turn_pattern);

        int armyCount = GM.getDefaultArmySize(size);
        GM.initPlayers(armyCount);
        GM.initTerritories(utils);
        GM.finishSendingArmies();
        for (int i : playerTurnPattern) {
            playerList[i].shipArmies(bm, setup);
        }

        // Game Start
        while (!isGameOver(bm)) {

            for (int i : playerTurnPattern) {
                System.out.println("Player " + i + " turn");
                // access playerList[i]
                // 1. place new Armies
                //int armies =
                //playerList[i].shipArmies(bm, setup);
                playerList[i].addArmies(bm, setup);
                // 2. attacking
                playerList[i].attack(bm, setup);
                // 3. fortifying position
                fortifyPlayersTerritory(bm, i);
            }
            break;

        }
    }
}