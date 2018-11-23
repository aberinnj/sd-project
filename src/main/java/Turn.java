import java.util.*;

/*///////////////////////////////////////////////////////////////////////////////
Turn class only serves as a PROXY to GameManager. It DOES NOT copy game status.
TurnManager deeply COPIES Turns

todo: find a way to test functions where there are dice rolls (battle func)
 *//////////////////////////////////////////////////////////////////////////////
public class Turn {
    List<String> previousTerritories;
    BoardManager BM;
    Player player;
    int turnId;
    Messenger messenger;
    GameManager GM;

    // Turn stores all game-board details and events from a specific turn
    Turn(BoardManager BM, Player p, int id) {
        this.previousTerritories = new ArrayList<String>();
        this.BM = BM;
        this.player = p;
        this.turnId = id;
        this.previousTerritories.addAll(p.getTerritories());
    }

    // Run each function
    public void turnFunction(GameManager GM, Messenger messenger, Game thisGame) throws InterruptedException {
        this.messenger = messenger;
        this.GM = GM;
        //placeNewArmies(thisGame);
        //attack(thisGame);
        //fortifyTerritories(thisGame);
        //earnCards();
        //addCredit();
        //purchaseUndo();
        //purchaseCard();
    }

    // Purchase Credit
    public void addCredit() throws InterruptedException {
        if (GM.baseQuery("Would you like to purchase credit?")) {
            //System.out.println("How much credit would you like to purchase?");
            //player.addMoney(Double.parseDouble(scanner.nextLine()));
            messenger.putMessage("How much credit would you like to purchase?");
            player.addMoney(Double.parseDouble(messenger.getMessage()));
        }
    }


    // use credit to buy cards or undo opportunities
    public void purchaseUndo() throws InterruptedException {
        if (GM.baseQuery("Would you like to purchase Undo?")) {
            Double cash = player.getWallet();
            // System.out.println("Undos cost 1000 each, you have " + cash + ", how many would you like to purchase?");
            messenger.putMessage("Undos cost 1000 each, you have " + cash + ", how many would you like to purchase?");
            int undos = Integer.parseInt(messenger.getMessage());
            if (undos * 1000 < cash) {
                player.addUndos(undos);
                player.addMoney( undos * 1000 * -1);
            }
        }
    }

    // use credit to buy cards or undo opportunities
    public void purchaseCard() throws InterruptedException {
        if (GM.baseQuery("Would you like to purchase more Cards?")) {
            Double cash = player.getWallet();
            //System.out.println("Cards cost 100 each, you have " + cash + ", how many would you like to purchase?");
            //int cards = Integer.parseInt(scanner.nextLine());
            messenger.putMessage("Cards cost 100 each, you have " + cash + ", how many would you like to purchase?");
            Double cards = Double.valueOf(messenger.getMessage());
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
    public boolean isPlayerEligibleToEarnCardsThisTurn() throws InterruptedException {
        for(String k : player.getTerritories()) {
            if( !previousTerritories.contains(k)) {
                //System.out.println("Player is eligible to earn a card for claiming a new Territory: claimed "+ k +".");
                messenger.putMessage("Player is eligible to earn a card for claiming a new Territory: claimed "+ k +".");
                return true;
            }
        }
        return false;
    }

    // earnCards draw a card and adds the card to the Player's hand
    public void earnCards() throws InterruptedException {
        Card c;
        if(isPlayerEligibleToEarnCardsThisTurn()) {
            c = BM.getGameDeck().draw();
            if(c != null) player.getHand().get(c.getUnit()).push(c);
        }
    }

    // calculate all free-armies to be received for placing new armies
    public int getFreeArmiesFromTerritories() throws InterruptedException {
        int freebies =  0;
        freebies += Math.max(3, (player.getTerritories().size() - player.getTerritories().size() % 3)/3);
        freebies += player.getContinentsOwned(BM);
        return freebies;
    }

    public int getArmiesFromCards() throws InterruptedException {
        int freebies = 0;
        for(Card e: player.getHandListing())
            //System.out.println(e.getOrigin() + " - " + e.getUnit());
            messenger.putMessage(e.getOrigin() + " - " + e.getUnit());
        if(player.getTotalCards() < 3){
            // System.out.println("You do not have enough cards to trade");
            messenger.putMessage("You do not have enough cards to trade");
        }else if(player.getTotalCards() >= 5) {
            freebies += calculateTradeableCard();
        } else if(GM.baseQuery("Would you like to exchange your cards for units? Yes/ No")){
            freebies += calculateTradeableCard();
        }
        return freebies;
    }

    /*////////////////////////////////////////////////////////////////////////////////
     Place New Armies from results received in getFreeArmiesFromTerritories
    *///////////////////////////////////////////////////////////////////////////////*/
    public void placeNewArmies(Game thisGame) throws InterruptedException {
        // System.out.println("__PLACE NEW ARMIES__");
        messenger.putMessage("__PLACE NEW ARMIES__");
        int newArmies = getFreeArmiesFromTerritories();

        player.addArmies(newArmies);
        //System.out.println((newArmies) + " new armies available");
        messenger.putMessage((newArmies) + " new armies available");

        GM.strengthenTerritories(player.getId(), thisGame);
    }

    // handles calculations of trading-in a set
    public int calculateTradeableCard() throws InterruptedException {
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
                // System.out.println("One of your cards have been found to represent a territory you're currently occupying." +
                //        " +2 Armies will be added to your territory: "+ bonusTo);
                messenger.putMessage("One of your cards have been found to represent a territory you're currently occupying." +
                                " +2 Armies will be added to your territory: "+ bonusTo);
                BM.addOccupantsTo(bonusTo, 2);
            }
            return new_sum;
        } else{
            return new_sum;
        }
    }

