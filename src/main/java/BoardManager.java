import java.text.ParseException;
import java.util.*;

/*////////////////////////////////////////////////////////////////////////////////
Continent Class
*///////////////////////////////////////////////////////////////////////////////*/
class Continent{
    List<String> FullContinent;

    Continent(String[] k) {
        FullContinent = new ArrayList<String>();
        FullContinent.addAll(Arrays.asList(k));
    }
}
/*////////////////////////////////////////////////////////////////////////////////
BoardManager class HANDLES TERRITORY, CONTINENT and DECK

Expects
todo: fix addTo and RemoveFrom below
*///////////////////////////////////////////////////////////////////////////////*/
public class BoardManager {
    private static HashMap<String, Territory> boardMap;
    private static HashMap<String, Continent> continentsMap;
    private static Deck gameDeck;
    public int completeSets;

    // Initializes the BoardManager variables and should be testable after
    BoardManager(){
        continentsMap = new HashMap<String,Continent>();
        boardMap = new HashMap<String, Territory>();

        boardMap.put("ALASKA", new Territory(new String[]{"KAMCHATKA", "NORTH WEST TERRITORY", "ALBERTA"}));
        boardMap.put("NORTH WEST TERRITORY", new Territory(new String[]{"ALASKA", "ALBERTA", "ONTARIO", "GREENLAND"}));
        boardMap.put("GREENLAND", new Territory(new String[]{"NORTH WEST TERRITORY", "ONTARIO", "QUEBEC", "ICELAND"}));
        boardMap.put("ALBERTA", new Territory(new String[]{"ALASKA", "NORTH WEST TERRITORY", "ONTARIO", "WESTERN UNITED STATES"}));
        boardMap.put("ONTARIO", new Territory(new String[]{"ALBERTA", "NORTH WEST TERRITORY", "QUEBEC", "GREENLAND", "WESTERN UNITED STATES", "EASTERN UNITED STATES"}));
        boardMap.put("QUEBEC", new Territory(new String[]{"ONTARIO", "EASTERN UNITED STATES", "GREENLAND"}));
        boardMap.put("WESTERN UNITED STATES", new Territory(new String[]{"ALBERTA", "ONTARIO", "EASTERN UNITED STATES", "CENTRAL AMERICA"}));
        boardMap.put("EASTERN UNITED STATES", new Territory(new String[]{"WESTERN UNITED STATES", "ONTARIO", "QUEBEC", "CENTRAL AMERICA"}));
        boardMap.put("CENTRAL AMERICA", new Territory(new String[]{"WESTERN UNITED STATES", "EASTERN UNITED STATES", "VENEZUELA"}));
        boardMap.put("VENEZUELA", new Territory(new String[]{"CENTRAL AMERICA", "PERU", "BRAZIL"}));
        boardMap.put("PERU", new Territory(new String[]{"VENEZUELA", "BRAZIL", "ARGENTINA"}));
        boardMap.put("BRAZIL", new Territory(new String[]{"VENEZUELA", "PERU", "ARGENTINA", "NORTH AFRICA"}));
        boardMap.put("ARGENTINA", new Territory(new String[]{"PERU", "BRAZIL"}));
        boardMap.put("NORTH AFRICA", new Territory(new String[]{"BRAZIL", "SOUTHERN EUROPE", "EGYPT", "EAST AFRICA", "CONGO"}));
        boardMap.put("CONGO", new Territory(new String[]{"NORTH AFRICA", "EAST AFRICA", "SOUTH AFRICA"}));
        boardMap.put("EGYPT", new Territory(new String[]{"NORTH AFRICA", "SOUTHERN EUROPE", "MIDDLE EAST", "EAST AFRICA"}));
        boardMap.put("EAST AFRICA", new Territory(new String[]{"NORTH AFRICA", "EGYPT", "MIDDLE EAST", "CONGO", "SOUTH AFRICA", "MADAGASCAR"}));
        boardMap.put("SOUTH AFRICA", new Territory(new String[]{"CONGO", "EAST AFRICA", "MADAGASCAR"}));
        boardMap.put("MADAGASCAR", new Territory(new String[]{"SOUTH AFRICA", "EAST AFRICA"}));
        boardMap.put("MIDDLE EAST", new Territory(new String[]{"EGYPT", "EAST AFRICA", "SOUTHERN EUROPE", "UKRAINE", "AFGHANISTAN", "INDIA"}));
        boardMap.put("INDIA", new Territory(new String[]{"MIDDLE EAST", "AFGHANISTAN", "CHINA", "SIAM"}));
        boardMap.put("SIAM", new Territory(new String[]{"INDIA", "CHINA", "INDONESIA"}));
        boardMap.put("INDONESIA", new Territory(new String[]{"SIAM", "NEW GUINEA", "WESTERN AUSTRALIA"}));
        boardMap.put("WESTERN AUSTRALIA", new Territory(new String[]{"INDONESIA", "NEW GUINEA", "EASTERN AUSTRALIA"}));
        boardMap.put("EASTERN AUSTRALIA", new Territory(new String[]{"WESTERN AUSTRALIA", "NEW GUINEA"}));
        boardMap.put("NEW GUINEA", new Territory(new String[]{"INDONESIA", "WESTERN AUSTRALIA", "EASTERN AUSTRALIA"}));
        boardMap.put("CHINA", new Territory(new String[]{"SIAM", "INDIA", "AFGHANISTAN", "URAL", "SIBERIA", "MONGOLIA"}));
        boardMap.put("AFGHANISTAN", new Territory(new String[]{"MIDDLE EAST", "INDIA", "CHINA", "URAL", "UKRAINE"}));
        boardMap.put("URAL", new Territory(new String[]{"UKRAINE", "SIBERIA", "CHINA", "AFGHANISTAN"}));
        boardMap.put("SIBERIA", new Territory(new String[]{"URAL", "CHINA", "MONGOLIA", "IRKUTSK", "YAKUTSK"}));
        boardMap.put("YAKUTSK", new Territory(new String[]{"SIBERIA", "IRKUTSK", "KAMCHATKA"}));
        boardMap.put("KAMCHATKA", new Territory(new String[]{"YAKUTSK", "IRKUTSK", "ALASKA", "JAPAN", "MONGOLIA"}));
        boardMap.put("IRKUTSK", new Territory(new String[]{"SIBERIA", "YAKUTSK", "KAMCHATKA", "MONGOLIA"}));
        boardMap.put("MONGOLIA", new Territory(new String[]{"SIBERIA", "IRKUTSK", "KAMCHATKA", "JAPAN", "CHINA"}));
        boardMap.put("JAPAN", new Territory(new String[]{"KAMCHATKA", "MONGOLIA"}));
        boardMap.put("UKRAINE", new Territory(new String[]{"SCANDINAVIA", "URAL", "AFGHANISTAN", "MIDDLE EAST", "SOUTHERN EUROPE", "NORTHERN EUROPE"}));
        boardMap.put("SCANDINAVIA", new Territory(new String[]{"UKRAINE", "NORTHERN EUROPE", "GREAT BRITAIN", "ICELAND"}));
        boardMap.put("ICELAND", new Territory(new String[]{"GREENLAND", "SCANDINAVIA", "GREAT BRITAIN"}));
        boardMap.put("GREAT BRITAIN", new Territory(new String[]{"ICELAND", "SCANDINAVIA", "NORTHERN EUROPE", "WESTERN EUROPE"}));
        boardMap.put("NORTHERN EUROPE", new Territory(new String[]{"GREAT BRITAIN", "SCANDINAVIA", "UKRAINE", "SOUTHERN EUROPE", "WESTERN EUROPE"}));
        boardMap.put("SOUTHERN EUROPE", new Territory(new String[]{"WESTERN EUROPE", "NORTHERN EUROPE", "UKRAINE", "MIDDLE EAST", "EGYPT", "NORTH AFRICA"}));
        boardMap.put("WESTERN EUROPE", new Territory(new String[]{"GREAT BRITAIN", "NORTHERN EUROPE", "SOUTHERN EUROPE", "NORTH AFRICA"}));

        continentsMap.put("NORTH AMERICA", new Continent(new String[]{"ALASKA", "NORTH WEST TERRITORY", "ALBERTA","ONTARIO","QUEBEC","GREENLAND", "WESTERN UNITED STATES", "EASTERN UNITED STATES", "CENTRAL AMERICA"}));
        continentsMap.put("SOUTH AMERICA", new Continent(new String[]{"VENEZUELA", "BRAZIL", "PERU", "ARGENTINA"}));
        continentsMap.put("EUROPE", new Continent(new String[]{"WESTERN EUROPE", "GREAT BRITAIN", "ICELAND", "SCANDINAVIA", "NORTHERN EUROPE", "SOUTHERN EUROPE", "UKRAINE"}));
        continentsMap.put("AFRICA", new Continent(new String[]{"NORTH AFRICA", "EGYPT", "CONGO", "EAST AFRICA", "SOUTH AFRICA", "MADAGASCAR"}));
        continentsMap.put("ASIA", new Continent(new String[]{"SIAM", "INDIA", "AFGHANISTAN", "URAL", "SIBERIA", "MONGOLIA", "CHINA", "MIDDLE EAST", "JAPAN", "YAKUTSK", "IRKUTSK", "KAMCHATKA"}));
        continentsMap.put("AUSTRALIA", new Continent(new String[]{"WESTERN AUSTRALIA", "INDONESIA", "EASTERN AUSTRALIA", "NEW GUINEA"}));

        gameDeck = new Deck();

        completeSets = 0;
    }

