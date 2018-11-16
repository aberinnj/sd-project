import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

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

    public String getBot_name() { return props.getProperty("bot_name");}

    public String getBot_apiToken() { return props.getProperty("bot_token");}

}

public class GameManager {
    static String base;

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

    // Sets up ALL Game Variables, which must be testable upon initialization
    GameManager() {
        BM = new BoardManager();
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

            s3Client = AmazonS3ClientBuilder.defaultClient();


        } catch (IOException e){
            System.out.println("Error: No api-keys found");
        }

        // twitter setup
        tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    }

    public void runGame(GameManager GM, Scanner scanner) throws IOException {
        JSONhandler JH = new JSONhandler(BM, playerList, GM.playerTurnPattern, GM.base);
        //  initialize(JH, ng, bm, MM, playerList, numPlayers, -1);
        JH.JSONinitializer(0);


        TM.init(GM, playerList.length);


        while(!GM.isGameOver()){

            for (int id: GM.playerTurnPattern) {
                System.out.println("Player " + id + " turn: " + GM.current_turn);
                TM.save(makeTurn(GM, scanner, playerList[id], GM.current_turn));

                GM.incrementTurn();
                JH.JSONwriter(GM.current_turn);

                if (GM.baseQuery("Would you like to save this game?", scanner)) {
                    upload();
                }

            }
        }
    }

    //Function to upload saved game file to S3 bucket
    public void upload() {

        try {

            if (!s3Client.doesBucketExistV2(bucketName)) {
                // Because the CreateBucketRequest object doesn't specify a region, the
                // bucket is created in the region specified in the client.
                s3Client.createBucket(new CreateBucketRequest(bucketName));

                // Verify that the bucket was created by retrieving it and checking its location.
                String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));
                System.out.println("Bucket location: " + bucketLocation);
            }
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it and returned an error response.
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }

        //ObjectMapper objectMapper = new ObjectMapper();
        //byte[] bytesToWrite = objectMapper.writeValueAsBytes(json);

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
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }

    //Function to download saved game file to S3 bucket
    public void download() throws IOException {


        S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;
        try {

            s3Client.getObject(
                    new GetObjectRequest(bucketName, fileObjKeyName),
                    new File(fileName)
            );

            // Get an object and print its contents.
            System.out.println("Downloading an object");
            fullObject = s3Client.getObject(new GetObjectRequest(bucketName, fileObjKeyName));
            System.out.println("Content-Type: " + fullObject.getObjectMetadata().getContentType());
            System.out.println("Content: ");
            displayTextInputStream(fullObject.getObjectContent());

            // Get a range of bytes from an object and print the bytes.
            GetObjectRequest rangeObjectRequest = new GetObjectRequest(bucketName, fileObjKeyName)
                    .withRange(0,9);
            objectPortion = s3Client.getObject(rangeObjectRequest);
            System.out.println("Printing bytes retrieved.");
            displayTextInputStream(objectPortion.getObjectContent());

            // Get an entire object, overriding the specified response headers, and print the object's content.
            ResponseHeaderOverrides headerOverrides = new ResponseHeaderOverrides()
                    .withCacheControl("No-cache")
                    .withContentDisposition("attachment; filename=example.txt");
            GetObjectRequest getObjectRequestHeaderOverride = new GetObjectRequest(bucketName, fileObjKeyName)
                    .withResponseHeaders(headerOverrides);
            headerOverrideObject = s3Client.getObject(getObjectRequestHeaderOverride);
            displayTextInputStream(headerOverrideObject.getObjectContent());
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
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

    private void displayTextInputStream(InputStream input) throws IOException {
        // Read the text input stream one line at a time and display each line.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println();
    }

    /*////////////////////////////////////////////////////////////////////////////////
    setGame is used to re-set all game variables from a turn, after initialization (like undo)
    setGame can an also be used for loading a game IF loader is using init TurnManager instead to store an entire game's turn listing
    in short, Loader gives all data to TurnManager and leaves everything to TurnManager and setGame
    then in _GameStarter, after loading all game, call this function to setGame from a turn
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

    }

    //
    public void loadGame(int turnToLoad, Loader loader) throws IOException {
        download();
        JsonObject turn = loader.LoadGame(turnToLoad, BM, GameManager.base);
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

    // make a turn
    public static Turn makeTurn(GameManager GM, Scanner scanner, Player p, int id) {
        Turn newTurn;
        do {
            newTurn = new Turn(BM, p, id);
            newTurn.turnFunction(GM, scanner);

            // find a way to display turn changes
            if(GM.baseQuery("Would you like to undo all actions for this turn? This will deduct one undo action.", scanner))
            {
                if (p.getUndos() < 1) { System.out.println("You can not perform an undo action now"); break;}
                p.addUndos(-1);
                GM.setGame(
                        TM.getTurnList().get(id-1),
                        TM.getTurnList().get(id-GM.playerTurnPattern.length)
                );
            } else
                break;
        } while(true);
        // post status to Twitter, differentiate newTurn.player.territories and newTurn.previousTerritories
        try {
            GM.broadcastToTwitter(newTurn, p);
        } catch (TwitterException e)
        {
            System.out.println(e.getMessage());
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
        System.out.println();
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

    public void incrementTurn(){
        current_turn++;
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


