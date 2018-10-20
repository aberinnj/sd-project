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

    Gson gson;
    ArrayList<String> linststring;
    BoardManager bm = null;
    Player[] playerList = null;
    Writer file;
    JsonWriter jw;
    int[] playerTurnPattern;
    private String fileName;


    JSONhandler(BoardManager bm, Player[] playerList, int[] playerTurnPattern, String base) throws IOException {
        this.bm = bm;
        this.playerList = playerList;
        this.playerTurnPattern = playerTurnPattern;
        this.fileName = base + "/src/files//Risk.json";
        this.file = new FileWriter(fileName, false);
        this.jw = new JsonWriter(file);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void write(Turns turns) throws IOException {
        Writer writer = new FileWriter(fileName);
        gson.toJson(turns, Turns.class, writer);
        writer.flush();
        writer.close();
    }

    public JsonArray JSONreader() throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        JsonElement obj = parser.parse(new FileReader(fileName));
        JsonObject jsonObject = obj.getAsJsonObject();

        JsonArray msg = jsonObject.getAsJsonArray("turns");

        return msg;
    }

    public void JSONwriter(int turnNumber) throws IOException {

        JSONturn newTurn = new JSONturn(bm, playerList, turnNumber, playerTurnPattern);
        JsonObject newTurnJSON = newTurn.createTurnJSON();
        JsonArray msg = JSONreader();

        Turns turns = new Turns();
        Iterator< JsonElement > itr = msg.iterator();
        while (itr.hasNext()) {
            JsonObject jsonObject = (JsonObject) itr.next();
            turns.addTurn(jsonObject);
        }

        turns.addTurn(newTurnJSON);
        write(turns);
    }

    public void JSONinitializer(int turnNumber) throws IOException {
        JSONturn newTurn = new JSONturn(bm, playerList, turnNumber, playerTurnPattern);
        JsonObject newTurnJSON = newTurn.createTurnJSON();
        Turns Turns = new Turns();
        Turns.addTurn(newTurnJSON);
        write(Turns);
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
        JsonArray deck = createDeck();
        turnJSON.add("Deck", (JsonElement) deck);
        JsonArray players = new JsonArray();

        JsonArray Players = new JsonArray();
        for (int jsonPlayer: playerTurnPattern) {

            JsonObject tempPlayer = new JsonObject();

            Player player = playerList[jsonPlayer];

            tempPlayer.addProperty("Player", jsonPlayer);

            JsonArray hand = createPlayerHand(player);
            tempPlayer.add("Hand", hand);

            JsonArray territories = createPlayerTerritories(player);
            tempPlayer.add("Territories", territories);

            Players.add(tempPlayer);
        }

        turnJSON.add("Players", (JsonElement) Players);
        return turnJSON;
    }

    public JsonArray createDeck() {
        ArrayList<Card> deck = new ArrayList<Card>(){{addAll(bm.getGameDeck().GameDeck);}};
        //JsonObject cards = new JsonObject();
        JsonArray cards = new JsonArray();
        for (Card card: deck) {
            JsonObject temp = new JsonObject();
            temp.addProperty(card.getOrigin(), card.getUnit());
            cards.add(temp);
        }
        return cards;
    }

    public JsonArray createPlayerHand(final Player player) {

        //JsonObject hand = new JsonObject();
        JsonArray hand = new JsonArray();
        for (Card card: player.getHandListing()) {
            JsonObject temp = new JsonObject();
            temp.addProperty(card.getOrigin(), card.getUnit());
            hand.add(temp);
        }
        return hand;
    }

    public JsonArray createPlayerTerritories(Player player) {
        List<String> t = player.getTerritories();
        //JsonArray ter = new JsonArray();
        JsonArray ter = new JsonArray();
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