    public String getAttackableTerritories() {
        String out = "";
        for(String country: BM.getAbleTerritories(player, true))
        {
            out += country + ": " + BM.getOccupantCount(country) + " armies, CAN ATTACK\n";
            for(String enemy: BM.getAllAdjacentEnemyTerritories(player.getId(), country))
            {
                // System.out.println("\t"+enemy + ", " + BM.getOccupantCount(enemy) +" enemy armies");
                messenger.putMessage("\t"+enemy + ", " + BM.getOccupantCount(enemy) +" enemy armies\n");
            }
        }
        return out;
    }

    public String battle(String attacker, String defender, int attackerDice, int defenderDice) {

        String out = null;
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
        int topIndexAttacker;
        int topIndexDefender;
        int potentialTransfer = attackerDice;

        do {
            topIndexAttacker = GM.getIndexOfHighestRollIn(attacker_dice, attacker_dice.size());
            topIndexDefender = GM.getIndexOfHighestRollIn(defender_dice, defender_dice.size());

            if (attacker_dice.get(topIndexAttacker) > defender_dice.get(topIndexDefender)) {
                BM.removeOccupantsFrom(defender, 1);
                if (BM.getOccupantCount(defender) == 0) { // if the defender loses all of its armies
                    BM.getBoardMap().get(defender).setStatusToFallen();
                    GM.getPlayer(BM.getBoardMap().get(defender).getOccupantID()).loseTerritories(defender); // take remove territory from defending player
                    BM.getBoardMap().get(attacker).loseOccupants(potentialTransfer, ArmyType.INFANTRY); // remove remaining attacking armies from attacking territory
                    GM.getPlayer(player.getId()).addArmies(potentialTransfer);
                    BM.initializeTerritory(player, defender, potentialTransfer); // reinitialize territory for defender
                    out += "Defender has lost the territory\n";
                    break;
                }
            }

            else if(attacker_dice.get(topIndexAttacker).equals(defender_dice.get(topIndexDefender)) || attacker_dice.get(topIndexAttacker) < defender_dice.get(topIndexDefender)) {
                BM.removeOccupantsFrom(attacker, 1);
                potentialTransfer--;
                if(BM.getOccupantCount(attacker) == 1) {
                    break;
                }
            }

            // takes other pairs in mind (next highest)
            attacker_dice.remove(topIndexAttacker);
            defender_dice.remove(topIndexDefender);

            // runs roll comparison again
            if(attacker_dice.size() == 0 || defender_dice.size() == 0) break;

        }while (true);

        BM.getBoardMap().get(defender).setStatusToNormal();
        out += "Attacker lost " + (attackerDice - attacker_dice.size()) + ", Defender lost " + (defenderDice - defender_dice.size()) + "\n";
        return out;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Fortifying is simple
    *///////////////////////////////////////////////////////////////////////////////*/
    public void fortifyTerritories(Game thisGame) throws InterruptedException {
        String origin;
        String destination;
        int transfer;
        // System.out.println("__FORTIFY TERRITORIES__");
        messenger.putMessage("__FORTIFY TERRITORIES__");

        if(GM.baseQuery("Would you like to fortify your territories?"))
        {
            for(String country: BM.getAbleTerritories(player, false)) {
                //System.out.println(country + ": " + BM.getOccupantCount(country) + " armies");
                messenger.putMessage(country + ": " + BM.getOccupantCount(country) + " armies");
            }

            do{
                origin = BM.queryTerritory("From: ", "FORTIFY_FROM", player, "", thisGame);
            }while(origin == null);

            //System.out.println("Territories fortify-able from " + origin);
            messenger.putMessage("Territories fortify-able from " + origin);

            for(String country: BM.getAllAdjacentTerritories(player.getId(), origin)) {
                // System.out.println(country);
                messenger.putMessage(country);
            }

            do{
                destination = BM.queryTerritory("Fortify: ", "FORTIFY", player, origin, thisGame);
            }while(destination == null);

            do{
                transfer = BM.queryCount("Transfer: ", "FORTIFY", player, origin, thisGame);
            } while(transfer == 0);

            BM.fortifyTerritory(origin, destination, transfer);
        }


    }
}