    // queryTerritory consults with the boardMap for territories
    public String queryTerritory(Scanner scanner, String query, String type, Player p, String origin) {
        System.out.println(query);
        String country = scanner.nextLine();

        // ALL queries for a territory requires a country to exist
        if (!boardMap.containsKey(country)) {
            System.out.println("Error: Territory not found. ");
            return null;
        }

        // strengthening requires a country to be owned by the player
        if (type.equals("STRENGTHEN")) {
            if(boardMap.get(country).getOccupantID() != p.getId()) {
                System.out.println("Error: Territory not owned. ");
                return null;
            }

        // initialization requires a country selected to be free
        } else if (type.equals("INITIALIZE")) {
            if(boardMap.get(country).isOccupied()){
                System.out.println("Error: Territory already occupied. ");
                return null;
            }

        // attacking from a country requires the country to be a player's territory, the army count be > 1 and the enemy neighbors to be > 1
        } else if (type.equals("ATTACK_FROM")) {
            if(boardMap.get(country).getOccupantID() != p.getId()) {
                System.out.println("Error: Territory not owned. ");
                return null;
            } else if ( getOccupantCount(country) <= 1) {
                System.out.println("Error: Territory not valid to attack from. ");
                return null;
            } else if ( getAllAdjacentEnemyTerritories(p.getId(), country).size() == 0) {
                System.out.println("Error: Territory seems to have no neighbors that can be attacked. ");
                return null;
            }

        // selecting an enemy country requires the country to be an enemy's territory and adjacent to an origin
        } else if (type.equals("ATTACK")) {
            if(boardMap.get(country).getOccupantID() == p.getId()) {
                System.out.println("Error: Territory is not an enemy territory. ");
                return null;
            } else if (!getAllAdjacentEnemyTerritories(p.getId(), origin).contains(country)) {
                System.out.println("Error: Territory is not adjacent to origin. ");
                return null;
            }


        // fortifying from a country requires the country to be player's territory, the army count to be more than one and the friendly neighbors to be > 0
        } else if (type.equals("FORTIFY_FROM")) {
            if(boardMap.get(country).getOccupantID() != p.getId()) {
                System.out.println("Error: Territory not owned. ");
                return null;
            } else if ( boardMap.get(country).getArmy().getInfantryCount() <= 1) {
                System.out.println("Error: Territory not valid to fortify from. ");
                return null;
            } else if ( getAllAdjacentTerritories(p.getId(), country).size() == 0) {
                System.out.println("Error: Territory seems to have no neighbors to fortify. ");
                return null;
            }

        // selecting a country to fortify requires the country to be the player's territory and adjacent to an origin
        } else if (type.equals("FORTIFY")) {
            if(boardMap.get(country).getOccupantID() != p.getId()) {
                System.out.println("Error: Territory is not an territory. ");
                return null;
            } else if (!getNeighborsOf(origin).contains(country)) {
                System.out.println("Error: Territory is not adjacent to origin. ");
                return null;
            }
        }
        return country;
    }

