import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

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
    Method displays Player's adjacent territories to given territory argument
     *///////////////////////////////////////////////////////////////////////////////*/
    public void displayPlayerNeighboringTerritories(BoardManager bm, String origin){
        System.out.println("Infantry/Cavalry/Artillery Count And Neighboring Countries");
        for(String country: territories){
            if(bm.isTerritoryANeighborOf(country, origin))
            {
                System.out.print(bm.getOccupantCountStatus(country) + " ");
                System.out.println(country);
            }
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method checks if player has a territory
     *///////////////////////////////////////////////////////////////////////////////*/
    public boolean ifPlayerHasTerritory(String country){
        return territories.contains(country);
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Methods returns a copy of the territories list
    *///////////////////////////////////////////////////////////////////////////////*/
    public <String> List <String> territoriesCopy(){
        List<String> copy = (List<String>) territories.stream().collect(Collectors.toList());
        return copy;
    }
    
    /*////////////////////////////////////////////////////////////////////////////////
    Method returns the number territories a player has
     *///////////////////////////////////////////////////////////////////////////////*/
    public int numOfTerritories(){
        return territories.size();
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
        homebase.loseInfantry(1);
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns the total number of remaining infantry
     *///////////////////////////////////////////////////////////////////////////////*/
    public int getRemainingArmies(){
        return homebase.getArtilleryCount() + homebase.getCavalryCount() + homebase.getInfantryCount();

    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns the total number of remaining infantry
    *///////////////////////////////////////////////////////////////////////////////*/
    public boolean isPlayerTheWinner(BoardManager bm){
        if (territories.size() == bm.getNumberOfTerritories()){
            return true;
        }
        return false;
    }

    /*///////////////////////////////////////////////////////////////////////////////
    Function to Add armies at the beginning of each turn
     */////////////////////////////////////////////////////////////////////
    public int addArmies() {
        int newArmies = (3 + Math.max(0, (int) Math.ceil((territories.size() - 12) / 3)));
        System.out.println(newArmies + " new armies available");
        homebase.addInfantryCount(newArmies);
        return newArmies;
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

        //bm.transferOccupantsFrom(origin, a, "ARTILLERY");
        //bm.addOccupantsTo(destination, a, "ARTILLERY");

        //bm.transferOccupantsFrom(origin, c, "CAVALRY");
        //bm.addOccupantsTo(destination, c, "CAVALRY");

        bm.transferOccupantsFrom(origin, army, "INFANTRY");
        bm.addOccupantsTo(destination, army, "INFANTRY");

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
