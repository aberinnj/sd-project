import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Map.Entry;

public class Player {

    private int id;                                                                                                     //simple id used for identifying player 0-5
    private Army homebase;                                                                                              //initial number of infantry, holds initial values
                                                                                                                        //use of int is also possible, but object Army is used instead in case setup changes
                                                                                                                        //and players also start with other pieces than just the infantry
    private List<String> territories;                                                                                   //List of player's territories

    private ArrayList<Entry<String, String>> Hand;                                                                      //List of cards a player has

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

    public void setTerritories(List<String> k)
    {
        territories.clear();
        territories.addAll(k);
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method lists the Player's territories
     *///////////////////////////////////////////////////////////////////////////////*/
    public void displayPlayerTerritories(BoardManager bm){
        System.out.println("__________________________________________");
        System.out.println("Infantry Count And Player #"+id+" Territories");

        for(String country: territories){
            System.out.print(bm.getOccupantCount(country) + " ");
            System.out.println(country);
        }
    }

    public void displayPlayerNeighboringTerritories(BoardManager bm, String origin) {
        System.out.println("Neighboring Territories: ");
        for(String country: territories){
            if (bm.isTerritoryANeighborOf(country, origin) && (bm.getTerritoryID(origin) == bm.getTerritoryID(country))) {
                System.out.print(bm.getOccupantCountStatus(country) + " ");
                System.out.println(country);
            }
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method displays Player's adjacent territories to given territory argument
     *///////////////////////////////////////////////////////////////////////////////*/
    public void displayAttackableNeighboringTerritories(BoardManager bm) {
        System.out.println("Infantry Count And Neighboring Countries");
        for(String country: territories){
            List<String> neighbors = bm.getNeighbors(country);
            System.out.println(country + ", Army count: " + bm.getOccupantCount(country) + " Can Attack:");
            for (String neighbor: neighbors) {
                if (bm.getTerritoryID(neighbor) != bm.getTerritoryID(country)) {
                    System.out.println(neighbor + ", Army count: " + bm.getOccupantCount(neighbor));
                }
            }
        }
    }

    public List<String> displayNeighborsAttacking(BoardManager bm, String attacker) {
        List<String> targets = new ArrayList<>();
        System.out.println("Neighboring Territories: ");
        List<String> neighbors = bm.getNeighbors(attacker);
        for(String country: neighbors){
            if (bm.getTerritoryID(attacker) != bm.getTerritoryID(country)) {
                targets.add(bm.getOccupantCountStatus(country) + " " + country);
            }
        }
        return targets;
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
        boolean isEmpty = (homebase.getInfantryCount() == 0);
        return isEmpty;
    }

    public void addArmies(int armies) { homebase.addInfantryCount(armies); }

    /*////////////////////////////////////////////////////////////////////////////////
    Method Diminishes number of army
     *///////////////////////////////////////////////////////////////////////////////*/
    public void shipArmy(){
        homebase.loseInfantry(1);
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns the total number of remaining infantry
     *///////////////////////////////////////////////////////////////////////////////*/
    public int getRemainingArmies(){ return homebase.getInfantryCount(); }

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
    public void shipArmies(BoardManager bm, Scanner territoryScanner) {
        //boolean invalidTerritory = true;
        while (!isBaseEmpty()) {
            displayPlayerTerritories(bm);

            System.out.println("Remaining armies: " + getRemainingArmies());
            System.out.println("Select a territory to ship your Army to: ");
            //Scanner territoryScanner = new Scanner(System.in);

            //while(invalidTerritory) {
            try {
                // prompts user for a territory
                String territory = territoryScanner.nextLine();
                if (!ifPlayerHasTerritory(territory))
                    throw new Exception("Error: " + territory + " is not your territory");
                //invalidTerritory = false;
                // transfer infantry to territory
                bm.addOccupantsTo(territory, 1, "INFANTRY");
                shipArmy();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            //}
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method executes movement of one army to another

    Note: Add exceptions to invalid string values in case of a mistake on the programmer's
    part
    Assumption: Cavalry = 5 Infantry and Artillery = 2 Cavalry
    *///////////////////////////////////////////////////////////////////////////////*/
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
        if (territories.size()<4) {
            return 0;
        }

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
        }
        System.out.println("You get " + moreArmies + "armies because you own " + ownedContinents + "Congratulations!");

        return moreArmies;
    }


    public ArrayList<Entry<String, String>> getHand() {
        return Hand;
    }

    public void displayHand() {
        System.out.println("You have the current cards");
        for (int i = 0; i < Hand.size();i++) {
            System.out.println(Hand.get(i));
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Constructor for making a player
    *///////////////////////////////////////////////////////////////////////////////*/
    Player(int id, int infantryCount)
    {
        this.id = id;
        homebase = new Army(infantryCount);
        Hand = new ArrayList<Entry<String, String>>();
        territories = new ArrayList<String>();
    }
}
