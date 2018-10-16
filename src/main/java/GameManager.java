import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.*;


/*///////////////////////////////////////////////////////////////////////
GameManager sets up game

On a high-level, this class sets up the game by Initializing Players and the Board
as well as the Deck


todo: make changes to current_turn, UPDATE JH.JSON_writer
*//////////////////////////////////////////////////////////////////////*/
public class GameManager {
    String base;
    static Player[] playerList;
    int[] playerTurnPattern;
    static TurnManager TM;
    private static BoardManager BM;
    int current_turn;

    // Sets up ALL Game Variables, which must be testable upon initialization
    GameManager() {
        BM = new BoardManager();
        TM = new TurnManager();
<<<<<<< HEAD
        this.base = System.getProperty("user.dir");
=======
        current_turn = 0;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    setGame is used to re-set all game variables from a turn, after initialization (like undo)
    setGame can an also be used for loading a game IF loader is using init TurnManager instead to store an entire game's turn listing
    in short, Loader gives all data to TurnManager and leaves everything to TurnManager and setGame
    then in _Starter, after loading all game, call this function to setGame from a turn
    *///////////////////////////////////////////////////////////////////////////////*/
    public void setGame(final Turn lastTurnOfPlayerBefore, final Turn lastTurnOfThisPlayer)
    {
        // playerTurnPattern = Loader.getPlayerTurnPattern -- unused by undo
        BM = new BoardManager(
                TM.copy(lastTurnOfPlayerBefore.BM.getBoardMap()),
                TM.copy(lastTurnOfPlayerBefore.BM.getGameDeck()),
                lastTurnOfPlayerBefore.BM.completeSets);

        playerList[lastTurnOfThisPlayer.player.getId()] = new Player(lastTurnOfThisPlayer.player.getId(),
                lastTurnOfThisPlayer.player.getNumberOfArmies(),
                new ArrayList<Card>(){{addAll(lastTurnOfThisPlayer.player.getHandListing());}},
                new ArrayList<String>(){{addAll(lastTurnOfThisPlayer.player.getTerritories());}});

>>>>>>> 4dacf0dedc3e9b6d8df5bbf27c2e5a9913969243
    }

    //
    public void loadGame(int turnToLoad, Loader loader) throws IOException {
        JsonObject turn = loader.LoadGame(turnToLoad, BM);
        int numPlayers = loader.getNumPlayers(turn);
        loader.setPlayers(BM, numPlayers, turn);
        //BM.gameDeck = loader.setDeck(turn); <- reinstantiates the deck from the JSON, currently Deck is private in and immutable


    }

    // must be called to start GameManager
    public void initializeAsNormal(int playerCount)
    {
        playerList = setPlayerList(playerCount);

        System.out.println("\n__Order of Turns:__");
        Dice k = new Dice();
        ArrayList<Integer> diceArr = new ArrayList<Integer>();
        for (int i = 0; i < playerCount; i++) {
            k.roll();
            diceArr.add(k.getDiceValue());
        }
        playerTurnPattern = getTurnPattern(playerCount, getIndexOfHighestRollIn(diceArr, playerCount));
    }

    // Query for yes/no
    // for Queries for territories, check BoardManager's queryTerritory
    public boolean baseQuery(String query, Scanner scanner) {
        String res;
        do {
            System.out.println(query);
            res = scanner.nextLine();
        }while(!res.toLowerCase().equals("y") && !res.toLowerCase().equals("yes") && !res.toLowerCase().equals("n") && !res.toLowerCase().equals("no"));

        return (res.toLowerCase().equals("y") || res.toLowerCase().equals("yes"));
    }


    public Player getPlayer(int playerID) {
        return playerList[playerID];
    }

    public BoardManager getBM() {return BM;}

    // Initializes PlayerList Array with the right amount of Infantry
    public Player[] setPlayerList(int size){
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
    public int getIndexOfHighestRollIn(ArrayList<Integer> diceList,int iterations){
        int indexOfHighestRoll = 0;
        int valueOfHighestRoll = diceList.get(0);
        for(int i=1; i<iterations; i++)
        {
            if (diceList.get(i) > valueOfHighestRoll)
            {
                valueOfHighestRoll = diceList.get(i);
                indexOfHighestRoll = i;
            }
        }
        return indexOfHighestRoll;
    }

    // Setup UserTurnPattern and display (optional)
    public int[] getTurnPattern(int size, int highest) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = (highest + i) % size;
            System.out.println((i+1)+ ". Player#" + array[i]);
        }
        return array;
    }

