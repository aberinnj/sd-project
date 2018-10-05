import java.util.ArrayList;
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

    MoveManager(){
        moveList = new ArrayList<Move>();
    }
}
