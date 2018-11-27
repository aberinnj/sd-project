/*////////////////////////////////////////////////////////////////////////////////

*///////////////////////////////////////////////////////////////////////////////*/

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
                    res += " player(s) \t";
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
            //else if (_GameMaster.gamesListing.get(context).state != GameState.QUEUE) {
            //    return "This game is no longer accepting players.";
            //}
            else {
                _GameMaster.allPlayersAndTheirGames.put(user_id, context);
                _GameMaster.gamesListing.get(context).addUser(user_id, username, chat_id);

                return "You have successfully joined.";
            }
        }
        else {
            return "You did not provide a gameID";
        }
    }

    public static String onCreate(int user_id, String gameID, String username, long chat_id){
        if(_GameMaster.allPlayersAndTheirGames.containsKey(user_id)){
            return "@"+username + " sorry! You are already playing a game.";
        }
        else {
            Game game = new Game();// create new game
            game.setGameID(gameID);// give it the ID
            _GameMaster.gamesListing.put(gameID, game);
            _GameMaster.gamesListing.get(gameID).addUser(user_id, username, chat_id);

            _GameMaster.allPlayersAndTheirGames.put(user_id, gameID);

            //_GameMaster.gamesListing.get(gameID).addObserver(_GameMaster.kineticEntity);
            return "Creating a new game session. \nGameID: " + gameID;
        }
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

    public static String onSkipReinforce(Game game){
        String msg = "";
        while(!CommandUtils.isReinforcingOver(game))
        {
            Player nextPlayer = CommandUtils.getPlayer(game);
            if(nextPlayer.getNumberOfArmies() > 0)
            {
                String terr = nextPlayer.getTerritories().get(nextPlayer.getNumberOfArmies() % nextPlayer.getTerritories().size());
                msg +=  nextPlayer.username + " reinforces " + terr + "\n";
                game.BM.strengthenTerritory(nextPlayer, terr, 1);
                game.turn++;

            }
        }

        game.nextTurnUserID = CommandUtils.getFirstPlayer(game.playerDirectory).id;
        return msg;
    }

    public static String onSkipClaim(Game game)
    {
        String msg = "";
        ArrayList<Integer> tempListing = new ArrayList<>();
        tempListing.addAll(game.playerDirectory.keySet());
        for(String terr: game.BM.getFreeTerritories())
        {
            Player player = game.playerDirectory.get(tempListing.get(game.turn % game.playerDirectory.size()));
            game.BM.initializeTerritory(player, terr, 1);
            msg += (player.username + " chose " + terr + "\n");
            game.turn += 1;

            Player nextNextPlayer = CommandUtils.getPlayer(game);
            game.nextTurnUserID = nextNextPlayer.id;
        }

        game.nextTurnUserID = CommandUtils.getFirstPlayer(game.playerDirectory).id;
        return msg;
    }

    public static String onPick(ChatInput in, Integer user_id, Game game ){
        if(user_id == game.nextTurnUserID)
        {
            Player player = CommandUtils.getPlayer(game);

            String tempTerritory = String.join(" ", in.getArgs());
            if(tempTerritory.equals(""))
                return "You did not put a country to claim.";
            else if(!game.BM.getFreeTerritories().contains(tempTerritory))
            {
                return "This territory has already been claimed.";
            }
            else {
                // SUCCESS
                game.BM.initializeTerritory(player, tempTerritory, 1);
                String out = "@" + player.username + " chose " + tempTerritory + ".\n";
                game.turn += 1;


                if(game.BM.getFreeTerritories().size() != 0)
                {
                    Player nextPlayer = CommandUtils.getPlayer(game);
                    out += "\nIt is now player @" + nextPlayer.username + "'s turn\n";
                    out += "The following territories are still available\n";
                    List<String> territories = game.BM.getFreeTerritories();
                    for (String territory : territories) {
                        out += (territory + "\n");
                    }
                    game.nextTurnUserID = nextPlayer.id;
                } else {
                    ArrayList<Integer> users = new ArrayList<Integer>();
                    users.addAll(game.playerDirectory.keySet());
                    game.nextTurnUserID = game.playerDirectory.get(users.get(0)).id;
                }
                return out;
            }


        } else {
            return "Uh Oh! It is not your turn player#" + user_id + ", it is player#"+game.nextTurnUserID+ "'s turn.";
        }
    }

    public static String onReinforce(ChatInput in, Integer user_id, Game game){
        String out;
        Player player = CommandUtils.getPlayer(game);
        if(player.getNumberOfArmies() == 0)
        {
            return "You already have dispatched all available armies";
        }
        else if(user_id == game.nextTurnUserID)
        {
            String tempTerritory = String.join(" ", in.getArgs());
            if (tempTerritory.equals(""))
            {
                return "You did not put a country to reinforce.";
            }
            else if (!player.getTerritories().contains(tempTerritory))
            {
                return "You do not own this territory.";
            }
            else
            {
                out = "@"+player.username + " reinforces " + tempTerritory;
                game.BM.strengthenTerritory(player, tempTerritory, 1);
                out += "\n@"+player.username + " you have " + player.getNumberOfArmies() + " armies left\n";

                return out;
            }
        } else {
            return "Uh Oh! It is not your turn player #" + user_id + ", it is player #"+ game.nextTurnUserID+ "'s turn.";
        }
    }

    public static String onFollowUpJoin(String context){
        _GameMaster.gamesListing.get(context).start();
        String res = "Order:\n";
        for (int user_id : _GameMaster.gamesListing.get(context).playerDirectory.keySet()) {
            res += "@";
            res += _GameMaster.gamesListing.get(context).playerDirectory.get(user_id).username;
            res += "\n";
        }

        res += "\n\n";
        res += "To begin claiming your initial territories, enter /listFreeTerritories to get the list of available territories again." +
                " The list is automatically shown below. \n\n" +
                "__AVAILABLE TERRITORIES__";
        for(String each: _GameMaster.gamesListing.get(context).BM.getFreeTerritories())
        {
            res += "\n"+each;
        }

        ArrayList<Integer> tempListing = new ArrayList<>();
        tempListing.addAll(_GameMaster.gamesListing.get(context).playerDirectory.keySet());
        Game game = _GameMaster.gamesListing.get(context);
        res += "\n\nIt is now player @" +
                game.playerDirectory.get(tempListing.get(game.turn % game.playerDirectory.size())).username + "'s turn";
        res += "\nEnter /pick <country> to select and capture your initial territories. ";

        return "Your game " + _GameMaster.gamesListing.get(context).gameID + " is now starting. " + res;
    }

    public static String onFollowUpInitPick(Game thisGame){
        String out = "Initial territory claiming is complete." +
                "\nPlayers have remaining armies to dispatch to their territories. Please select a territory to dispatch your remaining armies to and fortify. \n" +
                "\n/reinforce <country> to select a country to dispatch one(1) army to." +
                "\n/listMyTerritories to view your territories and their status. (not implemented yet)" +
                "\n\nIt is now player @" + thisGame.playerDirectory.get(0).username + "'s turn:" +
                "\nYour territories you can reinforce";


        for(String k: thisGame.playerDirectory.get(0).getTerritories())
        {
            out += "\n"+k;
        }

        thisGame.state = GameState.CLAIMING;
        return out;
    }

    public static String onFollowUpReinforce(Game game){
        String out = "";
        if(CommandUtils.isReinforcingOver(game) && game.state == GameState.CLAIMING)
        {
            game.state = GameState.ON_TURN;
            out = "Initial territory reinforcing is complete." +
                    "\nPlayers can now begin making turns\n" +
                    "\n/beginTurn to begin your turn" +
                    "\n/endTurn to end your turn" +
                    "\n\nIt is now player @" + game.playerDirectory.get(0).username + "'s turn:";

        } else if(game.state == GameState.CLAIMING){
            game.turn += 1;
            Player nextPlayer = CommandUtils.getPlayer(game);
            out = "\nIt is now player @" + nextPlayer.username + "'s turn";
            out += "\nYour territories are:";
            for(String i: nextPlayer.getTerritories())
            {
                out += "\n"+i;
            }

            game.nextTurnUserID = nextPlayer.id;
        }
        else if (game.state == GameState.ON_TURN )
        {
            Player nextPlayer = CommandUtils.getPlayer(game);

            if(nextPlayer.getNumberOfArmies() == 0)
            {
                out += "\n/attack <invading> <defending> <number of armies to attack with MAX.3> <number of armies to defend with MAX.2>" +
                        "\n/fortify <fortify from> <fortify neighbor> <number of armies to transfer>" +
                        "\n/buycredit <amount> to buy credit" +
                        "\n/buystuff <# of undos> <# of cards> to buy stuff with your credits" +
                        "\n/endturn to finally end your turn";
            }
            else {
                out += "\nCurrently, your territories are:";
                for (String i : nextPlayer.getTerritories()) {
                    out += "\n" + game.BM.getBoardMap().get(i).getArmy().getInfantryCount() + " armies -- " + i;
                }
            }
        }

        else {
            out = "/reinforce is done.";
        }
        return out;


    }

    public static String onBeginTurn(Game game)
    {
        Player player = CommandUtils.getPlayer(game);
        String out = ("Player @" +player.getUsername()+ " has begin their turn. You may: \n" +
                "\n/tradecards to trade your cards if you have pairs" +
                "\n/reinforce <country> to reinforce new free armies to your territory" +
                "\n/attack <invading> <defending> <number of armies to attack with MAX.3> <number of armies to defend with MAX.2>" +
                "\n/fortify <fortify from> <fortify neighbor> <number of armies to transfer>" +
                "\n/buycredit <amount> to buy credit" +
                "\n/buystuff <# of undos> <# of cards> to buy stuff with your credits" +
                "\n/endturn to finally end your turn");
        Turn turn = new Turn(game.BM, player, game.turn);
        game.setCurrentTurn(turn);
        try {
            int freebies = turn.getArmiesFromCards() + turn.getFreeArmiesFromTerritories();
            System.out.println(freebies);
            player.addArmies(freebies);
            System.out.println(player.getNumberOfArmies());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        out += ("\n\nYou have " + player.getNumberOfArmies() + " available armies to reinforce\n\n");
        out += ("\nFor attacking, you have the following able territories: \n");
        out += (turn.getAttackableTerritories());
        out += ("\n__YOU HAVE___");
        ArrayList<Card> cards = player.getHandListing();
        if (cards.isEmpty())
        {
            out += "\n\nNo Cards\n";
        } else {
            for (Card c : cards) {
                out += ("\n"+c.getOrigin() + ": " + c.getUnit());
            }
            out += "\n";
        }
        out += ("\n\t" + player.getUndos() + " undos");
        out += ("\n\t" + player.getWallet() + " credit");

        return out;
    }
}