    // Run setup to finish Game setup for all players
    public static void runSetup(GameManager GM, Scanner scanner) {
        System.out.println("__CLAIM TERRITORIES__");
        GM.claimTerritories(scanner);
        System.out.println("__STRENGTHEN TERRITORIES__");
        for (int id: GM.playerTurnPattern) {
            GM.strengthenTerritories(scanner, id);
        }

    }

<<<<<<< HEAD
    public void runGame(GameManager GM, Scanner scanner) throws IOException {
        JSONhandler JH = new JSONhandler(BM, playerList, playerTurnPattern, base);
=======
    public static void runGame(GameManager GM, Scanner scanner) throws IOException {
        JSONhandler JH = new JSONhandler(BM, playerList, GM.playerTurnPattern, base);
>>>>>>> 4dacf0dedc3e9b6d8df5bbf27c2e5a9913969243
        //  initialize(JH, ng, bm, MM, playerList, numPlayers, -1);
        JH.JSONinitializer(0);

        TM.init(playerList.length, GM.current_turn);

        while(!GM.isGameOver()){

            for (int id: GM.playerTurnPattern) {
                System.out.println("Player " + id + " turn: " + GM.current_turn);
                TM.save(makeTurn(GM, scanner, playerList[id], GM.current_turn));
                JH.JSONwriter(GM.current_turn);
                GM.current_turn++;
                //System.out.println("would you like to save?")
                //if yes JH.upload();
            }
        }
    }

    // make a turn
    public static Turn makeTurn(GameManager GM, Scanner scanner, Player p, int id) {
        Turn k;
        do {
            k = new Turn(BM, p, id);
            k.turnFunction(GM, scanner);
            // find a way to display turn changes
            if(GM.baseQuery("Would you like to undo all actions for this turn? ", scanner))
            {
                GM.setGame(
                        TM.getTurnList().get(id-1),
                        TM.getTurnList().get(id-GM.playerTurnPattern.length)
                );
            } else break;
        } while(true);
        return k;
    }

    // Display Free territories -- removed displayPlayerTerritories for simplicity and less console clutter
    public void claimTerritories(Scanner scanner){
        String territory;
        while(BM.getFreeTerritories().size() > 0) {
            for (int id : playerTurnPattern) {

                System.out.println("__Free Territories__");
                for(String k : BM.getFreeTerritories())
                    System.out.println(k);

                do{
                    territory = BM.queryTerritory(scanner, "Player #" + id + " -- Territory select: ",
                            "INITIALIZE", playerList[id], "");
                } while(territory == null);

                BM.initializeTerritory(playerList[id], territory, 1);

            }
        }
    }


    public void strengthenTerritories(Scanner scanner, int id) {
        String territory;

            while (playerList[id].getNumberOfArmies() > 0) {

                System.out.println("Infantry Count And Player #"+ id +" Territories");

                for (String country: playerList[id].getTerritories()) {
                    System.out.println(BM.getOccupantCount(country) + " " + country);}

                System.out.println("Remaining armies: " + playerList[id].getNumberOfArmies());
                System.out.println("Select a territory to ship your Army to: ");

                do{
                    territory = BM.queryTerritory(scanner, "Player #" + id + " -- Territory select: ",
                            "STRENGTHEN", playerList[id], "");
                } while(territory == null);

                BM.strengthenTerritory(playerList[id], territory, 1);
            }
    }

    // Player list only contains Players, and you can freely check if players have all the territories
    public boolean isGameOver(){
        for(Player i: playerList){
            if (i.isPlayerTheWinner(BM)){
                System.out.println("Someone won!?");
                return true;
            }
        }
        return false;
    }
}


