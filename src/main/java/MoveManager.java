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

    // Users can replay games
    public Move returnToMove(int i)
    {
        return moveList.get(i);
    }

    MoveManager(){
        moveList = new ArrayList<Move>();
    }
}
