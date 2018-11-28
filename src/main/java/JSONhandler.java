import java.io.*;
import java.util.*;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonWriter;

public class JSONhandler {

    Gson gson;
    BoardManager bm;
    Game game;
    String fileName;
    String base;

    JSONhandler(Game game) {
        this.bm = game.BM;
        this.game = game;
        this.base = System.getProperty("user.dir");
        this.fileName = base + "/src/files//Risk.json";
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void write(JsonObject newTurnJSON) throws IOException {
        Writer writer = new FileWriter(fileName, false);
        // JsonWriter jw = new JsonWriter(file);
        gson.toJson(newTurnJSON, writer);
        writer.flush();
        writer.close();
    }

    public JsonObject JSONreader() throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        JsonElement obj = parser.parse(new FileReader(fileName));
        System.out.println(fileName);
        System.out.println(obj.isJsonNull());
        JsonObject jsonObject = obj.getAsJsonObject();
        return jsonObject;
    }

    public void JSONwriter() throws IOException {
        JSONturn newTurn = new JSONturn(game);
        JsonObject newTurnJSON = newTurn.createTurnJSON();
        write(newTurnJSON);
    }
}

class JSONturn {

    @Expose
    Game game;

    JSONturn(Game game) {
        this.game = game;
    }

    public JsonObject createTurnJSON() {
        JsonObject turnJSON = new JsonObject();
        turnJSON.addProperty("gameID", game.gameID); // save gameID as gameID
        turnJSON.addProperty("Turn", game.turn);//dido for turn number
        JsonArray deck = createDeck();
        turnJSON.add("Deck", deck); // save Deck
        JsonArray Players = new JsonArray();
        for (int i = 0; i < game.playerDirectory.size(); i++) { // save players

            JsonObject tempPlayerJSON = new JsonObject();

            Player tempPlayer = game.playerDirectory.get(i);
            tempPlayerJSON.addProperty("PlayerID", tempPlayer.getId());
            tempPlayerJSON.addProperty("PlayerName", tempPlayer.username);
            tempPlayerJSON.addProperty("ChatID", tempPlayer.chat_id);
            tempPlayerJSON.addProperty("PlayerWallet", tempPlayer.getWallet());
            tempPlayerJSON.addProperty("Undos",tempPlayer.getUndos());
            //tempPlayerJSON.addProperty("Armies", tempPlayer.getNumberOfArmies());

            JsonArray hand = createPlayerHand(tempPlayer);
            tempPlayerJSON.add("Hand", hand);

            JsonArray territories = createPlayerTerritories(tempPlayer);
            tempPlayerJSON.add("Territories", territories);

            Players.add(tempPlayerJSON);
        }

        turnJSON.add("Players", Players);
        return turnJSON;
    }

    public JsonArray createDeck() {
        ArrayList<Card> deck = new ArrayList<Card>(){{addAll(game.BM.getGameDeck().GameDeck);}};
        //JsonObject cards = new JsonObject();
        JsonArray cards = new JsonArray();
        for (Card card: deck) {
            JsonObject temp = new JsonObject();
            temp.addProperty("Territory", card.getOrigin());
            temp.addProperty("Unit", card.getUnit());
            cards.add(temp);
        }
        return cards;
    }

    public JsonArray createPlayerHand(final Player player) {

        //JsonObject hand = new JsonObject();
        JsonArray hand = new JsonArray();
        for (Card card: player.getHandListing()) {
            JsonObject temp = new JsonObject();
            temp.addProperty("Territory", card.getOrigin());
            temp.addProperty("Unit", card.getUnit());
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
            int a = game.BM.getOccupantCount(terr);
            temp.addProperty("Territory", terr);
            temp.addProperty("Unit", a);
            ter.add(temp);
        }
        return ter;
    }

}
