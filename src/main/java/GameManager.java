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

/*///////////////////////////////////////////////////////////////////////
GameManager sets up game

On a high-level, this class sets up the game by Initializing Players and the Board
as well as the Deck


todo: make changes to current_turn, UPDATE JH.JSON_writer
*//////////////////////////////////////////////////////////////////////*/

class Props{
    String configPath;
    Properties props = new Properties();

    Props() throws IOException
    {
        this.configPath = System.getProperty("user.dir") +  "/secrets_TeamOne.prop";
        this.props.load(new FileInputStream(configPath));
    }

    public String getTwitter_apiKey() { return props.getProperty("twitter_apiKey"); }

    public String getTwitter_accessToken() { return props.getProperty("twitter_accessToken"); }

    public String getTwitter_apiSecretKey() { return props.getProperty("twitter_apiSecretKey"); }

    public String getTwitter_accessTokenSecret() { return props.getProperty("twitter_accessTokenSecret"); }

    public String getAws_access_key_id() { return props.getProperty("aws_access_key_id"); }

    public String getAws_secret_access_key() { return props.getProperty("aws_secret_access_key"); }

    public String getBot_name() { return props.getProperty("bot_name");}

    public String getBot_apiToken() { return props.getProperty("bot_token");}

}

public class GameManager {
    static String base;
    Game game;
    static Player[] playerList;
    int[] playerTurnPattern;
    static TurnManager TM;
    private static BoardManager BM;
    private String bucketName = "risk-game-team-one";
    protected static String fileObjKeyName;
    int current_turn;
    TwitterFactory tf;
    Twitter twitter;
    AmazonS3 s3Client;
    private String fileName;
    Messenger messenger;


    void setGame(Game thisGame)
    {
        game = thisGame;
    }

    // Sets up ALL Game Variables, which must be testable upon initialization
    GameManager() {
        BM = new BoardManager();
        TM = new TurnManager();
        base = System.getProperty("user.dir");
        this.fileName = base + "/src/files/Risk.json";
        current_turn = 0;


        ConfigurationBuilder cb = new ConfigurationBuilder();
        try {
            Props props = new Props();

            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(props.getTwitter_apiKey())
                    .setOAuthConsumerSecret(props.getTwitter_apiSecretKey())
                    .setOAuthAccessToken(props.getTwitter_accessToken())
                    .setOAuthAccessTokenSecret(props.getTwitter_accessTokenSecret());

            // s3Client = AmazonS3ClientBuilder.defaultClient();
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(props.getAws_access_key_id(), props.getAws_secret_access_key());

            s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion("us-east-1")
                    //.withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .build();


        } catch (IOException e){
            System.out.println("Error: No api-keys found");
        }

        // twitter setup
        tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
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

        JSONhandler JH = new JSONhandler(BM, playerList, GM.playerTurnPattern, GM.base);
        //  initialize(JH, ng, bm, MM, playerDirectory, numPlayers, -1);
        JH.JSONinitializer(0);


        TM.init(GM, playerList.length);


        while(!GM.isGameOver()){

            for (int id: GM.playerTurnPattern) {
                //System.out.println("Player " + id + " turn: " + GM.current_turn);
                messenger.putMessage("Player " + id + " turn: " + GM.current_turn);
                if (GM.gameTimeout(30)!=null) {
                    TM.save(makeTurn(GM, messenger, playerList[id], GM.current_turn, thisGame));
                }

                System.out.println("Player " + id + " turn: " + GM.current_turn);

                GM.incrementTurn();
                JH.JSONwriter(GM.current_turn);

                if (GM.baseQuery("Would you like to save this game?")) {
                    upload();
                }

            }
        }
    }

