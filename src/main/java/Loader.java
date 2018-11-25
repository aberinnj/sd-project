import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.*;


/*////////////////////////////////////////////////////////////////////////////////
Loader class takes care of remote turns, makes use of JSONHandler to get results
reinstantiates loaded game
*///////////////////////////////////////////////////////////////////////////////*/
public class Loader {

    Game game;

    // New loader creates new game to later be fully instantiated using saved variables loaded from S3
    Loader(String ID) {
        this.game = new Game();
        game.setGameID(ID);
    }

    public Game LoadGame() throws IOException {
        JSONhandler JH = new JSONhandler(game);
        JsonObject gameJson = JH.JSONreader();
        setGame(gameJson);
        setDeck(gameJson);
        setPlayers(gameJson);
        return game;
    }

    public void setGame(JsonObject gameJson) {
        game.setTurn(gameJson.get("Turn").getAsInt());
    }

    public void setDeck(JsonObject turn)
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
        game.resetDeck(GameDeck);
    }

    public void setPlayers(JsonObject gameJson) {
        JsonArray players = (JsonArray) gameJson.get("Players");

        Iterator<JsonElement> itr = players.iterator();
        while (itr.hasNext()) {
            JsonObject playerJSON = (JsonObject) itr.next();

            //Get variables saved in the JSON
            int playerID = playerJSON.get("PlayerID").getAsInt();
            String playerName = playerJSON.get("PlayerName").getAsString();
            long chat_id = playerJSON.get("ChatID").getAsLong();
            double wallet = playerJSON.get("PlayerWallet").getAsDouble();
            int undos = playerJSON.get("Undos").getAsInt();

            //re-instantiate the players from the loaded variables
            Player tempPlayer = new Player(playerID, playerName, chat_id, 0);
            tempPlayer.addMoney(wallet);
            tempPlayer.addUndos(undos);

            //While loop to add territories/ armies to player from JSON
            JsonArray territories = (JsonArray)playerJSON.get("Territories");
            Iterator<JsonElement> teris = territories.iterator();
            while (teris.hasNext()) {
                JsonObject tempTerriory = (JsonObject) teris.next();
                String territoryName = String.valueOf(tempTerriory.keySet());
                int territoryArmy = tempTerriory.get(territoryName).getAsInt();

                tempPlayer.addTerritories(territoryName);
                game.BM.addOccupantsTo(territoryName, territoryArmy);
            }

            // While loop to add cards to hand from JSON
            JsonArray hand = (JsonArray) playerJSON.get("Hand");
            Iterator<JsonElement> cads = hand.iterator();
            while (cads.hasNext())
            {
                JsonObject tempCard = (JsonObject) cads.next();
                String territory = String.valueOf(tempCard.keySet());
                String army = String.valueOf(tempCard.get(territory));
                Card nextCard = new Card(territory, army);
                tempPlayer.getHand().get(nextCard.getUnit()).push(nextCard);
            }

            // put the tempPlayer into the games player directory
            game.playerDirectory.put(game.playerDirectory.size(), tempPlayer);
            _GameMaster.allPlayersAndTheirGames.put(playerID, game.gameID);
        }
    }
}
