import java.util.*;

class Continent{
    List<String> FullContinent;

    Continent(String[] k) {
        FullContinent = new ArrayList<String>();
        FullContinent.addAll(Arrays.asList(k));
    }
}
/*////////////////////////////////////////////////////////////////////////////////
BoardManager Class handles Territory and Continents, as well as the Deck

Expects
todo: fix bug with loop
*///////////////////////////////////////////////////////////////////////////////*/
public class BoardManager {
    private static HashMap<String, Territory> boardMap;
    private static HashMap<String, Continent> continentsMap;
    private static Deck gameDeck;

    // Initializes the BoardManager variables and should be testable after
    BoardManager(String deckPath){
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
        boardMap.put("BRAZIL", new Territory(new String[]{"VENEZUELA", "PERU", "ARGENTINA", "NORTH AMERICA"}));
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

        gameDeck = new Deck(deckPath);
    }

    public HashMap<String, Territory> getBoardMap(){
        return boardMap;
    }

    public void setBoardMap(HashMap<String, Territory> newBoardMap)
    {
        for(String i: boardMap.keySet())
        {
            boardMap.get(i).setTerritory(
                    newBoardMap.get(i).isOccupied(),
                    newBoardMap.get(i).getOccupantID(),
                    new Army(newBoardMap.get(i).getArmy().getInfantryCount()));
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns a the list of continents
    *///////////////////////////////////////////////////////////////////////////////*/
    public List<String> getContinentsMap(String name) {
        return continentsMap.get(name).FullContinent;
    }

    // Method check if all territories are initialized and return boolean
    public boolean isAllTerritoriesInitialized()
    {
        for(String country: boardMap.keySet()) {
            if (!boardMap.get(country).isOccupied())
                return false;
        }
        return true;
    }

    // Displays free territories by printing to console
    public void displayFreeTerritories(){
        System.out.println("__Free Territories__");

        for(HashMap.Entry<String, Territory> country: boardMap.entrySet()){
            if(!boardMap.get(country.getKey()).isOccupied()){
                System.out.println(country.getKey());
            }
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method gets the number of territories involved in the game
    currently being only used for checking if player has taken all the territories
    *///////////////////////////////////////////////////////////////////////////////*/
    public int getNumberOfTerritories(){
        return boardMap.size();
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns true of a territory is adjacent to origin
    *///////////////////////////////////////////////////////////////////////////////*/
    public boolean isTerritoryANeighborOf(String country, String origin){
        return boardMap.get(origin).getNeighbors().contains(country);
    }

    public List<String> getNeighbors(String country) {
        return boardMap.get(country).getNeighbors();
    }

    public int getTerritoryID(String country) {
        return boardMap.get(country).getOccupantID();
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method prompts user for a territory to send an infantry to and initialize an army in .

    Adds new Army to a territory
    Diminishes number of player's initial number of infantry by 1(to transfer to territory)
    Adds territory to Player's list of territories

    Refactor.
    *///////////////////////////////////////////////////////////////////////////////*/
    public void setInitialTerritory(Player player, Scanner country) {
        boolean askAgainForInput;

        System.out.println("__________________________________________");
        System.out.println("Player #" + player.getId());
        //Scanner country = new Scanner(System.in);
        do{
            try {
                System.out.println();
                System.out.print("Select: ");
                String countryInput = country.nextLine();

                if (boardMap.get(countryInput).isOccupied()){
                    throw new Exception("Error: Chosen country already occupied.");
                }

                boardMap.get(countryInput).setTerritory(true, player.getId(), new Army(1));
                player.shipArmy();
                player.addTerritories(countryInput);

                askAgainForInput = false;
            } catch (NullPointerException e){
                System.out.print("Error: Country not found");
                askAgainForInput = true;
            } catch (Exception e){
                System.out.print(e.getMessage());
                askAgainForInput = true;
            }
        } while(askAgainForInput);
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns occupant count of a territory as int
    *///////////////////////////////////////////////////////////////////////////////*/
    public int getOccupantCount(String country){
        return boardMap.get(country).ArmyCount();
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns occupant count of a territory as a STATUS string
    Used in displaying territories with the count of user armies in it
    *///////////////////////////////////////////////////////////////////////////////*/
    public String getOccupantCountStatus(String country){
        return boardMap.get(country).seeArmyCount();
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method adds occupants to a territory that has already been occupied.
    *///////////////////////////////////////////////////////////////////////////////*/
    public void addOccupantsTo(String country, int count, String type){
        try {
            boardMap.get(country).addOccupants(count, type);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void removeOccupantsFrom(String country, int count, String type) {
        try {
            boardMap.get(country).loseOccupants(count, type);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method loses occupants in a territory
    *///////////////////////////////////////////////////////////////////////////////*/
    public void transferOccupantsFrom(String country, int count, String type){
        try {
            boardMap.get(country).loseOccupants(count, type);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void transferOwnership(String attacker, String defender) {
        try {
            int newOwner = boardMap.get(attacker).getOccupantID();
            boardMap.get(defender).transferOwnership(newOwner);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
