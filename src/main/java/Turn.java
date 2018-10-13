import java.util.*;

/*///////////////////////////////////////////////////////////////////////////////
Turn class only serves as a PROXY to GameManager. It DOES NOT copy game status.
TurnManager deeply COPIES Turns

todo: make move/undo functions better
todo: (bug) fortify does not allow to user skip
todo: implement function to move armies onto captured territories
 *//////////////////////////////////////////////////////////////////////////////
public class Turn {
    List<String> previousTerritories;
    BoardManager bm;
    Player player;
    int turnId;

    // Turn stores all game-board details and events from a specific turn
    Turn(BoardManager bm, Player p, int id) {
        this.previousTerritories = new ArrayList<String>();
        this.bm = bm;
        this.player = p;
        this.turnId = id;

        this.previousTerritories.addAll(p.getTerritories());
    }

    public boolean baseQuery(String query, Scanner scanner)
    {
        String res;
        do {
            System.out.println(query);
            res = scanner.nextLine();
        }while(!res.toLowerCase().equals("Y") && !res.toLowerCase().equals("yes") && !res.toLowerCase().equals("N") && !res.toLowerCase().equals("no"));

        return (res.toLowerCase().equals("Y") || res.toLowerCase().equals("yes"));
    }


    // Run each function
    public void turnFunction(Scanner scanner) {
        placeNewArmies(scanner);
        attack(scanner);
        fortifyPlayersTerritory(scanner);
        earnCards();
    }

    // Passes if a new territory is added to player's territories
    // Fails if there's no new territory added to player's territories
    public boolean isPlayerEligibleToEarnCardsThisTurn() {
        for(String k : player.getTerritories()) {
            if( !previousTerritories.contains(k)) {
                System.out.println("Player is eligible to earn a card for claiming a new Territory: claimed "+ k +".");
                return true;
            }
        }
        return false;
    }

    // earnCards draw a card and adds the card to the Player's hand
    public void earnCards(){
        if(isPlayerEligibleToEarnCardsThisTurn())
            player.addToHand((bm.getGameDeck().draw()));
    }


    public int getFreeArmiesFromTerritoriesAndCards(Scanner scanner){
        int freebies =  0;
        freebies += Math.max(3, (player.getTerritories().size() - player.getTerritories().size() % 3)/3);
        freebies += player.continentsOwned(bm);
        if(player.getHand().size() < 3){
            System.out.println("You do not have enough cards to trade");
        }else {
            freebies += queryTrade(scanner);
        }
        return freebies;
    }

    // Place New Armies from results received in getFreeArmiesFromTerritories
    public void placeNewArmies(Scanner scanner) {

        int newArmies = getFreeArmiesFromTerritoriesAndCards(scanner);
        System.out.println(newArmies + " new armies available");
        player.addArmies(newArmies);
        player.deployInfantry(bm, scanner);
    }

    // handles calculations of trading-in a set
    public int calculateTradeableCard()
    {
        int new_sum = 0;
        // design-combinations
        if ((player.getHandDesigns().contains("INFANTRY") && player.getHandDesigns().contains("INFANTRY") && player.getHandDesigns().contains("INFANTRY")) ||
                (player.getHandDesigns().contains("CAVALRY") && player.getHandDesigns().contains("CAVALRY") && player.getHandDesigns().contains("CAVALRY")) ||
                (player.getHandDesigns().contains("ARTILLERY") && player.getHandDesigns().contains("ARTILLERY") && player.getHandDesigns().contains("ARTILLERY")) ||
                (player.getHandDesigns().contains("INFANTRY") && player.getHandDesigns().contains("CAVALRY") && player.getHandDesigns().contains("ARTILLERY")) ||
                (player.getHandDesigns().contains("WILD"))){
            bm.completeSets++;
            if(bm.completeSets < 6)
                new_sum += (2*bm.completeSets+2);
            else if(bm.completeSets == 6)
                new_sum += (2*bm.completeSets+3);
            else
                new_sum += (15+(bm.completeSets-6)*5);


            // removal of set
            if (player.getHandDesigns().contains("INFANTRY") && player.getHandDesigns().contains("INFANTRY") && player.getHandDesigns().contains("INFANTRY"))
            {
                int i = player.getHandDesigns().indexOf("INFANTRY");
                player.getHandDesigns().remove(i);
                player.getHand().remove(i);
                i = player.getHandDesigns().indexOf("INFANTRY");
                player.getHandDesigns().remove(i);
                player.getHand().remove(i);
                i = player.getHandDesigns().indexOf("INFANTRY");
                player.getHandDesigns().remove(i);
                player.getHand().remove(i);
            } else if (player.getHandDesigns().contains("CAVALRY") && player.getHandDesigns().contains("CAVALRY") && player.getHandDesigns().contains("CAVALRY"))
            {
                int i = player.getHandDesigns().indexOf("CAVALRY");
                player.getHandDesigns().remove(i);
                player.getHand().remove(i);
                i = player.getHandDesigns().indexOf("CAVALRY");
                player.getHandDesigns().remove(i);
                player.getHand().remove(i);
                i = player.getHandDesigns().indexOf("CAVALRY");
                player.getHandDesigns().remove(i);
                player.getHand().remove(i);
            } else if (player.getHandDesigns().contains("ARTILLERY") && player.getHandDesigns().contains("ARTILLERY") && player.getHandDesigns().contains("ARTILLERY"))
            {
                int i = player.getHandDesigns().indexOf("ARTILLERY");
                player.getHandDesigns().remove(i);
                player.getHand().remove(i);
                i = player.getHandDesigns().indexOf("ARTILLERY");
                player.getHandDesigns().remove(i);
                player.getHand().remove(i);
                i = player.getHandDesigns().indexOf("ARTILLERY");
                player.getHandDesigns().remove(i);
                player.getHand().remove(i);
            } else if (player.getHandDesigns().contains("INFANTRY") && player.getHandDesigns().contains("CAVALRY") && player.getHandDesigns().contains("ARTILLERY"))
            {
                int i = player.getHandDesigns().indexOf("INFANTRY");
                player.getHandDesigns().remove(i);
                player.getHand().remove(i);
                i = player.getHandDesigns().indexOf("CAVALRY");
                player.getHandDesigns().remove(i);
                player.getHand().remove(i);
                i = player.getHandDesigns().indexOf("ARTILLERY");
                player.getHandDesigns().remove(i);
                player.getHand().remove(i);
            } else {
                int i = player.getHandDesigns().indexOf("WILD");
                player.getHandDesigns().remove(i);
                player.getHand().remove(i);
                for(int k=0; k<2; k++) {
                    if (!player.getHand().get(0).getUnit().equals("WILD")) {
                        player.getHandDesigns().remove(0);
                        player.getHand().remove(0);
                    } else {
                        player.getHandDesigns().remove(1);
                        player.getHand().remove(1);
                    }
                }
            }
            System.out.println(new_sum);
            return new_sum;
        } else{
            return new_sum;
        }
    }

