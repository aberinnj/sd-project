import java.util.*;

/*///////////////////////////////////////////////////////////////////////////////
Turn class only serves as a PROXY to GameManager. It DOES NOT copy game status.
TurnManager deeply COPIES Turns

todo: find a way to test functions where there are dice rolls (battle func)
 *//////////////////////////////////////////////////////////////////////////////
public class Turn {
    private List<String> previousTerritories;
    BoardManager BM;
    Player player;
    int turnId;

    // Turn stores all game-board details and events from a specific turn
    Turn(BoardManager BM, Player p, int id) {
        this.previousTerritories = new ArrayList<String>();
        this.BM = BM;
        this.player = p;
        this.turnId = id;

        this.previousTerritories.addAll(p.getTerritories());
    }

    // Run each function
    public void turnFunction(GameManager GM, Scanner scanner) {
        placeNewArmies(GM, scanner);
        attack(GM, scanner);
        fortifyTerritories(GM, scanner);
        earnCards();
        addCredit(GM, scanner);
        purchaseUndo(GM, scanner);
        purchaseCard(GM, scanner);
    }

    // Purchase Credit
    public void addCredit(GameManager GM, Scanner scanner)
    {
        if (GM.baseQuery("Would you like to purchase credit?", scanner)) {
            System.out.println("How much credit would you like to purchase?");
            player.addMoney(Double.parseDouble(scanner.nextLine()));
        }
    }

    // use credit to buy cards or undo opportunities
    public void purchaseUndo(GameManager GM, Scanner scanner)
    {
        if (GM.baseQuery("Would you like to purchase Undo?", scanner)) {
            Double cash = player.getWallet();
            System.out.println("Undos cost 1000 each, you have " + cash + ", how many would you like to purchase?");
            int undos = Integer.parseInt(scanner.nextLine());
            if (undos * 1000 < cash) {
                player.addUndos(undos);
                player.addMoney( undos * 1000 * -1);
            }
        }
    }

    // use credit to buy cards or undo opportunities
    public void purchaseCard(GameManager GM, Scanner scanner)
    {
        if (GM.baseQuery("Would you like to purchase more Cards?", scanner)) {
            Double cash = player.getWallet();
            System.out.println("Cards cost 100 each, you have " + cash + ", how many would you like to purchase?");
            int cards = Integer.parseInt(scanner.nextLine());
            if (cards * 100 < cash) {
                for (int i = 0; i < cards; i++) {
                    Card c = BM.getGameDeck().draw();
                    if(c != null) player.getHand().get(c.getUnit()).push(c);
                }
                player.addMoney( cards * 100 * -1);
            }
        }
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
        Card c;
        if(isPlayerEligibleToEarnCardsThisTurn()) {
            c = BM.getGameDeck().draw();
            if(c != null) player.getHand().get(c.getUnit()).push(c);
        }
    }

    // calculate all free-armies to be received for placing new armies
    public int getFreeArmiesFromTerritories(GameManager GM, Scanner scanner){
        int freebies =  0;
        freebies += Math.max(3, (player.getTerritories().size() - player.getTerritories().size() % 3)/3);
        freebies += player.getContinentsOwned(BM);


        System.out.println("You have the following cards");
        for(Card e: player.getHandListing())
            System.out.println(e.getOrigin() + " - " + e.getUnit());
        if(player.getTotalCards() < 3){
            System.out.println("You do not have enough cards to trade");
        }else if(player.getTotalCards() >= 5) {
            freebies += calculateTradeableCard();
        } else if(GM.baseQuery("Would you like to exchange your cards for units? Yes/ No", scanner)){
            freebies += calculateTradeableCard();
        }

        return freebies;
    }

    /*////////////////////////////////////////////////////////////////////////////////
     Place New Armies from results received in getFreeArmiesFromTerritories
    *///////////////////////////////////////////////////////////////////////////////*/
    public void placeNewArmies(GameManager GM, Scanner scanner) {
        System.out.println("__PLACE NEW ARMIES__");
        int newArmies = getFreeArmiesFromTerritories(GM, scanner);

        player.addArmies(newArmies);
        System.out.println((newArmies) + " new armies available");

        GM.strengthenTerritories(scanner, player.getId());
    }

