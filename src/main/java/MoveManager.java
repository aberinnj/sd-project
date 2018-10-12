import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class MoveManager {
    private ArrayList<Move> moveList;

    public void addMove(Move i){
        moveList.add(i);
    }

    // Users can undo their actions
    public Move getLastMove(){
        return moveList.get(moveList.size()-1);
    }

    // Users can replay games
    public Move returnToMove(int i)
    {
        return moveList.get(i);
    }

    public Move addToMoveManager(BoardManager bm, MoveManager MM, Player[] list, int size, int playerID){
        HashMap<String, Territory> moveMap = new HashMap<String, Territory>();
        HashMap<String, Territory> boardMap = bm.getBoardMap();
        HashMap<Integer, List<String>> playerTerritories = new HashMap<Integer, List<String>>();
        for(String key: boardMap.keySet())
        {
            //String[] neighbors = Array<String>(boardMap.get(key).getNeighborsOf());
            int occ = boardMap.get(key).getOccupantID();
            boolean isOcc = boardMap.get(key).isOccupied();
            int count = boardMap.get(key).getArmy().getInfantryCount();
            //moveMap.put(key, new Territory(neighbors));

        }
        for(int i=0; i<size; i++)
        {
            List<String> territoryList = new ArrayList<String>(list[i].getTerritories());
            playerTerritories.put(i, territoryList);
        }
        return new Move(playerID, moveMap, playerTerritories);

    }

    MoveManager(){
        moveList = new ArrayList<Move>();
    }
}
