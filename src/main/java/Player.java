import java.util.ArrayList;
import java.util.List;

public class Player {

    private int id;
    private int infantryCount;
    private List<String> territories;

    /* Getters: getInfantryCount used for battles*/
    public int getInfantryCount() {
        return infantryCount;
    }

    /* Getters: getID used for setting a territory to a player*/
    public int getId() {
        return id;
    }

    /* Mutator: setting infantry */
    public int loseInfantry(int i) {
        return infantryCount - i;
    }

    public void addTerritories(String country){
        territories.add(country);
    }
    public void seeTerritories(){
        System.out.print("__________________________________________\nPlayer #"+id+" Territories\n");
        for(String country: territories){
            System.out.println(country);
        }
    }

    Player(int id, int infantryCount)
    {
        this.id = id;
        this.infantryCount=infantryCount;
        territories = new ArrayList<String>();
    }
}
