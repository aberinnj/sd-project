import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.*;


/*////////////////////////////////////////////////////////////////////////////////
Loader class takes car of remote turns, makes use of JSONHandler to get results
*///////////////////////////////////////////////////////////////////////////////*/
public class Loader {

    public JsonObject LoadGame(int turnNumber, BoardManager bm) throws IOException {

        Player[] emptyPlayer = null;
        int[] emptyTurnPattern = null;

        JSONhandler JH = new JSONhandler(bm, emptyPlayer, emptyTurnPattern);
        JH.download();
        JsonObject turn = JH.getTurnJSON(turnNumber);
        return turn;
    }

    public static int getNumPlayers(JsonObject turn) {
        JsonArray players = (JsonArray) turn.get("Players");
        return players.size();
    }

    public Stack<Card> setDeck(JsonObject turn)
    {

        Stack<Card> GameDeck = new Stack<Card>();

        // get the main deck array (undrawn cards) from the json turn
        JsonArray Deck = (JsonArray) turn.get("Deck");
        Iterator<JsonElement> itr = Deck.iterator();
        while (itr.hasNext())
        {
            JsonObject tempCard = (JsonObject) itr.next();
            String territory = String.valueOf(tempCard.keySet());
            String army = String.valueOf(tempCard.get(territory));
            GameDeck.push(new Card(territory, army));
        }
        return GameDeck;
    }

    public Player[] setPlayers(BoardManager bm, int numPlayers, JsonObject turn) {

        Player[] playerList = new Player[numPlayers];

        JsonArray players = (JsonArray) turn.get("Players");
        Iterator<JsonElement> itr = players.iterator();
        int i = 0;

        while (itr.hasNext()) {

            JsonObject jsonObject = (JsonObject) itr.next();

            int playerID = jsonObject.get("Player").getAsInt();
            Player tempPlayer = new Player(playerID, 0);
            //While loop to add territories to player from JSON
            JsonArray territories = (JsonArray) jsonObject.get("Territories");
            Iterator<JsonElement> teris = territories.iterator();
            while (teris.hasNext()) {
                JsonObject tempTerriory = (JsonObject) teris.next();
                String territoryName = String.valueOf(tempTerriory.keySet());
                int territoryArmy = tempTerriory.get(territoryName).getAsInt();

                tempPlayer.addTerritories(territoryName);
                bm.addOccupantsTo(territoryName, territoryArmy);
            }
            // While loop to add cards to hand from JSON
            JsonArray hand = (JsonArray) jsonObject.get("Hand");
            Iterator<JsonElement> cads = hand.iterator();
            while (teris.hasNext())
            {
                JsonObject tempCard = (JsonObject) cads.next();
                String territory = String.valueOf(tempCard.keySet());
                String army = String.valueOf(tempCard.get(territory));
                Card nextCard = new Card(territory, army);
                tempPlayer.addCard(nextCard);
            }
            playerList[i] = tempPlayer;
        }

        return playerList;
    }

    // list available game objects;
    public ArrayList<String> listObjects(String bucket){
        ArrayList<String> k = new ArrayList<>();
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-1")
                .withCredentials(new ProfileCredentialsProvider())
                .build();
        ListObjectsV2Result result = s3.listObjectsV2(bucket);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os: objects) {
            k.add(os.getKey());
        }
        return k;
    }

    Loader(String bucket){
        final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion("us-east-1")
                .withCredentials(new ProfileCredentialsProvider())
                .build();

        try {

            if (!s3Client.doesBucketExistV2(bucket)) {
                // Because the CreateBucketRequest object doesn't specify a region, the
                // bucket is created in the region specified in the client.
                s3Client.createBucket(new CreateBucketRequest(bucket));

                // Verify that the bucket was created by retrieving it and checking its location.
                String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucket));
                System.out.println("Bucket location: " + bucketLocation);
            }
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it and returned an error response.
            System.out.println("If you're getting an access denied error. Make sure you have your AWS configured.");
            e.printStackTrace();
        }
    }

}