    // queryCount consults with territory-occupantCount
    public int queryCount(Scanner scanner, String query, String type, Player p, String origin) {
        System.out.println(query);
        int count;
        try{
            count = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e){
            System.out.println("Error: input is not a number. ");
            return 0;
        }

        // for attacks, the number of dice that can be rolled is 1, 2 or 3, the count of occupants must also be more than 1
        if(type.equals("ATTACK")) {
            if(getOccupantCount(origin) <= (count)) {
                System.out.println("Error: " + origin + " does not have enough armies to roll " + count + " dice. ");
                return 0;
            } else if (count > 3) {
                System.out.println("Warning: The maximum is 3 dice. Defaulting dice roll to 3." );
                return 3;
            }

        } else if (type.equals("DEFEND")) {
            if(getOccupantCount(origin) < count) {
                System.out.println("Error: " + origin + " does not have enough armies to roll " + count + " dice. ");
                return 0;
            } else if (count > 2) {
                System.out.println("Warning: The maximum is 2 dice. Defaulting dice roll to 2." );
                return 2;
            }
        } else if (type.equals("FORTIFY")) {
            if(getOccupantCount(origin) <= count) {
                System.out.println("Error: There are not enough occupants in " + origin + " to transfer " + count + " armies. ");
                return 0;
            }

        }
        return count;
    }

