import java.io.*;
import java.util.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonWriter;

public class JSONhandler {

    Gson gson = new Gson();
    ArrayList<String> linststring;
    BoardManager bm = null;
    Player[] playerList = null;
    FileWriter file = new FileWriter("/home/austin/IdeaProjects/sd_project_refactored/Risk.json", false);
    JsonWriter jw = new JsonWriter(file);
    int[] playerTurnPattern;
    private String clientRegion = "us-east-1";
    private String bucketName = "RISK-games";
    private String stringObjKeyName = "test_json";
    private String fileObjKeyName = "RiskGSON";
    private String fileName;
    private String key = "RiskGSON";

    JSONhandler(BoardManager bm, Player[] playerList, int[] playerTurnPattern) throws IOException {
        this.bm = bm;
        this.playerList = playerList;
        this.playerTurnPattern = playerTurnPattern;
        this.fileName = "/home/austin/IdeaProjects/sd_project_refactored/Risk.json";
    }

    public JsonArray JSONreader() throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        Object obj = parser.parse(new FileReader("/home/austin/IdeaProjects/sd_project_refactored/Risk.json"));
        JsonObject jsonObject = (JsonObject) obj;

        JsonArray msg = (JsonArray) jsonObject.get("turns");

        return msg;
    }

    public void JSONwriter(int turnNumber) throws IOException {

        JSONturn newTurn = new JSONturn(bm, playerList, turnNumber, playerTurnPattern);
        JsonObject newTurnJSON = newTurn.createTurnJSON();

        JsonArray msg = JSONreader();
        msg.add(newTurnJSON);

        JsonObject n = new JsonObject();
        n.add("turns", msg);
        gson.toJson(n, jw);

        file.flush();
        file.close();
    }

    public void JSONinitializer(int turnNumber) throws IOException {
        JSONturn newTurn = new JSONturn(bm, playerList, turnNumber, playerTurnPattern);
        JsonObject newTurnJSON = newTurn.createTurnJSON();
        Writer writer = new FileWriter("/home/austin/IdeaProjects/sd_project_refactored/Risk.json");
        Turns Turns = new Turns();
        Turns.addTurn(newTurnJSON);

        Gson gson = new GsonBuilder().create();

        gson.toJson(Turns, Turns.class, writer);

        writer.flush(); //flush data to file   <---
        writer.close(); //close write*/
    }

    public JsonObject getTurnJSON(int turnNumber) throws FileNotFoundException {
        JsonArray msg = JSONreader();
        Iterator< JsonElement > itr = msg.iterator();
        while (itr.hasNext()) {
            JsonObject jsonObject = (JsonObject) itr.next();
            Object Obj = jsonObject.get("Turn");
            if (((JsonElement) Obj).getAsInt() == turnNumber) {
                return jsonObject;
            }
        }
        System.out.println("Turn not found");
        return null; //it wasn't found at all
    }

    public void upload() throws JsonProcessingException {

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(clientRegion)
                .withCredentials(new ProfileCredentialsProvider())
                .build();

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
            metadata.addUserMetadata("x-amz-meta-title", "someTitle");
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

    public void download() throws IOException {


        S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .withCredentials(new ProfileCredentialsProvider())
                    .build();

            s3Client.getObject(
                    new GetObjectRequest(bucketName, key),
                    new File(fileName)
            );

            // Get an object and print its contents.
            System.out.println("Downloading an object");
            fullObject = s3Client.getObject(new GetObjectRequest(bucketName, key));
            System.out.println("Content-Type: " + fullObject.getObjectMetadata().getContentType());
            System.out.println("Content: ");
            displayTextInputStream(fullObject.getObjectContent());

            // Get a range of bytes from an object and print the bytes.
            GetObjectRequest rangeObjectRequest = new GetObjectRequest(bucketName, key)
                    .withRange(0,9);
            objectPortion = s3Client.getObject(rangeObjectRequest);
            System.out.println("Printing bytes retrieved.");
            displayTextInputStream(objectPortion.getObjectContent());

            // Get an entire object, overriding the specified response headers, and print the object's content.
            ResponseHeaderOverrides headerOverrides = new ResponseHeaderOverrides()
                    .withCacheControl("No-cache")
                    .withContentDisposition("attachment; filename=example.txt");
            GetObjectRequest getObjectRequestHeaderOverride = new GetObjectRequest(bucketName, key)
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

}
class JSONturn {

    @Expose
    BoardManager bm = null;
    Player[] playerList = null;
    int turnNumber;
    int[] playerTurnPattern;

    JSONturn(BoardManager bm, Player[] playerList, int turnNumber, int[] playerTurnPattern) {
        this.bm = bm;
        this.playerList = playerList;
        this.turnNumber = turnNumber;
        this.playerTurnPattern = playerTurnPattern;
    }

    public JsonObject createTurnJSON() {
        JsonObject turnJSON = new JsonObject();

        turnJSON.addProperty("Turn", turnNumber);
        List<JsonObject> deck = createDeck();
        turnJSON.add("Deck", (JsonElement) deck);
        JsonArray players = new JsonArray();

        List<JsonObject> Players = new ArrayList<JsonObject>();
        for (int jsonPlayer: playerTurnPattern) {

            JsonObject tempPlayer = new JsonObject();

            Player player = playerList[jsonPlayer];

            tempPlayer.addProperty("Player", jsonPlayer);

            List<JsonObject> hand = createPlayerHand(player);
            tempPlayer.add("Hand", (JsonElement) hand);

            List<JsonObject> territories = createPlayerTerritories(player);
            tempPlayer.add("Territories", (JsonElement) territories);

            Players.add(tempPlayer);
        }

        turnJSON.add("Players", (JsonElement) Players);
        return turnJSON;
    }

    public List<JsonObject> createDeck() {
        ArrayList<Card> deck = new ArrayList<Card>(){{addAll(bm.getGameDeck().GameDeck);}};
        //JsonObject cards = new JsonObject();
        List<JsonObject> cards = new ArrayList<JsonObject>();
        for (Card card: deck) {
            JsonObject temp = new JsonObject();
            temp.addProperty(card.getOrigin(), card.getUnit());
            cards.add(temp);
        }
        return cards;
    }

    public List<JsonObject> createPlayerHand(final Player player) {

        //JsonObject hand = new JsonObject();
        List<JsonObject> hand = new ArrayList<JsonObject>();
        for (Card card: player.getHandListing()) {
            JsonObject temp = new JsonObject();
            temp.addProperty(card.getOrigin(), card.getUnit());
            hand.add(temp);
        }
        return hand;
    }

    public List<JsonObject> createPlayerTerritories(Player player) {
        List<String> t = player.getTerritories();
        //JsonArray ter = new JsonArray();
        List<JsonObject> ter = new ArrayList<JsonObject>();
        for (String terr: t) {
            JsonObject temp = new JsonObject();
            int a = bm.getOccupantCount(terr);
            temp.addProperty(terr, a);
            ter.add(temp);
        }
        return ter;
    }

}

class Turns {
    @Expose
    List<JsonObject> turns = new ArrayList<JsonObject>();

    public List<JsonObject> getTurns() {
        return turns;
    }

    public void addTurn(JsonObject turn) {
        this.turns.add(turn);
    }
}
