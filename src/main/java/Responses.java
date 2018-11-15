import java.util.ArrayList;

public class Responses {

    public static String onStart(){
        return "Hi! You called /start. To play risk, please select an action below: \n\n" +
                "/create to create a new instance of the game\n" +
                "/listAllGames to list all available game sessions\n" +
                "/listMyGames to list all your game sessions\n" +
                "/join <id> to select an available session\n" +
                "/load to replay a game.";
    }
    public static String onListAllGames(ArrayList<Game> listing){
        if(listing.size() == 0){
            return "There are currently no sessions being played. To create a game session, type /create";
        } else {
            String res = (listing.size() + " live game sessions have been found. Select one to join: ");
            for (Game k : listing) {
                res += ("\n" + k.gameID);
            }
            return res;
        }
    }

    public static String onListMyGames(int id){
        String games = "";
        boolean taken = false;

        String res = "Your games: \n";
        for( Game each: _GameMaster.gamesListing) {
            if(each.playerList.contains(id)){
                taken = true;
                games += each.gameID + "\n";
            }
        }
        if(!taken){
            games = "You currently do not have any games.";
        }
        return res + games;
    }

    public static String onJoin(ChatInput in, int id) {
        String res = "";
        for(Game each: _GameMaster.gamesListing){
            if(each.gameID.equals(in.getArgs().get(0)) && !each.playerList.contains(id) && each.playerList.size() < _GameMaster.MAX_PLAYERS_PER_GAME && each.state == GameState.QUEUE)
            {
                res = "You successfully joined a game.";
                each.playerList.add(id);


                break;
            } else if(each.playerList.contains(id)) {
                res = "You're already playing this game.";
                break;
            }
            else {
                res = "Uh Oh! You cannot the join this game. This game is either full or it has already commenced";
                break;
            }
        }
        return res;

    }

    public static String onGetStatus(int id){
        String res = "";
        boolean taken = false;
        for(Game each: _GameMaster.gamesListing){
            if(!each.playerList.contains(id))
            {
                taken = true;
                res += "ID=" + each.gameID + "\n";

                switch(each.state)
                {
                    case QUEUE:{
                        res += "STATE=In Queue\n";
                        break;
                    }
                    case WAIT:{
                        res += "STATE=Waiting on a turn\n";
                        break;
                    }
                    case INIT:{
                        res += "STATE=Game has started.\n";
                        break;
                    }
                    case END:{
                        res += "STATE=Game ended.\n";
                        break;
                    }
                    default:
                        res += "STATE=unknown\n";
                }
                res += "PLAYERS="+each.playerList.size();
            }
        }

        if(!taken)
            res += "Your games: \nYou currently do not have any games.";
        return res;
    }

    public static String onCreate(String id){
        return "Creating a new game session. The game's ID is " + id;
    }

    public static String onHelp(){
        return "Risk has the following commands available: \n";
    }
}
