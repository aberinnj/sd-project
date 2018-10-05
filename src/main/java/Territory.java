import java.util.List;

public class Territory {
    private boolean occupied;                                                                                           //
    private int occupiedByID;
    private Army occupants;
    private List<String> neighbors;

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
    Adds number of occupants, depending on type
    *///////////////////////////////////////////////////////////////////////////////*/
    public void addOccupants(int army, String type) throws Exception{
        if (type.equals("INFANTRY")){
            occupants.addInfantryCount(army);
        }
        else if (type.equals("CAVALRY")){
            occupants.addCavalryCount(army);
        }
        else if (type.equals("ARTILLERY")){
            occupants.addArtilleryCount(army);
        }else
            throw new Exception("Invalid Army Type ");
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Subtracts number of occupants, depending on type
    *///////////////////////////////////////////////////////////////////////////////*/
    public void loseOccupants(int army, String type) throws Exception{
        if (type.equals("INFANTRY")){
            occupants.loseInfantry(army);
        }
        else if (type.equals("CAVALRY")){
            occupants.loseCavalry(army);
        }
        else if (type.equals("ARTILLERY")){
            occupants.loseArtillery(army);
        }else
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

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns the number of occupants in the territory, as int (compared to method
    below which only returns a string

    Returns the number of infantry, as base unit.
    Assumption: Cavalry = 5 Infantry and Artillery = 2 Cavalry
    *///////////////////////////////////////////////////////////////////////////////*/
    public int ArmyCount()
    {
        return occupants.getInfantryCount() + 5*(occupants.getCavalryCount()) + 10*(occupants.getArtilleryCount());
    }

    public Army getArmy(){
        return occupants;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns the status of occupant-count, as a string.
    Used only to show in displaying territories
    *///////////////////////////////////////////////////////////////////////////////*/
    public String seeArmyCount(){
        return (occupants.getInfantryCount() + "/" + occupants.getCavalryCount() + "/" + occupants.getArtilleryCount());
    }



}