    //Function to upload saved game file to S3 bucket
    public void upload() throws InterruptedException {

        try {

            if (!s3Client.doesBucketExistV2(bucketName)) {
                // Because the CreateBucketRequest object doesn't specify a region, the
                // bucket is created in the region specified in the client.
                s3Client.createBucket(new CreateBucketRequest(bucketName));

                // Verify that the bucket was created by retrieving it and checking its location.
                //String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));
                //System.out.println("Bucket location: " + bucketLocation);
            }
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it and returned an error response.
            // e.printStackTrace();
            messenger.putMessage(String.valueOf(e));
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            // e.printStackTrace();
            messenger.putMessage(String.valueOf(e));
        }

        try {

            // Upload a text string as a new object.
            //s3Client.putObject(bucketName, stringObjKeyName, "Uploaded String Object");

            // Upload a file as a new object with ContentType and title specified.
            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, new File(fileName));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("Risk_Json_SavedGames", "RiskJSON");
            request.setMetadata(metadata);
            s3Client.putObject(request);
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            // e.printStackTrace();
            messenger.putMessage(String.valueOf(e));
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            // e.printStackTrace();
            messenger.putMessage(String.valueOf(e));
        }
    }

    //Function to download saved game file to S3 bucket
    public void download() throws IOException, InterruptedException {

        S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;
        try {
            s3Client.getObject(
                    new GetObjectRequest(bucketName, fileObjKeyName),
                    new File(fileName)
            );

            // Get an object
            fullObject = s3Client.getObject(new GetObjectRequest(bucketName, fileObjKeyName));
            S3ObjectInputStream stream = fullObject.getObjectContent();
            File targetFile = new File(fileName);
            FileUtils.copyInputStreamToFile(stream, targetFile);
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            // e.printStackTrace();
            messenger.putMessage(String.valueOf(e));
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            // e.printStackTrace();
            messenger.putMessage(String.valueOf(e));
        }
        finally {
            // To ensure that the network connection doesn't remain open, close any open input streams.
            if(fullObject != null) {
                fullObject.close();
            }
            if(objectPortion != null) {
                objectPortion.close();
            }
            if(headerOverrideObject != null) {
                headerOverrideObject.close();
            }
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////
    setGame is used to re-set all game variables from a turn, after initialization (like undo)
    setGame can an also be used for loading a game IF loader is using init TurnManager instead to store an entire game's turn listing
    in short, Loader gives all data to TurnManager and leaves everything to TurnManager and setGame
    then in _GameStarter, after loading all game, call this function to setGame from a turn
    *///////////////////////////////////////////////////////////////////////////////*
    /*
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

    }*/

    //
    public void loadGame(int turnToLoad, Loader loader) throws IOException, InterruptedException {
        download();
        JsonObject turn = loader.LoadGame(turnToLoad, BM, GameManager.base);
        int numPlayers = loader.getNumPlayers(turn);
        loader.setPlayers(BM, numPlayers, turn);
        //BM.gameDeck = loader.setDeck(turn); <- reinstantiates the deck from the JSON, currently Deck is private in and immutable

    }

    // must be called to start GameManager
    public void initializeAsNormal(int playerCount, Game thisGame) throws InterruptedException {
        // playerList = setPlayerList(playerCount);
        setGame(thisGame);
        setPlayerList(playerCount);

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

    // Query for yes/no
    // for Queries for territories, check BoardManager's queryTerritory
    public boolean baseQuery(String query) throws InterruptedException {
        String res;
        do {
            //System.out.println(query);
            messenger.putMessage(query);
            res = messenger.getMessage();
        }while(!res.toLowerCase().equals("y") && !res.toLowerCase().equals("yes") && !res.toLowerCase().equals("n") && !res.toLowerCase().equals("no"));

        return (res.toLowerCase().equals("y") || res.toLowerCase().equals("yes"));
    }


    public Player getPlayer(int playerID) {
        return playerList[playerID];
    }

    public BoardManager getBM() {return BM;}

    // telegram style, gives each players the appropriate number of armies on init
    public void setPlayerList(int size){
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
            playerList[a].addArmies(default_infantry);
        }
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

    // make a turn
    public static Turn makeTurn(GameManager GM, Messenger messenger, Player p, int id, Game thisGame) throws InterruptedException {
        Turn newTurn;
        do {
            newTurn = new Turn(BM, p, id);
            newTurn.turnFunction(GM, messenger, thisGame);

            // find a way to display turn changes
            if(GM.baseQuery("Would you like to undo all actions for this turn? This will deduct one undo action."))
            {
                // if (p.getUndos() < 1) { System.out.println("You can not perform an undo action now"); break;}
                if (p.getUndos() < 1) { messenger.putMessage("You can not perform an undo action now"); break;}
                p.addUndos(-1);
                /*
                GM.setGame(
                        TM.getTurnList().get(id-1),
                        TM.getTurnList().get(id-GM.playerTurnPattern.length)
                );*/
            } else
                break;
        } while(true);
        // post status to Twitter, differentiate newTurn.player.territories and newTurn.previousTerritories
        try {
            GM.broadcastToTwitter(newTurn, p);
        } catch (TwitterException e)
        {
            e.printStackTrace();
        }
        return newTurn;
    }

    public void broadcastToTwitter(Turn k, Player p) throws TwitterException
    {
        int gains = 0;
        String result = "Turn("+ k.turnId + "):Player " + p.getId() + " captured ";
        for(String terr: p.getTerritories())
        {
            if (!k.previousTerritories.contains(terr)) {
                gains++;
            }
        }
        result += gains;
        if(gains == 1)
            result += " territory.";
        else
            result += " territories.";

        System.out.println("\nTurn Summary: ");
        if (gains > 0) {
            Status status = twitter.updateStatus(result);
            System.out.println(status.getText());
        } else
            System.out.println(result + "no territories this turn.");
    }

    // Create list of free territories for creating keyboard during game initialization
    public List availableTerritories(Game thisGame) {
        return BM.getFreeTerritories();
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


