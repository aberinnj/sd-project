import java.util.*;

/*///////////////////////////////////////////////////////////////////////////////
TurnManager - takes car of client turns, saves result into object and calls uploads

TurnManager should be making deep-copying turns for recreating entire games from turns
 *//////////////////////////////////////////////////////////////////////////////
public class TurnManager {
    private ArrayList<Turn> turnList;

    public ArrayList<Turn> getTurnList(){
        return turnList;
    }

    // testable deep copy of boardMap
    public HashMap<String, Territory> copy(HashMap<String, Territory> old){
        HashMap<String, Territory> actualMap = new HashMap<String, Territory>();
        for(Map.Entry<String, Territory> obj: old.entrySet())
        {
            String[] neighbors = obj.getValue().getNeighbors().toArray(new String[0]);
            boolean isOccupied = obj.getValue().isOccupied();
            int armyCount = obj.getValue().getArmy().getInfantryCount();
            int occupantID = obj.getValue().getOccupantID();

            actualMap.put(obj.getKey(), new Territory(neighbors, obj.getKey()));
            actualMap.get(obj.getKey()).setTerritory(isOccupied, occupantID, new Army(armyCount));
        }
        return actualMap;
    }

    // testable deep copy of game-deck
    public Deck copy(Deck old){
        Stack<Card> actualDeckStack = new Stack<Card>();
        for(int i=0; i<old.GameDeck.size(); i++)
        {
            actualDeckStack.add(i, old.GameDeck.get(i));
        }
        return new Deck(actualDeckStack);
    }

    // Users can undo their actions with this
    public Turn getLastMove(){
        return turnList.get(turnList.size()-1);
    }

    // Users can replay games
    public Turn returnToMove(int i)
    {
        return turnList.get(i);
    }

    TurnManager(){
        turnList = new ArrayList<Turn>();
    }
}
