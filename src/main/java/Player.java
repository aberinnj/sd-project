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
        for(String i: k)
        {
            if (!territories.contains(i))
                territories.add(i);

        }
        for(String i: territories)
        {
            if(!k.contains(i))
                territories.remove(i);
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

    public void attack(BoardManager bm, Scanner scanner) {
        for (String territory: territories) {
            displayPlayerNeighboringTerritories(bm, territory, true);
        }
        System.out.println("Would you like to attack? Please enter Yes or No");
        String attack = scanner.nextLine();
        if (attack == "Yes") {
            boolean attacking = true;
            while (attacking) {
                boolean noTerritoryChosen = false;
                String origin = "";
                while (!noTerritoryChosen) {
                    System.out.println("You have chosen to attack. Please select the territory you would like to attack with.");
                    origin = scanner.nextLine();
                    if (bm.getOccupantCount(origin) < 2) {
                        System.out.println("That territory does not have enough armies to attack.");
                        System.out.println("Please select a territory with at least two armies");
                    }
                    else noTerritoryChosen = true;
                }
                displayPlayerNeighboringTerritories(bm, origin, true);
                System.out.println("Please select the territory you would like to attack.");
                String defender = scanner.nextLine();
                if (bm.isTerritoryANeighborOf(origin, defender) && !ifPlayerHasTerritory(defender)) { //Is this a good enough check?

                    boolean attackCheck = false;
                    int armiesToAttackWith = Integer.parseInt(null);
                    while (!attackCheck) {  //check to ensure the number of attacking armies is proper
                        System.out.println("How many armies would you like to attack with?");
                        armiesToAttackWith = Integer.parseInt(scanner.nextLine());
                        if (armiesToAttackWith > bm.getOccupantCount(origin) || armiesToAttackWith < 1 || armiesToAttackWith > 4) {
                            System.out.println("Please enter a valid number of armies");
                            armiesToAttackWith = Integer.parseInt(scanner.nextLine());
                        }
                        else attackCheck = true;
                    }

                    int possibleAttackDie = Math.min(armiesToAttackWith - 1, 3);
                    System.out.println("You may use up to " + possibleAttackDie + ", how many would you like to use?");
                    int numAttackDie = Integer.parseInt(scanner.nextLine()); //need check to ensure the proper amount of die

                    boolean defenseCheck = false;
                    int armiesToDefendWith = 0;
                    while (!defenseCheck) {
                        System.out.println("How many armies would you like to defend with?");
                        armiesToDefendWith = Integer.parseInt(scanner.nextLine());
                        if (armiesToDefendWith > bm.getOccupantCount(defender) || armiesToDefendWith < 1 || armiesToDefendWith > 2) {
                            System.out.println("Please enter a valid number of armies");
                            armiesToDefendWith = Integer.parseInt(scanner.nextLine());
                        }
                        else defenseCheck = true;
                    }

                    //int possibleDefenseDie = math.min(armiesToDefendWith, 3);
                    int numDefenseDie;
                    if (armiesToDefendWith == 2) {
                        System.out.println("You may use 1 or 2 armies, how many would you like to use?");
                        numDefenseDie = Integer.parseInt(scanner.nextLine()); //need check to ensure the proper amount of die
                    }
                    else {
                        System.out.println("You are defending with one army");
                        numDefenseDie = 1;
                    }

                    int[] attackDie = new int[numAttackDie];
                    int[] defenseDie = new int[numDefenseDie];

                    Dice dice = new Dice();
                    int i, j;
                    for (i = 0; i < numAttackDie; i++) {
                        dice.roll();
                        attackDie[i] = dice.getDiceValue();
                    }

                    Arrays.sort(attackDie);

                    for (i = 0; i < numDefenseDie; i++) {
                        dice.roll();
                        defenseDie[i] = dice.getDiceValue();
                    }

                    Arrays.sort(defenseDie);
                    /* Print out the die values */
                    System.out.println("Attack Die Values:");
                    for (i = 0; i < numAttackDie; i++) {
                        System.out.print(attackDie[i] + ' ');
                    }
                    System.out.println("\nDefense Die Values:");
                    for (i = 0; i < numDefenseDie; i++) {
                        System.out.print(defenseDie[i] + ' ');
                    }
                    System.out.print("\n");

                    /* Compare Die */
                    int attackLoss = 0;
                    int defenseLoss = 0;
                    for (i = 0; i < Math.min(numDefenseDie, numAttackDie); i++) {
                        if (defenseDie[i] < attackDie[i]) defenseLoss += 1;
                        else attackLoss += 1;
                    }

                    /* Remove armies from attacker and defender */
                    try {
                        bm.removeOccupantsFrom(origin, attackLoss, "INFANTRY");
                        bm.removeOccupantsFrom(defender, defenseLoss, "INFANTRY");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //If defender lost all armies
                    if (bm.getOccupantCount(defender) == 0) {
                        bm.transferOwnership(origin, defender);
                        System.out.println("You won a territory, you get to draw a card");
                        Entry<String, String> card = Deck.drawCard();
                        System.out.println("Your card is " + card);
                        Hand.add(card);
                    }

                    System.out.println("Would you like to attack again? Yes/No");
                    String ans = scanner.nextLine();
                    if (ans == "No") {
                        attacking = false;
                    }
                }
            }
        }
        else System.out.println("You have chosen not to attack");
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Methods returns a copy of the territories list
    *///////////////////////////////////////////////////////////////////////////////*/
        public List<String> territoriesCopy(){
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
