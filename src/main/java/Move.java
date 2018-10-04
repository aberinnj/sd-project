
import java.util.HashMap;
import java.util.List;

public class Move {
    public int playerID;
    public HashMap<String, Territory> CurrentTerritoryStatus;
    public HashMap<Integer, List<String>> playerTerritories;

    Move(int id, HashMap<String, Territory> cts, HashMap<Integer, List<String>>  pt){
        playerID = id;
        CurrentTerritoryStatus = cts;
        playerTerritories = pt;
    }
}
