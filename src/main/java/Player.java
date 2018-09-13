import java.util.ArrayList;
import java.util.List;

public class Player {

    private int id;                                                                                                     //simple id used for identifying player 0-5
    private Army homebase;                                                                                              //initial number of infantry, holds initial values
                                                                                                                        //use of int is also possible, but object Army is used instead in case setup changes
                                                                                                                        //and players also start with other pieces than just the infantry
    private List<String> territories;                                                                                   //List of player's territories


    /*////////////////////////////////////////////////////////////////////////////////
    Get method, rerturns id
     *///////////////////////////////////////////////////////////////////////////////*/
    public int getId() {
        return id;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Adds a country to Player's territory list
     *///////////////////////////////////////////////////////////////////////////////*/
    public void addTerritories(String country){
        territories.add(country);
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Loses a territory.
    *///////////////////////////////////////////////////////////////////////////////*/
    public void loseTerritories(String country){
        if(ifPlayerHasTerritory(country)) {
            territories.remove(country);
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method lists the Player's territories
     *///////////////////////////////////////////////////////////////////////////////*/
    public void displayPlayerTerritories(BoardManager bm){
        System.out.println("__________________________________________");
        System.out.println("Infantry/Cavalry/Artillery Count And Player #"+id+" Territories");

        for(String country: territories){
            System.out.print(bm.getOccupantCount(country) + " ");
            System.out.println(country);
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method checks if player has a territory
     *///////////////////////////////////////////////////////////////////////////////*/
    public boolean ifPlayerHasTerritory(String country){
        return territories.contains(country);
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method checks if player no longer has unused infantry pieces
     *///////////////////////////////////////////////////////////////////////////////*/
    public boolean isBaseEmpty(){
        boolean isEmpty = (homebase.getInfantryCount() == 0 && homebase.getCavalryCount() == 0 && homebase.getArtilleryCount() ==0);
        return isEmpty;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method Diminishes number of army
     *///////////////////////////////////////////////////////////////////////////////*/
    public void shipArmy(){
        homebase.loseInfantry();
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns the total number of remaining infantry
     *///////////////////////////////////////////////////////////////////////////////*/
    public int getRemainingArmies(){
        return homebase.getArtilleryCount() + homebase.getCavalryCount() + homebase.getInfantryCount();

    }

    /*////////////////////////////////////////////////////////////////////////////////
    Constructor for making a player
    *///////////////////////////////////////////////////////////////////////////////*/
    Player(int id, int infantryCount)
    {
        this.id = id;
        homebase = new Army(infantryCount);
        territories = new ArrayList<String>();
    }
}