    // queries for trading
    public int queryTrade(Scanner scanner) {
        System.out.println("You have the following cards");
        int sum = 0;
        for(Card e: player.getHand())
            System.out.println(e.getOrigin() + " - " + e.getUnit());
        if(baseQuery("Would you like to exchange your cards for units? Yes/ No", scanner))
        {
            for(Card k: player.getHand())
            {
                // assign 2 armies to territory right away
                if (player.getTerritories().contains(k.getOrigin()))
                {
                    System.out.println("One of your cards have been found to represent a territory you're currently occupying." +
                            " +2 Armies will be added to your territory: "+ k.getOrigin());
                    bm.addOccupantsTo(k.getOrigin(), 2, "INFANTRY");
                    break;
                }
            }
            sum += calculateTradeableCard();

            return sum;
        } else
            return sum;
    }

    public String getOriginFortify(Scanner scanner) throws Exception {
        System.out.println("__________________________________________");
        System.out.println("Infantry Count And Player #"+player.getId()+" Territories");
        for (String i: bm.getTerritories(player, false)) {System.out.println(bm.getOccupantCount(i) + " " + i);}

        System.out.print("\nMoveFrom: ");
        String origin = scanner.nextLine();
        System.out.println();
        if(player.ifPlayerHasTerritory(origin)){
            if (bm.getOccupantCount(origin) <= 1) {
                throw new Exception("Uh Oh! This territory only has one army. You cannot transfer the defending army of a territory.");
            }
            for(String i: bm.getNeighborsOf(origin)) {
                System.out.println(i);
            }
            System.out.println("Army Count: " + bm.getOccupantCount(origin));
        } else
            throw new Exception("Player does not own territory " + origin);
        return origin;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Helper method for querying player for number of army to transfer
    *///////////////////////////////////////////////////////////////////////////////*/
    public void queryTransfer(String origin, Scanner scanner) throws Exception {
        Scanner intScanner = new Scanner(System.in);
        System.out.print("\nTransfer army: ");
        int army_count = intScanner.nextInt();
        System.out.println();

        if(bm.getOccupantCount(origin) > (army_count))
        {
            System.out.print("\nMoveTo: ");
            String destination = scanner.nextLine();
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
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method below executes the third action a player can do in their turn.
    Method calls fortify, showTerritories functions for player
    Method requires index or Player Id as an argument, BoardManager passed

    Refactor. Add Exception to transfer count.
    *///////////////////////////////////////////////////////////////////////////////*/
    public void fortifyPlayersTerritory(Scanner scanner)
    {
        System.out.println("Would you like to fortify your territories?");
        String attack = scanner.nextLine();
        if (attack.equals("No")) {
            System.out.println("You have chosen not to fortify");
            return;
        }

        System.out.println("------------------------");
        System.out.println("Fortify your territories");
        System.out.println("by moving armies from one");
        System.out.println("territory to another. ");
        System.out.println("------------------------");

        boolean invalid = false;
        do {
            try {
                String origin = getOriginFortify(scanner);

                queryTransfer(origin, scanner);

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

    public String getAttacker(Scanner scanner) {
        boolean noTerritoryChosen = false;
        String attacker = null;
        while (!noTerritoryChosen) {
            System.out.println("You have chosen to attack. Please select the territory you would like to attack with.");
            attacker = scanner.nextLine();
            if (bm.getOccupantCount(attacker) < 2 || !player.ifPlayerHasTerritory(attacker)) {
                System.out.println("That territory can not attack.");
            }
            else noTerritoryChosen = true;
        }
        return attacker;
    }

    public int getAttackArmies(Scanner scanner, String attacker) {
        boolean attackCheck = false;
        int armiesToAttackWith = 0;
        while (!attackCheck) {  //check to ensure the number of attacking armies is proper
            System.out.println("How many armies would you like to attack with?");
            armiesToAttackWith = Integer.parseInt(scanner.nextLine());
            if (armiesToAttackWith > bm.getOccupantCount(attacker) || armiesToAttackWith < 1 || armiesToAttackWith > 4) {
                System.out.println("Please enter a valid number of armies");
                armiesToAttackWith = Integer.parseInt(scanner.nextLine());
            } else attackCheck = true;
        }
        return armiesToAttackWith;
    }


    public int getNumAttackDie(int armiesToAttackWith) {
        return   Math.min(armiesToAttackWith - 1, 3);
    }

    public String getDefender(Scanner scanner, String attacker) {
        System.out.println("Please select the territory you would like to attack.");
        boolean hasDefender = false;
        String defender = scanner.nextLine();
        while (!hasDefender) {
            if (bm.isTerritoryANeighborOf(attacker, defender) && !player.ifPlayerHasTerritory(defender)) {
                hasDefender = true;
            }
            else {
                System.out.println("That territory can not be attacked\n Please select again.");
                defender = scanner.nextLine();
            }
        }
        return defender;
    }

    public int getDefenseArmies(Scanner scanner, String defender) {
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
        return armiesToDefendWith;
    }

    public int getNumDefenseDie(Scanner scanner, int armiesToDefendWith) {
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
        return numDefenseDie;
    }

    public void totalDefenseLoss(String attacker, String defender) {
        bm.transferOwnership(attacker, defender);

        //hand.add(card); //worry about cards later
    }

    public int[] getDieValues(int numDie, Dice dice) {
        int[] Die = new int[numDie];
        for (int i = 0; i < numDie; i++) {
            dice.roll();
            Die[i] = dice.getDiceValue();
        }
        Arrays.sort(Die);
        for(int i=0; i<Die.length/2; i++){
            int temp = Die[i];
            Die[i] = Die[Die.length -i -1];
            Die[Die.length -i -1] = temp;
        }
        return Die;
    }

    public void attack(Scanner scanner) {

        while (true) {
            System.out.println("Infantry Count And Neighboring Countries");
            bm.getAllAdjacentEnemyTerritories(player);
            System.out.println("Would you like to attack? Please enter Yes or No");
            String attack = scanner.nextLine();
            if (attack.equals("No")) {
                System.out.println("You have chosen not to attack");
                return;
            }

            String attacker = getAttacker(scanner);
            List<String> targets = bm.getEnemyNeighbors(attacker);
            for (String target: targets){
                System.out.println(target);
            }
            String defender = getDefender(scanner, attacker);

            int attackArmies = getAttackArmies(scanner, attacker);

            int numAttackDie = getNumAttackDie(attackArmies);

            int defenseArmies = getDefenseArmies(scanner, defender);

            int numDefenseDie = getNumDefenseDie(scanner, defenseArmies);

            Dice dice = new Dice();
            int[] defenseDie = getDieValues(numDefenseDie, dice);
            int[] attackDie = getDieValues(numAttackDie, dice);

            // Print out the die values
            System.out.println("Attack Die Values:");
            for (int i = 0; i < numAttackDie; i++) {
                System.out.print(attackDie[i]);
                System.out.print(' ');
            }
            System.out.println("\nDefense Die Values:");
            for (int i = 0; i < numDefenseDie; i++) {
                System.out.print(defenseDie[i]);
                System.out.print(' ');
            }
            System.out.print("\n");

            // Compare Die
            int attackLoss = 0;
            int defenseLoss = 0;
            for (int i = 0; i < Math.min(numDefenseDie, numAttackDie); i++) {
                if (defenseDie[i] < attackDie[i]) defenseLoss += 1;
                else attackLoss += 1;
            }

            // Remove armies from attacker and defender
            try {
                bm.removeOccupantsFrom(attacker, attackLoss, "INFANTRY");
                bm.removeOccupantsFrom(defender, defenseLoss, "INFANTRY");
            } catch (Exception e) {
                e.printStackTrace();
            }

            //If defender lost all armies
            if (bm.getOccupantCount(defender) == 0) {
                totalDefenseLoss(attacker, defender);
            }
        }
    }
}
