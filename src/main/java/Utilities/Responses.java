package Utilities;

import java.util.ArrayList;

public class Responses {

    public static String onStart(){
        return  "Hi! To play risk, please select an action below: \n" +
                "/create to create a new instance of the game\n" +
                "/join to select an available session\n" +
                "/load to replay a game.";

    }
    public static String onJoin(ArrayList<String> listing){
        if(listing.size() < 1){
            return "There are currently no sessions being played.";
        } else {
            String res = (listing.size() + " live game sessions have been found. Select one to join: ");
            for (String k : listing) {
                res += ("\n" + k);
            }
            return res;
        }
    }
}
