import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

class Continent{
    public List<String> FullContinent;

    Continent(List<String> k)
    {
        FullContinent = k;
    }

}

public class BoardManager {

    private static HashMap<String, Territory> boardMap;                                                                 // boardMap is a hashmap <TerritoryName, TerritoryObject>
                                                                                                                        // TerritoryName is used as a key to get the corresponding object
                                                                                                                        // TerritoryObject is an object of class Territory

    private static HashMap<String, Continent> continentsMap = new HashMap<String,Continent>();                          //continentsMap is hashmap <ContinentName, Terrtories> used to check

    private static Deck gameDeck;
    /*////////////////////////////////////////////////////////////////////////////////
    Constructor, constructs map and puts it inside a hashmap

    Make a parser and parse json to get map=[]
    for each object inside map, get territoryName and neighborsObject
    Assign neighborsObject into a list, territoryNeighbors and add into boardMap

    Refactor.
    *///////////////////////////////////////////////////////////////////////////////*/
    BoardManager(String mapPath, String deckPath){
        boardMap = new HashMap<String, Territory>();
        try {
            FileReader json = new FileReader(mapPath);
            JsonParser parser = new JsonParser();

            Gson gson = new Gson();
            JsonObject rootObj = parser.parse(json).getAsJsonObject();
            JsonArray mapArray = rootObj.getAsJsonArray("map");

            for(JsonElement mapItem: mapArray){
                // gets {name, neighbors}
                JsonObject territoryObject = mapItem.getAsJsonObject();
                String territoryName = territoryObject.get("name").getAsString();
                JsonArray neighborsObject = territoryObject.get("neighbors").getAsJsonArray();

                // adds a new item where key: territoryName and value: a new territory object
                Type listType= new TypeToken<List<String>>() {}.getType();
                List<String> territoryNeighbors = gson.fromJson(neighborsObject, listType);
                boardMap.put(territoryName, new Territory(false, -1, null, territoryNeighbors));
            }

            JsonArray continentArray = rootObj.getAsJsonArray("continents");
            // gets {name, territories}
            for(JsonElement continentItem: continentArray){
                JsonObject continentObject = continentItem.getAsJsonObject();
                String continentName = continentObject.get("name").getAsString();
                JsonArray territoriesObject = continentObject.get("territories").getAsJsonArray();

                Type listType= new TypeToken<List<String>>() {}.getType();
                List<String> territoriesOfContinent = gson.fromJson(territoriesObject, listType);
                //System.out.println(continentName);
                continentsMap.put(continentName, new Continent(territoriesOfContinent));
            }

            gameDeck = new Deck(deckPath);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns a the list of continents
    *///////////////////////////////////////////////////////////////////////////////*/
    public List<String> getContinentsMap(String name) {

        return continentsMap.get(name).FullContinent;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method iterates through the hashmap boardMap and checks if the territory.isOccupied
    Occupied below becomes false when a territory is not occupied
    *///////////////////////////////////////////////////////////////////////////////*/
    public boolean isAllTerritoriesInitialized(){
        boolean allOccupied = true;
        for(HashMap.Entry<String, Territory> country: boardMap.entrySet()){
            allOccupied = (allOccupied && country.getValue().isOccupied());
            if (!allOccupied){
                break;
            }
        }
        return allOccupied;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method iterates through the hashmap and displays all unoccupied territory
    *///////////////////////////////////////////////////////////////////////////////*/
    public void displayUntakenTerritories(){
        System.out.println("__________________________________________");
        System.out.println("UNTAKEN-TERRITORIES");

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

    /*////////////////////////////////////////////////////////////////////////////////
    Method prompts user for a territory to send an infantry to and initialize an army in .

    Adds new Army to a territory
    Diminishes number of player's initial number of infantry by 1(to transfer to territory)
    Adds territory to Player's list of territories

    Refactor.
    *///////////////////////////////////////////////////////////////////////////////*/
    public void setInitialTerritory(Player player, Utilities utils) {
        boolean askAgainForInput;

        //Scanner country = new Scanner(System.in);
        do{
            try {
                System.out.println();
                System.out.print("Select: ");
                String countryInput = utils.getInput("Player #" + player.getId() + " -- Select: ");

                if (boardMap.get(countryInput).isOccupied()){
                    throw new Exception("Error: Chosen country already occupied.");
                }

                boardMap.get(countryInput).setTerritory(true, player.getId(), new Army(1));
                player.shipArmy();
                player.addTerritories(countryInput);


                askAgainForInput = false;
            } catch (InputMismatchException e){
                System.out.print("Error: Invalid input.");
                askAgainForInput = true;
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
