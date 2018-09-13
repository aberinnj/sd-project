import java.util.List;

public class Territory {
    private boolean occupied;                                                                                           //
    private int occupiedByID;
    private Army occupants;
    private List<String> neighbors;

    /*////////////////////////////////////////////////////////////////////////////////
    Army Type as enum to avoid programmer mistakes of mistyping string values

    Not yet implemented
    *///////////////////////////////////////////////////////////////////////////////*/
    enum ARMY_TYPE{
        INFANTRY, CAVALRY, ARTILLERY
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Constructor for a territory. occupants member is initially set to null
    *///////////////////////////////////////////////////////////////////////////////*/
    Territory(boolean h, int i, Army j,List<String> k)
    {
        occupied = h;
        occupiedByID = i;
        occupants = j;
        neighbors = k;
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
    Adds or subtracts number of occupants, depending on type
    *///////////////////////////////////////////////////////////////////////////////*/
    public void addOccupants(int army, String type) throws Exception{
        if (type.equals("INFANTRY")){
            occupants.addInfantryCount(army);
        }
        else if (type.equals("CAVALRY")){
            occupants.addCavalryCount(army);
        }
        else if (type.equals("ARTILLERY")){
            occupants.addCavalryCount(army);
        }else
            throw new Exception("Invalid Army Type {Replace with enum(?) INFANTRY}");
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
    public List<String> getNeighbors(){
        return neighbors;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns the status of occupant-count, as a string.
    Used only to show in displaying territories
    *///////////////////////////////////////////////////////////////////////////////*/
    public String seeArmyCount(){
        return (occupants.getInfantryCount() + "/" + occupants.getCavalryCount() + "/" + occupants.getArtilleryCount());
    }

}