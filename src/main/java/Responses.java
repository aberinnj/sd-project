/*////////////////////////////////////////////////////////////////////////////////

*///////////////////////////////////////////////////////////////////////////////*/

import java.util.ArrayList;
import java.util.List;

public class Responses {

    public static String onStart(){
        return "Welcome! To play risk, you can either do the following:" +
                "\n/create your own session for this chat, if the chat is not playing" +
                "\n/join if the chat is playing and there's enough players." +
                "\n/help to view all the available commands.";

    }

    public static String onListAllGames(){
        if (_GameMaster.gamesListing.isEmpty()){
            return "There are no current sessions. /create to create your own session.";
        }
        else {
            String res = "The following game sessions have been found.\n";
            for(String game_id: _GameMaster.gamesListing.keySet()){

                res += _GameMaster.gamesListing.get(game_id).playerDirectory.size();
                res += " player(s) \t";
                res += game_id;

                res += "\n";
            }
            return res;
        }
    }

    public static String onListMyGames(int user_id){
        if (_GameMaster.gamesListing.isEmpty()){
            return "There are no current sessions. /create to create your own session.";
        }
        else {
            String res = "";
            boolean gamesFound = false;
            for(String id: _GameMaster.gamesListing.keySet()){
                if (_GameMaster.gamesListing.get(id).playerDirectory.containsKey(user_id)) {
                    res += _GameMaster.gamesListing.get(id).playerDirectory.size();
                    res += " players(s) \t";
                    res += id;
                    res += "\n";
                    gamesFound = true;
                }
            }
            if(gamesFound)
                return "The following are your games: \n" + res;
            else {
                return "No games are found.";
            }
        }
    }

    public static String onJoin(ChatInput in, int user_id, String username, Long chat_id ) {
        String context;
        if(in.getArgs().size() > 0) {
            context = in.getArgs().get(0);

            if (!_GameMaster.gamesListing.containsKey(context)) {
                return "The game does not exist.";
            } else if (_GameMaster.gamesListing.get(context).playerDirectory.containsKey(user_id)) {
                return "You are already in this game.";
            }
            else if (_GameMaster.gamesListing.get(context).state != GameState.QUEUE) {
                return "This game is no longer accepting players.";
            } else {
                _GameMaster.allPlayersAndTheirGames.put(user_id, context);
                _GameMaster.gamesListing.get(context).addUser(user_id, username, chat_id);

                return "You have successfully joined.";
            }
        }
        else {
            return "You did not provide a gameID";
        }
    }

    public static String onGetStatus(String game){
        if (!_GameMaster.gamesListing.containsKey(game)){
            return "Game not found.";
        }
        else {
            String res = "Status for " + game + ":\n";
            res += _GameMaster.gamesListing.get(game).playerDirectory.size();
            res += " player(s) \t";
            res += game;
            res += "\n";
            res += "Status: ";
            res += _GameMaster.gamesListing.get(game).state;
            res += "\n\n";
            return res;
        }
    }

    public static String onCreate(int user_id, String gameID, String username, long chat_id){
            Game game = new Game();// create new game
            game.setGameID(gameID);// give it the ID
            _GameMaster.gamesListing.put(gameID, game);
            _GameMaster.gamesListing.get(gameID).addUser(user_id, username, chat_id);

            _GameMaster.allPlayersAndTheirGames.put(user_id, gameID);

            _GameMaster.gamesListing.get(gameID).addObserver(_GameMaster.kineticEntity);
            return "Creating a new game session. \nGameID: " + gameID;
    }

    public static String onHelp(){
        return "Risk has the following commands available: \n" +
                "/create to create a new instance of the game\n" +
                "/join <gameID> to select an available session\n" +
                "/listAllGames to list all available game sessions\n" +
                "/listMyGames to list all your game sessions\n" +
                "/getStatus to get the status on the chat's current game\n" +
                "/load to replay a game.";
    }
}
