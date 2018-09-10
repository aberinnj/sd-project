import java.util.ArrayList;
import java.util.List;

public class Player {

    private int id;
    private Army homebase;
    private List<String> territories;


    /* Getters: getID used for setting a territory to a player*/
    public int getId() {
        return id;
    }


    public void addTerritories(String country){
        territories.add(country);
    }
    public void seeTerritories(BoardManager bm){
        System.out.print("__________________________________________\nInfantry/Cavalry/Artillery Count And Player #"+id+" Territories\n");
        for(String country: territories){
            System.out.print(bm.getOccupantCount(country) + " ");
            System.out.println(country);
        }
    }
    public boolean checkIfPlayerHasTerritory(String country){
        return territories.contains(country);
    }

    public boolean isBaseEmpty(){
        boolean isEmpty = (homebase.getInfantryCount() == 0 && homebase.getCavalryCount() == 0 && homebase.getArtilleryCount() ==0);
        return isEmpty;
    }

    public void shipArmy(){
        homebase.loseInfantry();
    }
    public int getRemainingArmies(){
        return homebase.getArtilleryCount() + homebase.getCavalryCount() + homebase.getInfantryCount();

    }

    Player(int id, int infantryCount)
    {
        this.id = id;
        homebase = new Army(id, infantryCount);
        territories = new ArrayList<String>();
    }
}