    // handles calculations of trading-in a set
    public int calculateTradeableCard()
    {
        int new_sum = 0;
        boolean getBonus = false;
        String bonusTo = "";
        // design-combinations
        if (player.getHand().get("INFANTRY").size() >= 3 || player.getHand().get("CAVALRY").size() >= 3 || player.getHand().get("ARTILLERY").size() >= 3 ||
                (player.getHand().get("INFANTRY").size() >= 1 && player.getHand().get("CAVALRY").size() >= 1 && player.getHand().get("ARTILLERY").size() >= 1 ) ||
                (player.getHand().get("WILD").size() >= 1)){
            BM.completeSets++;
            if(BM.completeSets < 6)
                new_sum += (2* BM.completeSets+2);
            else if(BM.completeSets == 6)
                new_sum += (2* BM.completeSets+3);
            else
                new_sum += (15+(BM.completeSets-6)*5);

            // removal of set
            if (player.getHand().get("INFANTRY").size() >= 3) {

                Card e;
                for(int i=0; i<3; i++)
                {
                    e = player.getHand().get("INFANTRY").pop();
                    if (player.getTerritories().contains(e.getOrigin())) {
                        getBonus = true;
                        bonusTo = e.getOrigin();
                    }
                }
            }
            else if (player.getHand().get("CAVALRY").size() >= 3)
            {
                Card e;
                for(int i=0; i<3; i++)
                {
                    e = player.getHand().get("CAVALRY").pop();
                    if (player.getTerritories().contains(e.getOrigin())) {
                        getBonus = true;
                        bonusTo = e.getOrigin();
                    }
                }
            }
            else if (player.getHand().get("ARTILLERY").size() >= 3 )
            {
                Card e;
                for(int i=0; i<3; i++)
                {
                    e = player.getHand().get("ARTILLERY").pop();
                    if (player.getTerritories().contains(e.getOrigin())) {
                        getBonus = true;
                        bonusTo = e.getOrigin();
                    }
                }
            }
            else if (player.getHand().get("INFANTRY").size() >= 1 && player.getHand().get("CAVALRY").size() >= 1 && player.getHand().get("ARTILLERY").size() >= 1 )
            {
                ArrayList<Card> listing = new ArrayList<Card>();
                listing.add(player.getHand().get("INFANTRY").pop());
                listing.add(player.getHand().get("CAVALRY").pop());
                listing.add(player.getHand().get("ARTILLERY").pop());
                for(Card e: listing)
                {
                    if (player.getTerritories().contains(e.getOrigin())) {
                        getBonus = true;
                        bonusTo = e.getOrigin();
                        break;
                    }
                }
            } else {

                Card k;
                ArrayList<Card> listing = new ArrayList<Card>();
                listing.add(player.getHand().get("WILD").pop());
                for(int i=0; i<2; i++)
                {
                    if(player.getHand().get("INFANTRY").size() > 0)
                        listing.add(player.getHand().get("INFANTRY").pop());
                    else if(player.getHand().get("CAVALRY").size() > 0)
                        listing.add(player.getHand().get("CAVALRY").pop());
                    else if(player.getHand().get("ARTILLERY").size() > 0)
                        listing.add(player.getHand().get("ARTILLERY").pop());
                }
                for(Card e: listing)
                {
                    if (player.getTerritories().contains(e.getOrigin())) {
                        getBonus = true;
                        bonusTo = e.getOrigin();
                        break;
                    }
                }
            }
            if (getBonus) {
                System.out.println("One of your cards have been found to represent a territory you're currently occupying." +
                        " +2 Armies will be added to your territory: "+ bonusTo);
                BM.addOccupantsTo(bonusTo, 2);
            }
            return new_sum;
        } else{
            return new_sum;
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Attacking and battle
    *///////////////////////////////////////////////////////////////////////////////*/
    public void attack(GameManager GM, Scanner scanner) {
        System.out.println("__LAUNCH AN ATTACK__");

        while (GM.baseQuery("Would you like to attack? {Yes/No) ", scanner)) {

            System.out.println("List of your territories and enemy territories you can attack:");
            for(String country: BM.getAbleTerritories(player, true))
            {
                System.out.println(country + ": " + BM.getOccupantCount(country) + " armies, CAN ATTACK");
                for(String enemy: BM.getAllAdjacentEnemyTerritories(player.getId(), country))
                {
                    System.out.println("\t"+enemy + ", " + BM.getOccupantCount(enemy) +" enemy armies");
                }
            }

            String origin;
            String territory;
            int attackerDice;
            int defenderDice;

            do{
                origin = BM.queryTerritory(scanner, "From: ", "ATTACK_FROM", player, "");
            } while(origin == null);
            do{
                territory = BM.queryTerritory(scanner, "Attack: ", "ATTACK", player, origin);
            } while(territory == null);
            BM.getBoardMap().get(territory).setStatusToUnderAttack();
            do{
                attackerDice = BM.queryCount(scanner, "Attacker (Player "+ player.getId()+") rolls: ", "ATTACK", player, origin);
            } while(attackerDice == 0);
            do{
                defenderDice = BM.queryCount(scanner, "Defender of " + territory + "(Player " + BM.getBoardMap().get(territory).getOccupantID() + ") rolls: ", "DEFEND", player, territory);
            } while(defenderDice == 0);

            // setup
            Dice dice = new Dice();
            ArrayList<Integer> attacker_dice;
            ArrayList<Integer> defender_dice;
            attacker_dice = new ArrayList<Integer>();
            for (int i = 0; i < attackerDice; i++) {
                dice.roll();
                attacker_dice.add(dice.getDiceValue());
            }
            defender_dice = new ArrayList<Integer>();
            for (int i = 0; i < defenderDice; i++) {
                dice.roll();
                defender_dice.add(dice.getDiceValue());
            }

            // Deciding battles
            battle(GM, attackerDice, territory, origin, attacker_dice, defender_dice);
        }
    }

    // Battle function
    public void battle(GameManager GM, int attackerDice, String defending, String attacking, ArrayList<Integer> attacker_dice, ArrayList<Integer> defender_dice){

        int topIndexAttacker;
        int topIndexDefender;
        int potentialTransfer = attackerDice;

        do {
            topIndexAttacker = GM.getIndexOfHighestRollIn(attacker_dice, attacker_dice.size());
            topIndexDefender = GM.getIndexOfHighestRollIn(defender_dice, defender_dice.size());

            System.out.println("Attacker: "+attacker_dice.get(topIndexAttacker) + " vs. Defender: " + defender_dice.get(topIndexDefender));

            // Attack CASES
            if(attacker_dice.get(topIndexAttacker) > defender_dice.get(topIndexDefender)) {
                System.out.println("\tDefender loses one army.");
                BM.removeOccupantsFrom(defending, 1);

                if(BM.getOccupantCount(defending) == 0) {
                    BM.getBoardMap().get(defending).setStatusToFallen();
                    System.out.println("The ATTACK is a success! " + defending + " is captured.");
                    GM.getPlayer(BM.getBoardMap().get(defending).getOccupantID()).loseTerritories(defending);
                    BM.getBoardMap().get(attacking).loseOccupants(potentialTransfer, ArmyType.INFANTRY);
                    GM.getPlayer(player.getId()).addArmies(potentialTransfer);
                    BM.initializeTerritory(player, defending, potentialTransfer);

                    break;
                }
            }
            else if(attacker_dice.get(topIndexAttacker).equals(defender_dice.get(topIndexDefender))) {
                System.out.println("\tAttacker loses one army.");
                BM.removeOccupantsFrom(attacking, 1);
                potentialTransfer--;

                if(BM.getOccupantCount(attacking) == 1) {
                    System.out.println("The ATTACK fails with no remaining army left. " + attacking + " can no longer attack. ");
                    break;
                }
            }
            else if(attacker_dice.get(topIndexAttacker) < defender_dice.get(topIndexDefender)) {
                System.out.println("\tAttacker loses one army.");
                BM.removeOccupantsFrom(attacking, 1);
                potentialTransfer--;

                if(BM.getOccupantCount(attacking) == 1) {
                    System.out.println("The ATTACK fails with no remaining army left. "+ attacking + " can no longer attack. ");
                    break;
                }
            }

            // takes other pairs in mind (next highest)
            attacker_dice.remove(topIndexAttacker);
            defender_dice.remove(topIndexDefender);

            // runs roll comparison again
            if(attacker_dice.size() == 0 || defender_dice.size() == 0) break;

        } while(true);
        BM.getBoardMap().get(defending).setStatusToNormal();

    }


    /*////////////////////////////////////////////////////////////////////////////////
    Fortifying is simple
    *///////////////////////////////////////////////////////////////////////////////*/
    public void fortifyTerritories(GameManager GM, Scanner scanner)
    {
        String origin;
        String destination;
        int transfer;
        System.out.println("__FORTIFY TERRITORIES__");
        if(GM.baseQuery("Would you like to fortify your territories?", scanner))
        {
            for(String country: BM.getAbleTerritories(player, false)) {
                System.out.println(country + ": " + BM.getOccupantCount(country) + " armies");
            }

            do{
                origin = BM.queryTerritory(scanner, "From: ", "FORTIFY_FROM", player, "");
            }while(origin == null);

            System.out.println("Territories fortify-able from " + origin);
            for(String country: BM.getAllAdjacentTerritories(player.getId(), origin)) {
                System.out.println(country);
            }

            do{
                destination = BM.queryTerritory(scanner, "Fortify: ",
                        "FORTIFY", player, origin);
            }while(destination == null);

            do{
                transfer = BM.queryCount(scanner, "Transfer: ", "FORTIFY", player, origin);
            } while(transfer == 0);

            BM.fortifyTerritory(origin, destination, transfer);
        }


    }
}
