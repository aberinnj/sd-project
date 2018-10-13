import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Map.Entry;

/*////////////////////////////////////////////////////////////////////////////////
Player Class
todo: setTerritories renders null throwing nullpointerexception in testing
*///////////////////////////////////////////////////////////////////////////////*/
public class Player {

    private int id;
    private Army unassignedArmy;
    private List<String> territories;
    private ArrayList<Card> Hand;
    private ArrayList<String> HandDesigns;

    // get player id
    public int getId() {
        return id;
    }

    // adds a country to player's territories
    public void addTerritories(String country){
        territories.add(country);
    }

    // removes a country from a player's territories
    public void loseTerritories(String country){
        if(ifPlayerHasTerritory(country)) {
            territories.remove(country);
        }
    }

    // replaces all territories with expected List K
    public void setTerritories(List<String> k)
    {
        territories.clear();
        territories.addAll(k);
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
        return (unassignedArmy.getInfantryCount() == 0);
    }

    public void addArmies(int armies) { unassignedArmy.addInfantryCount(armies); }

    /*////////////////////////////////////////////////////////////////////////////////
    Method Diminishes number of army
     *///////////////////////////////////////////////////////////////////////////////*/
    public void deployArmies(int i){
        unassignedArmy.loseInfantry(i);
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns the total number of remaining infantry
     *///////////////////////////////////////////////////////////////////////////////*/
    public int getRemainingArmies(){ return unassignedArmy.getInfantryCount(); }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns the total number of remaining infantry
    *///////////////////////////////////////////////////////////////////////////////*/
    public boolean isPlayerTheWinner(BoardManager bm){
        return territories.size() == bm.getNumberOfTerritories();
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns bool depicting if player still has territories
    *///////////////////////////////////////////////////////////////////////////////*/
    public boolean hasPlayerLost(BoardManager bm){
        return territories.size() == 0;
    }

    /*
    Moved the ship armies function here so there would be one function as well as to only allow one player to place armies
    at a time
    */
    public void deployInfantry(BoardManager bm, Scanner territoryScanner) {
        //boolean invalidTerritory = true;
        while (!isBaseEmpty()) {
            System.out.println("__________________________________________");
            System.out.println("Infantry Count And Player #"+id+" Territories");
            for (String i: territories) {System.out.println(bm.getOccupantCount(i) + " " + i);}

            System.out.println("Remaining armies: " + getRemainingArmies());
            System.out.println("Select a territory to ship your Army to: ");

            //while(invalidTerritory) {
            try {
                // prompts user for a territory
                String territory = territoryScanner.nextLine();
                if (!ifPlayerHasTerritory(territory))
                    throw new Exception("Error: " + territory + " is not your territory");
                //invalidTerritory = false;
                // transfer infantry to territory
                bm.addOccupantsTo(territory, 1, "INFANTRY");
                deployArmies(1);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            //}
        }
    }

    //
    public void fortifyTerritory(BoardManager bm, String origin, String destination, int army) {

        bm.transferOccupantsFrom(origin, army, "INFANTRY");
        bm.addOccupantsTo(destination, army, "INFANTRY");

    }


    /*////////////////////////////////////////////////////////////////////////////////
    Methods returns a copy of the territories list
    *///////////////////////////////////////////////////////////////////////////////*/
    public List<String> getTerritories(){ return territories; }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns the number territories a player has
    *///////////////////////////////////////////////////////////////////////////////*/
    public int numOfTerritories(){
            return territories.size();
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method checks the number of continents a player owns and designates more armies
    *///////////////////////////////////////////////////////////////////////////////*/
    public int continentsOwned(BoardManager bm){
        int moreArmies= 0;
        String ownedContinents = " ";

        if (territories.containsAll(bm.getContinentsMap("AUSTRALIA"))){
            moreArmies =+ 2;
            ownedContinents += "AUSTRALIA, ";
        }
        if (territories.containsAll(bm.getContinentsMap("ASIA"))){
            moreArmies += 7;
            ownedContinents += "ASIA, ";
        }
        if (territories.containsAll(bm.getContinentsMap("NORTH AMERICA"))){
            moreArmies += 5;
            ownedContinents += "NORTH AMERICA, ";
        }
        if (territories.containsAll(bm.getContinentsMap("EUROPE"))){
            moreArmies += 5;
            ownedContinents += "EUROPE, ";
        }
        if (territories.containsAll(bm.getContinentsMap("AFRICA"))){
            moreArmies += 3;
            ownedContinents += "AFRICA, ";
        }
        if (territories.containsAll(bm.getContinentsMap("SOUTH AMERICA"))){
            moreArmies += 2;
            ownedContinents += "SOUTH AMERICA, ";
        }else{
            ownedContinents += "NO CONTINENTS. ";
        }
        System.out.println("You get " + moreArmies + " armies because you own " + ownedContinents + "Congratulations!");

        return moreArmies;
    }

    public void addToHand(Card e){
        Hand.add(e);
        HandDesigns.add(e.getUnit());
    }

    public ArrayList<Card> getHand() {
        return Hand;
    }
    public ArrayList<String> getHandDesigns() {
        return HandDesigns;
    }


    /*////////////////////////////////////////////////////////////////////////////////
    Constructor for making a player
    *///////////////////////////////////////////////////////////////////////////////*/
    Player(int id, int infantryCount)
    {
        this.id = id;
        unassignedArmy = new Army(infantryCount);
        Hand = new ArrayList<Card>();
        territories = new ArrayList<String>();
        HandDesigns = new ArrayList<String>();
    }
}
