import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;


/**/
public class BoardManager {

    private static HashMap<String, Territory> thisMap;

    /**/
    BoardManager(String path){
        thisMap = new HashMap<String, Territory>();
        try {
            FileReader json = new FileReader(path);
            JsonParser parser = new JsonParser();

            Gson gson = new Gson();
            JsonObject rootObj = parser.parse(json).getAsJsonObject();
            JsonArray mapArray = rootObj.getAsJsonArray("map");

            for(JsonElement mapItem: mapArray){
                JsonObject territoryObject = mapItem.getAsJsonObject();
                String territoryName = territoryObject.get("name").getAsString();
                JsonArray neighborsObject = territoryObject.get("neighbors").getAsJsonArray();

                Type listType= new TypeToken<List<String>>() {}.getType();
                List<String> territoryNeighbors = gson.fromJson(neighborsObject, listType);
                thisMap.put(territoryName, new Territory(false, -1, null, territoryNeighbors));
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    /**/
    public boolean isAllTerritoriesInitialized(){
        boolean occupied = true;
        for(HashMap.Entry<String, Territory> country: thisMap.entrySet()){
            occupied = (occupied && country.getValue().isOccupied());
            if (!occupied){
                break;
            }
        }
        return occupied;
    }
    /**/
    public void seeUntakenTerritories(){
        System.out.println("\n__________________________________________\nUNTAKEN-TERRITORIES");
        for(HashMap.Entry<String, Territory> country: thisMap.entrySet()){
            if(!thisMap.get(country.getKey()).isOccupied()){
                System.out.println(country.getKey());
            }
        }

    }


    /* Refactor first few lines of method */
    public void setupInitialTerritories(Player player) {
        boolean askAgainForInput;
        System.out.print("\n__________________________________________\nPlayer #" + player.getId());
        Scanner country = new Scanner(System.in);
        do{
            try {
                System.out.println();
                System.out.print("Select: ");
                String countryInput = country.nextLine();

                if (thisMap.get(countryInput).isOccupied()){
                    throw new Exception("Error: Chosen country already occupied.");
                }

                thisMap.get(countryInput).setTerritory(true, player.getId(), new Army(player.getId(), 1));
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

    /**/
    public void seeNeighbors(String key){
        for(String k: thisMap.get(key).getNeighbors()){
            System.out.println(k);
        }
    }
    public String getOccupantCount(String country){
        return thisMap.get(country).seeArmyCount();
    }
    public void addOccupantsTo(String country, int count, String type){
        try {
            thisMap.get(country).addOccupants(count, type);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }




}
