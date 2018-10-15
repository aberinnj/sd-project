import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

public class LoadGame {

    public JsonObject LoadGame(int turnNumber, BoardManager bm) throws IOException {

        Player[] emptyPlayer = null;
        int[] emptyTurnPattern = null;

        JSONhandler JH = new JSONhandler(bm, emptyPlayer, emptyTurnPattern);
        JH.download();
        JsonObject turn = JH.getTurnJSON(turnNumber);
        return turn;
    }

    public int getNumPlayers(JsonObject turn) {
        JsonArray players = (JsonArray) turn.get("Players");
        return players.size();
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

            JsonArray territories = (JsonArray) jsonObject.get("Territories");
            Iterator<JsonElement> teris = territories.iterator();
            while (teris.hasNext()) {
                JsonObject tempTerriory = (JsonObject) teris.next();
                String territoryName = String.valueOf(tempTerriory.keySet());
                int territoryArmy = tempTerriory.get(territoryName).getAsInt();

                tempPlayer.addTerritories(territoryName);
                bm.addOccupantsTo(territoryName, territoryArmy);
            }

            //JsonArray hand = (JsonArray) jsonObject.get("Hand");
            //Hand needs to be implemented as well using a similar technique as territories
            //Did not implement because Norman is reconfiguring the Deck system.
            playerList[i] = tempPlayer;
        }

        return playerList;
    }

}
