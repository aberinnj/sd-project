import java.lang.reflect.Array;
import java.util.*;

/*////////////////////////////////////////////////////////////////////////////////
Player Class

todo: resetTerritories renders null throwing nullpointerexception in testing
*///////////////////////////////////////////////////////////////////////////////*/
public class Player {

    private int id;
    private Army placeholder;
    private List<String> territories;
    private HashMap<String, Stack<Card>> Hand;
    private float wallet = 0;
    private int Undos = 0;

    // add undos
    public void addUndos(int undos) { Undos = Undos + undos; }

    // get undos
    public int getUndos() { return Undos; }

    // add to the wallet
    public void addMoney(float add) { wallet = wallet + add; }

    // get wallet contents
    public float getWallet() { return wallet; }

    // get player id
    public int getId() {
        return id;
    }

    // get continents
    public int getContinentsOwned(BoardManager bm){
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

    // get hand
    public HashMap<String, Stack<Card>> getHand() {
        return Hand;
    }

    // get territories
    public List<String> getTerritories(){ return territories; }

    // get number of armies
    public int getNumberOfArmies(){ return placeholder.getInfantryCount(); }

    /*////////////////////////////////////////////////////////////////////////////////
    *///////////////////////////////////////////////////////////////////////////////*/
    public void addTerritories(String country){
        territories.add(country);
    }

    /*////////////////////////////////////////////////////////////////////////////////
    *///////////////////////////////////////////////////////////////////////////////*/
    public void loseTerritories(String country){
            territories.remove(country);
    }

    // add to armies
    public void addArmies(int armies) { placeholder.addInfantryCount(armies); }

    // diminish armies
    public void loseArmies(int armies) {placeholder.loseInfantry(armies);}

    // total cards in hand
    public int getTotalCards(){
        return Hand.get("CAVALRY").size() + Hand.get("INFANTRY").size() + Hand.get("ARTILLERY").size() + Hand.get("WILD").size();
    }

    public ArrayList<Card> getHandListing(){
        ArrayList<Card> k = new ArrayList<Card>();
        k.addAll(Hand.get("INFANTRY"));
        k.addAll(Hand.get("CAVALRY"));
        k.addAll(Hand.get("ARTILLERY"));
        k.addAll(Hand.get("WILD"));

        return k;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns the total number of remaining infantry
    *///////////////////////////////////////////////////////////////////////////////*/
    public boolean isPlayerTheWinner(BoardManager bm){
        return territories.size() == bm.getBoardMap().keySet().size();
    }

    // new-game constructor
    Player(int id, int infantryCount)
    {
        this.id = id;
        placeholder = new Army(infantryCount);
        Hand = new HashMap<String, Stack<Card>>();
        territories = new ArrayList<String>();

        Hand.put("INFANTRY", new Stack<Card>());
        Hand.put("CAVALRY", new Stack<Card>());
        Hand.put("ARTILLERY", new Stack<Card>());
        Hand.put("WILD", new Stack<Card>());
    }

    // load-game constructor
    Player(int id, int infantryCount, ArrayList<Card> cardStack, ArrayList<String> territories)
    {
        this.id = id;
        this.placeholder = new Army(infantryCount);
        this.Hand = new HashMap<String, Stack<Card>>();
        Hand.put("INFANTRY", new Stack<Card>());
        Hand.put("CAVALRY", new Stack<Card>());
        Hand.put("ARTILLERY", new Stack<Card>());
        Hand.put("WILD", new Stack<Card>());

        for(Card e: cardStack)
        {
            Hand.get(e.getUnit()).push(e);
        }
        this.territories = territories;



    }
}
