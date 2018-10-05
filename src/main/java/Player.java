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
        System.out.println("Infantry/Cavalry/Artillery Count And Player #"+id+" Territories");

        for(String country: territories){
            System.out.print(bm.getOccupantCount(country) + " ");
            System.out.println(country);
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method displays Player's adjacent territories to given territory argument
     *///////////////////////////////////////////////////////////////////////////////*/
    public void displayPlayerNeighboringTerritories(BoardManager bm, String origin, boolean attacking) {
        System.out.println("Infantry/Cavalry/Artillery Count And Neighboring Countries");
        for(String country: territories){
            if (attacking) {
                System.out.println(country + " Can Attack");
                System.out.println("Army count: " + bm.getOccupantCount(country));
            }
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
    public int getRemainingArmies(){ return homebase.getArtilleryCount() + homebase.getCavalryCount() + homebase.getInfantryCount(); }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns the total number of remaining infantry
    *///////////////////////////////////////////////////////////////////////////////*/
    public boolean isPlayerTheWinner(BoardManager bm){
        return territories.size() == bm.getNumberOfTerritories();
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method checks the number of continents a player owns and designates more armies
    *///////////////////////////////////////////////////////////////////////////////*/
    private int continentsOwned(BoardManager bm){
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

    /*///////////////////////////////////////////////////////////////////////////////
    Function to Add armies at the beginning of each turn
     */////////////////////////////////////////////////////////////////////
    public void addArmies(BoardManager bm, Scanner scanner) {
        int newArmies = (3 + Math.max(0, (int) Math.ceil((territories.size() - 12) / 3)));
        newArmies = newArmies + continentsOwned(bm);
        System.out.println(newArmies + " new armies available");

        if (Hand.size() < 3) System.out.println("You currently do not have enough cards to make an exchange.");

        else newArmies += tradeCards(scanner);

        homebase.addInfantryCount(newArmies);
        shipArmies(bm, scanner);
        //return newArmies;
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

    public void fortifyTerritory(BoardManager bm, String origin, String destination, int army) {


        bm.transferOccupantsFrom(origin, army, "INFANTRY");
        bm.addOccupantsTo(destination, army, "INFANTRY");

    }

    public List<String> getTerritories(){
            return territories;
        }
        /*////////////////////////////////////////////////////////////////////////////////
        Method returns the number territories a player has
         *///////////////////////////////////////////////////////////////////////////////*/
        public int numOfTerritories(){
            return territories.size();
        }


    public void displayHand() {
        System.out.println("You have the current cards");
        for (int i = 0; i < Hand.size();i++) {
            System.out.println(Hand.get(i));
        }
    }

    public int tradeCards(Scanner scanner) {
        displayHand();
        String ans;

        do{
            System.out.println("Would you like to exchange your cards for units? Yes/ No");
            ans = scanner.nextLine();
        }while (!ans.equals("Yes") && !ans.equals("No"));
        //if (ans == "No") return 0;
        //else {

        //}
        return 0;
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
