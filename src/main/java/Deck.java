import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Deck {

    private static HashMap<String, String> thisDeck;
    private static ArrayList<Entry<String, String>> Deck;

    //Function for drawing a random card from the deck and then removing said entry from the deck
    public static Entry<String, String> drawCard() {

        int rando = (int) ((Math.random() * Deck.size()));
        Entry<String, String> out = Deck.get(rando);
        Deck.remove(rando);
        return out;
    }

    public static ArrayList<Entry<String, String>> getDeck() { return Deck; }

    Deck(String path) {

        //Create a hashtable of the cards from the original Risk game version
        //cards are formatted with a territory and troop type

        thisDeck = new HashMap<String, String>();
        try {
            FileReader json = new FileReader(path);
            JsonParser parser = new JsonParser();

            JsonObject rootObj = parser.parse(json).getAsJsonObject();
            JsonArray cardDeck = rootObj.getAsJsonArray("Cards");

            for (JsonElement mapItem: cardDeck) {
                JsonObject cardObject = mapItem.getAsJsonObject();
                String cardTerritory = cardObject.get("name").getAsString();
                String cardUnit = cardObject.get("unit").getAsString();

                thisDeck.put(cardTerritory, cardUnit);

            }

            //Set of entries
            Set<Entry<String, String>> cards = thisDeck.entrySet();

            //Creating an ArrayList using Set data
            ArrayList<Entry<String, String>> Deck = new ArrayList<Entry<String,String>>(cards);

            this.Deck = Deck;

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
