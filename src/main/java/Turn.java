import java.util.*;

public class Turn {

    NewGame game = null;
    BoardManager bm = null;
    Player player = null;
    Scanner scanner = null;
    List<String> territories = null;
    ArrayList<Map.Entry<String, String>> hand = null;

    Turn(NewGame ng, BoardManager bm, Player playerCurrent, Scanner scanner) {
        this.game = ng;
        this.bm = bm;

        this.player = playerCurrent;
        this.territories = playerCurrent.getTerritories();
        this.hand = playerCurrent.getHand();

        this.scanner = scanner;
        turnFunciton();
    }

    public void turnFunciton() {
        // 1. place new Armies
        addArmies();
        // 2. attacking
        //attack(bm, setup);
        // 3. fortifying position
        fortifyPlayersTerritory();
    }

    /*///////////////////////////////////////////////////////////////////////////////
    Function to Add armies at the beginning of each turn
     */////////////////////////////////////////////////////////////////////
    public void addArmies() {

        int newArmies = (3 + Math.max(0, (int) Math.ceil((territories.size() - 12) / 3)));
        newArmies = newArmies + continentsOwned(bm);
        System.out.println(newArmies + " new armies available");

        if (hand.size() < 3) System.out.println("You currently do not have enough cards to make an exchange.");

        else newArmies += tradeCards();

        player.addArmies(newArmies);
        player.shipArmies(bm, scanner);
        //return newArmies;
    }

    public int tradeCards() {

        int armies = 0;
        System.out.println("You have the following cards");
        player.displayHand();
        String ans;

        do{
            System.out.println("Would you like to exchange your cards for units? Yes/ No");
            ans = scanner.nextLine();
        }while (!ans.equals("Yes") && !ans.equals("No"));
        //if (ans == "No") return 0;
        //else {

        //}
        return armies;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method below executes the third action a player can do in their turn.
    Method calls fortify, showTerritories functions for player
    Method requires index or Player Id as an argument, BoardManager passed

    Refactor. Add Exception to transfer count.
    *///////////////////////////////////////////////////////////////////////////////*/
    public void fortifyPlayersTerritory()
    {
        Scanner scan = new Scanner(System.in);
        System.out.println("------------------------");
        System.out.println("Fortify your territories");
        System.out.println("by moving armies from one");
        System.out.println("territory to another. ");
        System.out.println("------------------------");
        boolean invalid = false;
        do {
            try {
                player.displayPlayerTerritories(bm);

                System.out.print("\nMoveFrom: ");
                String origin = scan.nextLine();
                System.out.println();
                if(player.ifPlayerHasTerritory(origin)){
                    if (bm.getOccupantCount(origin) <= 1) {
                        throw new Exception("Uh Oh! This territory only has one army. You cannot transfer the defending army of a territory.");
                    }
                    System.out.println("Neighboring Territories: ");
                    player.displayPlayerNeighboringTerritories(bm, origin, false);
                    System.out.println("Army Count: " + bm.getOccupantCount(origin));
                } else
                    throw new Exception("Player does not own territory " + origin);


                int army_count = queryTransferCount();
                if(bm.getOccupantCount(origin) > (army_count))
                {
                    System.out.print("\nMoveTo: ");
                    String destination = scan.nextLine();
                    System.out.println();
                    if(player.ifPlayerHasTerritory(destination)){
                        if(!bm.isTerritoryANeighborOf(destination, origin)) {
                            throw new Exception("Uh Oh! The given destination territory is not adjacent to the given origin " + origin);
                        }
                        player.fortifyTerritory(bm, origin, destination, army_count);
                    } else
                        throw new Exception("Player does not own territory " + destination);

                }else
                    throw new Exception("Uh Oh! This territory does not have the given amount of armies to transfer");

                invalid = false;
            } catch (InputMismatchException k){
                System.out.println("Error: Invalid input.");
                invalid = true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                invalid = true;
            }
        }while(invalid);
    }

    public void attack(BoardManager bm, Scanner scanner) {
        for (String territory: territories) {
            player.displayPlayerNeighboringTerritories(bm, territory, true);
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
                player.displayPlayerNeighboringTerritories(bm, origin, true);
                System.out.println("Please select the territory you would like to attack.");
                String defender = scanner.nextLine();
                if (bm.isTerritoryANeighborOf(origin, defender) && !player.ifPlayerHasTerritory(defender)) { //Is this a good enough check?

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
                    // Print out the die values
                    System.out.println("Attack Die Values:");
                    for (i = 0; i < numAttackDie; i++) {
                        System.out.print(attackDie[i] + ' ');
                    }
                    System.out.println("\nDefense Die Values:");
                    for (i = 0; i < numDefenseDie; i++) {
                        System.out.print(defenseDie[i] + ' ');
                    }
                    System.out.print("\n");

                    // Compare Die
                    int attackLoss = 0;
                    int defenseLoss = 0;
                    for (i = 0; i < Math.min(numDefenseDie, numAttackDie); i++) {
                        if (defenseDie[i] < attackDie[i]) defenseLoss += 1;
                        else attackLoss += 1;
                    }

                    // Remove armies from attacker and defender
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
                        Map.Entry<String, String> card = Deck.drawCard();
                        System.out.println("Your card is " + card);
                        hand.add(card);
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

    /*////////////////////////////////////////////////////////////////////////////////
    Helper method for querying player for number of army to transfer
    *///////////////////////////////////////////////////////////////////////////////*/
    public static int queryTransferCount(){
        Scanner intScanner = new Scanner(System.in);
        System.out.print("\nTransfer army: ");
        int army_count = intScanner.nextInt();
        System.out.println();
        return army_count;
    }
}
