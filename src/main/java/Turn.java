
import java.util.*;


/*///////////////////////////////////////////////////////////////////////////////
Turn Class
todo: make move/undo functions better
 *//////////////////////////////////////////////////////////////////////////////
public class Turn {
    BoardManager bm;
    Player player;
    ArrayList<Map.Entry<String, String>> hand;

    // Turn stores all game-board details and events from a specific turn
    Turn(BoardManager bm, Player p) {
        this.bm = bm;
        this.player = p;
        this.hand = p.getHand();
    }

    public void turnFunction(Scanner scanner) {
        placeNewArmies(scanner);
        attack(scanner);
        fortifyPlayersTerritory(scanner);
    }



    public void placeNewArmies(Scanner scanner) {

        int newArmies = (3 + Math.max(0, (int) Math.ceil((player.getTerritories().size() - 12) / 3.0)));
        newArmies = newArmies + player.continentsOwned(bm);
        System.out.println(newArmies + " new armies available");

        if (hand.size() < 3) System.out.println("You currently do not have enough cards to make an exchange.");

        else newArmies += tradeCards(scanner);

        player.addArmies(newArmies);
        player.deployInfantry(bm, scanner);
        //return newArmies;
    }

    public int tradeCards(Scanner scanner) {

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

    public String getOriginFortify(Scanner scanner) throws Exception {
        player.displayPlayerTerritories(bm);

        System.out.print("\nMoveFrom: ");
        String origin = scanner.nextLine();
        System.out.println();
        if(player.ifPlayerHasTerritory(origin)){
            if (bm.getOccupantCount(origin) <= 1) {
                throw new Exception("Uh Oh! This territory only has one army. You cannot transfer the defending army of a territory.");
            }
            player.displayPlayerNeighboringTerritories(bm, origin);
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
        int numAttackDie = Math.min(armiesToAttackWith - 1, 3);
        return numAttackDie;
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
        System.out.println("You won a territory, you get to draw a card");
        Map.Entry<String, String> card = Deck.drawCard();
        System.out.println("Your card is " + card);
        //implement function to move armies onto captured territories
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
            player.displayAttackableNeighboringTerritories(bm);
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
