import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;


enum Status{
    UNDER_ATTACK, NORMAL, FALLEN
}
public class Territory extends Observable {
    String name;
    private boolean occupied;                                                                                           //
    private int occupiedByID;
    private Army occupants;
    private List<String> neighbors;
    Status status;

    /*////////////////////////////////////////////////////////////////////////////////
    Constructor for a territory. occupants member is initially set to null
    *///////////////////////////////////////////////////////////////////////////////*/
    Territory(String[] neighborList, String name)
    {
        this.name = name;
        this.occupied = false;
        this.occupiedByID = -1;
        this.occupants = null;
        this.neighbors = new ArrayList<String>();
        this.neighbors.addAll(Arrays.asList(neighborList));
        setStatusToNormal();
    }

    public void setStatusToNormal(){
        status = Status.NORMAL;
    }
    public void setStatusToUnderAttack(){
        status = Status.UNDER_ATTACK;
        setChanged();
        notifyObservers();
    }

    public void setStatusToFallen(){
        status = Status.FALLEN;
        setChanged();
        notifyObservers();

        deleteObservers();
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Territory set
    *///////////////////////////////////////////////////////////////////////////////*/
    public void setTerritory(boolean i, int j, Army k){
        occupied = i;
        occupiedByID = j;
        occupants = k;
        setStatusToNormal();
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Adds number of occupants, depending on type
    *///////////////////////////////////////////////////////////////////////////////*/
    public void addOccupants(int army, ArmyType type){
        if (type.equals(ArmyType.INFANTRY))
            occupants.addInfantryCount(army);
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Subtracts number of occupants, depending on type
    *///////////////////////////////////////////////////////////////////////////////*/
    public void loseOccupants(int army, ArmyType type){
        if (type.equals(ArmyType.INFANTRY))
            occupants.loseInfantry(army);
    }

    public void transferOwnership(int newOwner) {
        occupiedByID = newOwner;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns if territory is occupied
    *///////////////////////////////////////////////////////////////////////////////*/
    public boolean isOccupied(){
        return occupied;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns all neighbors of a territory
    *///////////////////////////////////////////////////////////////////////////////*/
    public List<String> getNeighbors(){ return neighbors; }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns id of Occupant
    *///////////////////////////////////////////////////////////////////////////////*/
    public int getOccupantID(){ return occupiedByID; }


    public Army getArmy(){
        return occupants;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns the status of occupant-count, as a string.
    Used only to show in displaying territories
    *///////////////////////////////////////////////////////////////////////////////*/
    public String seeArmyCount(){
        return String.valueOf((occupants.getInfantryCount()));
    }



}