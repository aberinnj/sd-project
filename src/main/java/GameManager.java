import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;

import java.io.*;
import java.util.*;

public class GameManager {
    Game game;
    static Player[] playerList;
    int[] playerTurnPattern;
    static TurnManager TM;
    private static BoardManager BM;
    int current_turn;


    Messenger messenger;


    void setGame(Game thisGame)
    {
        game = thisGame;
    }

    // Sets up ALL Game Variables, which must be testable upon initialization
    GameManager() {
        BM = new BoardManager();
        //TM = new TurnManager();

        current_turn = 0;
    }

        public String gameTimeout(int timeout) {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        String input = null;

        try{
            Future<String> result = ex.submit(new ConsoleInputReader());
            try {
                input = result.get(timeout,TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                e.getCause().printStackTrace();
            } catch (TimeoutException e) {
                System.out.println("Cancelling reading task");
                result.cancel(true);
                System.out.println("\nThread cancelled. input is null because you did not take action");
                System.out.println("\nMOVING TO NEXT PLAYER");
            }
            catch(InterruptedException e)
            {
                // this part is executed when an exception (in this example InterruptedException) occurs
            }
        } finally {
            ex.shutdownNow();
        }
        return input;
    }


    public void runGame(GameManager GM, Messenger messenger, Game thisGame) throws IOException, InterruptedException {

        this.messenger = messenger;

        //TM.init(GM, playerList.length);


        while(!GM.isGameOver()){

            for (int id: GM.playerTurnPattern) {
                //System.out.println("Player " + id + " turn: " + GM.current_turn);
                messenger.putMessage("Player " + id + " turn: " + GM.current_turn);
                if (GM.gameTimeout(30)!=null) {
                   // TM.save(makeTurn(GM, messenger, playerList[id], GM.current_turn, thisGame));
                }

                System.out.println("Player " + id + " turn: " + GM.current_turn);

                GM.incrementTurn();
                //JH.JSONwriter(GM.current_turn);

                //if (GM.baseQuery("Would you like to save this game?")) {
                //    upload();
                //}

            }
        }
    }

    // must be called to start GameManager
    public void initializeAsNormal(int playerCount, Game thisGame) throws InterruptedException {
        // playerList = setPlayerList(playerCount);
        setGame(thisGame);
        //setPlayerList(playerCount);

        System.out.println("Initialize as Normal");

        // System.out.println("\n__Order of Turns:__");
        thisGame.messenger.putMessage("Dice automatically rolled. Order of turns");
        Dice k = new Dice();
        ArrayList<Integer> diceArr = new ArrayList<Integer>();
        for (int i = 0; i < playerCount; i++) {
            k.roll();
            diceArr.add(k.getDiceValue());
        }
        playerTurnPattern = getTurnPattern(playerCount, getIndexOfHighestRollIn(diceArr, playerCount), thisGame);

        System.out.print("Decided pattern: " );
        for(int i: playerTurnPattern)
        {
            System.out.print(i + " ");
        }
        System.out.println();
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
    public int[] getTurnPattern(int size, int highest, Game thisGame) throws InterruptedException {
        int[] array = new int[size];

        for (int i = 0; i < size; i++) {
            int roll = (highest + i) % size;
            array[i] = roll;
            //System.out.println((i+1)+ ". Player#" + array[i]);

            // use turnPattern to hold the order of player turns
            thisGame.setTurnPattern(i, roll);

            //messenger.putMessage((i+1)+ ". Player @" + thisGame.playerDirectory.get(players.get(i)).username);
        }
        return array;
    }

    public static void strengthenTerritory(Game thisGame) {
        //System.out.println("__STRENGTHEN TERRITORIES__");
        thisGame.messenger.putMessage("__STRENGTHEN TERRITORIES__");
        for (int id: thisGame.game.GM.playerTurnPattern) {
            thisGame.game.GM.strengthenTerritories(id, thisGame);
        }
    }


    public void strengthenTerritories(int id, Game thisGame) {
        String territory;

            while (playerList[id].getNumberOfArmies() > 0) {

                // System.out.println("Infantry Count And Player #"+ id +" Territories");
                messenger.putMessage("Infantry Count And Player #"+ id +" Territories");

                for (String country: playerList[id].getTerritories()) {
                    //System.out.println(BM.getOccupantCount(country) + " " + country);}
                    messenger.putMessage(BM.getOccupantCount(country) + " " + country);}

                //System.out.println("Remaining armies: " + playerDirectory[id].getNumberOfArmies());
                //System.out.println("Select a territory to ship your Army to: ");
                messenger.putMessage("Remaining armies: " + playerList[id].getNumberOfArmies());
                messenger.putMessage("Select a territory to ship your Army to: ");

                do{
                    territory = BM.queryTerritory("Player #" + id + " -- Territory select: ",
                            "STRENGTHEN", playerList[id], "", thisGame);
                } while(territory == null);

                BM.strengthenTerritory(playerList[id], territory, 1);
            }
    }

    public void incrementTurn(){
        current_turn++;
    }

    // Player list only contains Players, and you can freely check if players have all the territories
    public boolean isGameOver() throws InterruptedException {
        for(Player i: playerList){
            if (i.isPlayerTheWinner(BM)){
                // System.out.println("Someone won!?");
                messenger.putMessage("Someone won!?");
                return true;
            }
        }
        return false;
    }
}