    // simply returns boardmap
    public HashMap<String, Territory> getBoardMap(){
        return boardMap;
    }

    // simply returns gamedeck
    public Deck getGameDeck() {return gameDeck;}

    // used for initialization in constructor above
    public List<String> getContinentsMap(String name) {
        return continentsMap.get(name).FullContinent;
    }

    // Displays free territories
    public List<String> getFreeTerritories(){
        List<String> listing = new ArrayList<String>();

        for(HashMap.Entry<String, Territory> country: boardMap.entrySet()){
            if(!boardMap.get(country.getKey()).isOccupied()){
                listing.add(country.getKey());
            }
        }
        return listing;
    }

    // Gets a player's territories, but most useful in getting territories a player can attack from
    public List<String> getAbleTerritories(Player p, boolean attacking) {
        List<String> listing = new ArrayList<String>();
        for(String country: p.getTerritories()) {
            if(attacking) {
                if (getOccupantCount(country) > 1)
                    listing.add(country);
            } else{
                listing.add(country);
            }
        }
        return listing;
    }

    // Get All
    public List<String> getAllAdjacentTerritories(int id, String countryOfOrigin) {
        List<String> listing = new ArrayList<String>();
        for(String country: getNeighborsOf(countryOfOrigin)){
            if(boardMap.get(country).getOccupantID() == id)
                listing.add(country);
        }
        return listing;
    }

    public List<String> getAllAdjacentEnemyTerritories(int id, String countryOfOrigin) {
        List<String> listing = new ArrayList<String>();
         for(String country: getNeighborsOf(countryOfOrigin)){
            if(boardMap.get(country).getOccupantID() != id)
                listing.add(country);
        }
        return listing;
    }

    public List<String> getNeighborsOf(String country) {
        return boardMap.get(country).getNeighbors();
    }

    // Takes care of setting territories for initialization // can also be used after winning a territory
    // Note: Attacking takes an amount of armies and places them into player's placeholder Army
    public void initializeTerritory(Player p, String territory, int armyCount ){
        boardMap.get(territory).setTerritory(true, p.getId(), new Army(armyCount));
        p.addTerritories(territory);
        p.loseArmies(armyCount);
    }

    public void strengthenTerritory(Player p, String territory, int armyCount) {
        addOccupantsTo(territory, armyCount);
        p.loseArmies(armyCount);
    }

    public void fortifyTerritory(String origin, String territory, int armyCount) {
        removeOccupantsFrom(origin, armyCount);
        addOccupantsTo(territory, armyCount);
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns occupant count of a territory as int
    *///////////////////////////////////////////////////////////////////////////////*/
    public int getOccupantCount(String country){
        return boardMap.get(country).getArmy().getInfantryCount();
    }

    /*////////////////////////////////////////////////////////////////////////////////
    *///////////////////////////////////////////////////////////////////////////////*/
    public void addOccupantsTo(String country, int count) {
        boardMap.get(country).addOccupants(count, ArmyType.INFANTRY);

    }

    public void removeOccupantsFrom(String country, int count) {
        boardMap.get(country).loseOccupants(count, ArmyType.INFANTRY);

    }

}
