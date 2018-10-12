import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Territory {
    private boolean occupied;                                                                                           //
    private int occupiedByID;
    private Army occupants;
    private List<String> neighbors;

    /*////////////////////////////////////////////////////////////////////////////////
    Constructor for a territory. occupants member is initially set to null
    *///////////////////////////////////////////////////////////////////////////////*/
    Territory(String[] neighborList)
    {
        occupied = false;
        occupiedByID = -1;
        occupants = null;
        neighbors = new ArrayList<String>();
        neighbors.addAll(Arrays.asList(neighborList));
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Territory set
    *///////////////////////////////////////////////////////////////////////////////*/
    public void setTerritory(boolean i, int j, Army k){
        occupied = i;
        occupiedByID = j;
        occupants = k;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Adds number of occupants, depending on type
    *///////////////////////////////////////////////////////////////////////////////*/
    public void addOccupants(int army, String type) throws Exception{
        if (type.equals("INFANTRY")){
            occupants.addInfantryCount(army);
        }
        else
            throw new Exception("Invalid Army Type ");
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Subtracts number of occupants, depending on type
    *///////////////////////////////////////////////////////////////////////////////*/
    public void loseOccupants(int army, String type) throws Exception{
        if (type.equals("INFANTRY")){
            occupants.loseInfantry(army);
        }
        else
            throw new Exception("Invalid Army Type ");
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