import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*///////////////////////////////////////////////////////////////////////////////
TurnManager
 *//////////////////////////////////////////////////////////////////////////////
public class TurnManager {
    private ArrayList<Turn> turnList;

    public void save(Turn t){
        turnList.add(t);
    }

    // Users can undo their actions
    public Turn getLastMove(){
        return turnList.get(turnList.size()-1);
    }

    // Users can replay games
    public Turn returnToMove(int i)
    {
        return turnList.get(i);
    }

    public void addToMoveManager(BoardManager bm, TurnManager MM, Player[] list, int size, int playerID){
        HashMap<String, Territory> moveMap = new HashMap<String, Territory>();
        HashMap<String, Territory> boardMap = bm.getBoardMap();
        HashMap<Integer, List<String>> playerTerritories = new HashMap<Integer, List<String>>();
        for(String key: boardMap.keySet())
        {
            //String[] neighbors = Array<String>(boardMap.get(key).getNeighborsOf());
            int occ = boardMap.get(key).getOccupantID();
            boolean isOcc = boardMap.get(key).isOccupied();
            int count = bm.getOccupantCount(key);
            //moveMap.put(key, new Territory(neighbors));

        }
        for(int i=0; i<size; i++)
        {
            List<String> territoryList = new ArrayList<String>(list[i].getTerritories());
            playerTerritories.put(i, territoryList);
        }

    }

    TurnManager(){
        turnList = new ArrayList<Turn>();
    }
}
