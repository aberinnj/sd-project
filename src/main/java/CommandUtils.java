import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

/*//////////////////////////////////////////////////////////////////////////////////
Main function
//////////////////////////////////////////////////////////////////////////////////*/
public class CommandUtils {

    public static ChatInput getInput(String message){

        String[] listing = message.split(" ");
        ChatInput parsedInput = new ChatInput();
        parsedInput.command = listing[0];

        for(int i=1; i<listing.length; i++)
        {
            parsedInput.args.add(listing[i]);
        }
        return parsedInput;
    }



    // function to get the game being communicated with
    public static Game getGame(int user_id) {
        String gameID = _GameMaster.allPlayersAndTheirGames.get(user_id);
        Game game = _GameMaster.gamesListing.get(gameID);
        return game;
    }


    public static Player getPlayer(Game game) {
        ArrayList<Integer> tempListing = new ArrayList<>();
        tempListing.addAll(game.playerDirectory.keySet());
        return game.playerDirectory.get(tempListing.get(game.turn % game.playerDirectory.size()));
    }

    public static Boolean isReinforcingOver(Game game){
        boolean reinforceDone = true;
        ArrayList<Integer> tempListing = new ArrayList<>();
        tempListing.addAll(game.playerDirectory.keySet());
        for(Integer k: tempListing)
        {
            int armiesLeft = game.playerDirectory.get(k).getNumberOfArmies();
            reinforceDone = reinforceDone && (armiesLeft==0);
        }
        return reinforceDone;
    }
}
