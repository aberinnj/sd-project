import java.util.List;

/**/
public class Territory {
    private boolean occupied;
    private int occupiedByID;
    private Army occupants;
    private List<String> neighbors;

    /* FOR GUI, for now use ifs in func addOccupants*/
    enum ARMY_TYPE{
        INFANTRY, CAVALRY, ARTILLERY
    }

    /**/
    Territory(boolean h, int i, Army j,List<String> k)
    {
        occupied = h;
        occupiedByID = i;
        occupants = j;
        neighbors = k;
    }

    /**/
    public void setTerritory(boolean i, int j, Army k){
        occupied = i;
        occupiedByID = j;
        occupants = k;
    }
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

    public boolean isOccupied(){
        return occupied;
    }

    public List<String> getNeighbors(){
        return neighbors;
    }

    public String seeArmyCount(){
        return (occupants.getInfantryCount() + "/" + occupants.getCavalryCount() + "/" + occupants.getArtilleryCount());
    }

}