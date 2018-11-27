
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Observer;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import twitter4j.TwitterException;

/*////////////////////////////////////////////////////////////////////////////////
Provides window and clean space for calling necessary functions
*///////////////////////////////////////////////////////////////////////////////*/
class Fetcher implements Observer {

    @Override
    public void update(Observable obs, Object arg)
    {
        Game thisGame = (Game) obs;
        if(thisGame.state != GameState.QUEUE && thisGame.state == GameState.START)
        {
            // brief window here
        }
        else if(thisGame.state == GameState.INIT)
        {
            System.out.print("Initializing... ");
            thisGame.start();
        }
        else if(thisGame.state == GameState.CLAIM)
        {
            System.out.println("\nClaiming Territories... ");

        }
    }
}

/*////////////////////////////////////////////////////////////////////////////////
_GameMaster is the BOT that handles all chat commands and game hosting/handling
todo: make sure only one instance is currently running
todo: directory is a dangerous way of mapping a user to a game. Only one game per person for now
*///////////////////////////////////////////////////////////////////////////////*/
public class _GameMaster {
    static HashMap<String, Game> gamesListing;
    static HashMap<Integer, String> allPlayersAndTheirGames;
    static Fetcher kineticEntity;
    final static int MIN_PLAYERS_PER_GAME = 2;
    final static int MAX_PLAYERS_PER_GAME = 6;

    public static void main(String[] args) {
        gamesListing = new HashMap<>();
        kineticEntity = new Fetcher();
        allPlayersAndTheirGames = new HashMap<>();

        // Telegram
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();
        try{
            botsApi.registerBot(new CommandsHandler());
        } catch (TelegramApiException e)
        {
            e.printStackTrace();
        }
    }

}

/*////////////////////////////////////////////////////////////////////////////////
Bot is a proxy for games and players, it forwards output and input to respective
entities

*///////////////////////////////////////////////////////////////////////////////*/
class CommandsHandler extends TelegramLongPollingBot{

    // function to get the game being communicated with
    public Game getGame(Update update) {
        int user_id = update.getMessage().getFrom().getId();
        String gameID = _GameMaster.allPlayersAndTheirGames.get(user_id);
        Game game = _GameMaster.gamesListing.get(gameID);
        return game;
    }

    public Player getPlayer(Game game) {
        ArrayList<Integer> tempListing = new ArrayList<>();
        tempListing.addAll(game.playerDirectory.keySet());
        return game.playerDirectory.get(tempListing.get(game.turn % game.playerDirectory.size()));
    }

    @Override
    public void onUpdateReceived (Update update){

        if (update.hasMessage() && update.getMessage().hasText()){

            SendMessage message = new SendMessage();

            message.setChatId(update.getMessage().getChatId());

            ChatInput in = new ChatInput(CommandUtils.getInput(update.getMessage().getText()));

            switch(in.getCommand())
            {
                case "/start": {
                    message.setText(Responses.onStart());
                    break;
                }
                case "/listAllGames": {
                    message.setText(Responses.onListAllGames());
                    break;
                }

                case "/skipClaim": {
                    int user_id = update.getMessage().getFrom().getId();

                    Game game = getGame(update);

                    String msg = "";
                    ArrayList<Integer> tempListing = new ArrayList<>();
                    tempListing.addAll(game.playerDirectory.keySet());
                    for(String terr: game.BM.getFreeTerritories())
                    {
                        Player player = game.playerDirectory.get(tempListing.get(game.turn % game.playerDirectory.size()));
                        game.BM.initializeTerritory(player, terr, 1);
                        msg += (player.username + " chose " + terr + "\n");
                        game.turn += 1;
                    }
                    message.setText(msg);
                    break;
                }

                case "/listGamesS3": {
                    AWS aws = null;
                    try {
                        aws = new AWS();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ArrayList<String> games = aws.listObjects();
                    message.setText("Available games to load: \n");
                    for (String g: games) {
                        message.setText(g + "\n");
                    }
                    break;
                }

                case "/saveGame": {
                    AWS aws = null;
                    try {
                        aws = new AWS();
                        Game game = getGame(update);
                        aws.upload(game.gameID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    message.setText("your game has been saved");
                    break;
                }

                case "/undo": {
                    try {
                        AWS aws = new AWS();
                        aws.download(in.getArgs().get(0));
                        // create new loader & game using the input gameID
                        Loader loader = new Loader(in.getArgs().get(0));
                        _GameMaster.gamesListing.put(in.getArgs().get(0), loader.LoadGame());
                        message.setText("Turn undid");
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }

                // format -> /loadGame (gameID)
                case "/loadGame": {
                    try {
                        AWS aws = new AWS();
                        aws.download(in.getArgs().get(0));
                        // create new loader & game using the input gameID
                        Loader loader = new Loader(in.getArgs().get(0));
                        _GameMaster.gamesListing.put(in.getArgs().get(0), loader.LoadGame());
                        int turn = _GameMaster.gamesListing.get(in.getArgs().get(0)).turn;
                        message.setText("Game loaded, it is now the " + turn + " turn");
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case "/debugMyID": {
                    int id = update.getMessage().getFrom().getId();
                    String name = update.getMessage().getFrom().getUserName();
                    String fullName = update.getMessage().getFrom().getFirstName() + update.getMessage().getFrom().getLastName() ;
                    message.setText("Your ID: "+id + "\n Your USERNAME: "+name+ "\n Your NAME: "+ fullName);
                    break;
                }
                case "/join": {
                    //if(_GameMaster.allPlayersAndTheirGames.containsKey(update.getMessage().getFrom().getId())){
                    //    message.setText("@"+update.getMessage().getFrom().getUserName() + " sorry! you are already playing a game.");
                    //}
                    //else
                    message.setText(Responses.onJoin(in, update.getMessage().getFrom().getId(), update.getMessage().getFrom().getUserName(), update.getMessage().getChatId()));
                    break;
                }

                case "/getStatus": {
                    if (in.getArgs().size() > 0)
                    {
                        message.setText(Responses.onGetStatus(in.getArgs().get(0)));
                    } else {
                        message.setText("You did not provide a gameID.");
                    }
                    break;
                }

                case "/listMyGames": {
                    int id = update.getMessage().getFrom().getId();
                    message.setText(Responses.onListMyGames(id));
                    break;
                }

                case "/create": {
                    if(_GameMaster.allPlayersAndTheirGames.containsKey(update.getMessage().getFrom().getId())){
                        message.setText("@"+update.getMessage().getFrom().getUserName() + " sorry! You are already playing a game.");
                    } else{
                        message.setText(Responses.onCreate(
                                update.getMessage().getFrom().getId(),
                                "risk-game-" + UUID.randomUUID().toString(),
                                update.getMessage().getFrom().getUserName(),
                                update.getMessage().getChatId())
                        );
                    }
                    break;
                }

                case "/help": {
                    message.setText(Responses.onHelp());
                    break;
                }

                // function to pick territory, there may be a way to message a player directly and in turn order given a player's user_id and turn number
                case "/pick": {
                    int user_id = update.getMessage().getFrom().getId();
                    String gameID = _GameMaster.allPlayersAndTheirGames.get(user_id);

                    Game game = getGame(update);
                    ArrayList<Integer> tempListing = new ArrayList<>();
                    tempListing.addAll(game.playerDirectory.keySet());

                    if(user_id == game.nextTurnUserID)
                    {
                        Player player = getPlayer(game);

                        String tempTerritory = String.join(" ", in.getArgs());
                        if(tempTerritory.equals(""))
                            message.setText("You did not put a country to claim.");
                        else {
                            // SUCCESS
                            game.BM.initializeTerritory(player, tempTerritory, 1);
                            String out = "@" + player.username + " chose " + tempTerritory + ".\n";
                            game.turn += 1;

                            if(game.BM.getFreeTerritories().size() != 0)
                            {
                                out += "\nIt is now player @" + game.playerDirectory.get(tempListing.get(game.turn % game.playerDirectory.size())).username + "'s turn\n";
                                out += "The following territories are still available\n";
                                List<String> territories = _GameMaster.gamesListing.get(gameID).BM.getFreeTerritories();
                                for (String territory : territories) {
                                    out += (territory + "\n");
                                }
                                game.nextTurnUserID = game.playerDirectory.get(tempListing.get(game.turn % game.playerDirectory.size())).id;
                            } else {
                                ArrayList<Integer> users = new ArrayList<Integer>();
                                users.addAll(game.playerDirectory.keySet());
                                game.nextTurnUserID = game.playerDirectory.get(users.get(0)).id;
                            }

                            message.setText(out);
                        }

                    } else {
                        message.setText("Uh Oh! It is not your turn player#" + user_id + ", it is player#"+game.nextTurnUserID+ "'s turn.");
                    }

                    break;
                }

                case "/reinforce": {
                    Game game = getGame(update);
                    Player player = getPlayer(game);

                    String tempTerritory = String.join(" ", in.getArgs());
                    game.BM.strengthenTerritory(player, tempTerritory, 1);
                    String out = player.username + " you have " + player.getNumberOfArmies() + "left\n";
                    game.turn += 1;
                    out += "It is now player " + game.turn % game.playerDirectory.size() + " turn";
                    message.setText(out);
                    break;
                }

                case "/listFreeTerritories": {
                    int user_id = update.getMessage().getFrom().getId();
                    if(_GameMaster.allPlayersAndTheirGames.containsKey(user_id))
                    {
                        String gameID = _GameMaster.allPlayersAndTheirGames.get(user_id);
                            if(_GameMaster.gamesListing.get(gameID).state == GameState.QUEUE || _GameMaster.gamesListing.get(gameID).state == GameState.INIT )
                            {
                                message.setText("The game has not yet started.");

                            } else if (_GameMaster.gamesListing.get(gameID).state == GameState.CLAIM) {
                                Messenger msg = _GameMaster.gamesListing.get(gameID).messenger;
                                msg.putMessage(_GameMaster.gamesListing.get(gameID).BM.getFreeTerritories());
                                message.setText(msg.getMessage());
                            }
                            else{
                                message.setText("There is no territory to claim.");
                            }

                    }else{
                        message.setText("You are not playing a game.");
                    }
                    break;
                }

                // message should be formatted /attack (attack territory) (defend territory) (Number of armies to attack with) (number of armies to defend with)
                case "/attack": {
                    Game game = getGame(update);
                    String attacker = null;
                    String defender = null;
                    int i = 0; // number to keep track of the size of the country inputs

                    if (game.BM.boardMap.containsKey(in.getArgs().get(0))) {
                        attacker = in.getArgs().get(0);
                        i = 1;
                    }
                    else if (game.BM.boardMap.containsKey(in.getArgs().get(0) + " " + in.getArgs().get(1))) {
                        attacker = in.getArgs().get(0) + " " + in.getArgs().get(1);
                        i = 2;
                    }
                    else if (game.BM.boardMap.containsKey(in.getArgs().get(0) + " " + in.getArgs().get(1) + " " + in.getArgs().get(2))) {
                        attacker = in.getArgs().get(0) + " " + in.getArgs().get(1) + " " + in.getArgs().get(2);
                        i = 3;
                    }

                    if (game.BM.boardMap.containsKey(in.getArgs().get(i))) {
                        defender = in.getArgs().get(i);
                        i += 1;
                    }
                    else if (game.BM.boardMap.containsKey(in.getArgs().get(i) + " " + in.getArgs().get(i+1))) {
                        defender = in.getArgs().get(i) + " " + in.getArgs().get(i+1);
                        i += 2;
                    }
                    else if (game.BM.boardMap.containsKey(in.getArgs().get(i) + " " + in.getArgs().get(i+1) + " " + in.getArgs().get(i+2))) {
                        defender = in.getArgs().get(i) + " " + in.getArgs().get(i+1) + " " + in.getArgs().get(i+2);
                        i += 3;
                    }

                    int attackDie = Integer.parseInt(in.getArgs().get(i));
                    int defenseDie = Integer.parseInt(in.getArgs().get(i+1));

                    Turn turn = game.currentTurn;
                    message.setText(turn.battle(attacker, defender, attackDie, defenseDie));
                    break;
                }

                // message should be formatted /fortify (move from) (move to) (Num armies)
                case "/fortify": {
                    Game game = getGame(update);
                    String from = null;
                    String to = null;
                    int i = 0; // number to keep track of the size of the country inputs

                    if (game.BM.boardMap.containsKey(in.getArgs().get(0))) {
                        from = in.getArgs().get(0);
                        i = 1;
                    }
                    else if (game.BM.boardMap.containsKey(in.getArgs().get(0) + " " + in.getArgs().get(1))) {
                        from = in.getArgs().get(0) + " " + in.getArgs().get(1);
                        i = 2;
                    }
                    else if (game.BM.boardMap.containsKey(in.getArgs().get(0) + " " + in.getArgs().get(1) + " " + in.getArgs().get(2))) {
                        from = in.getArgs().get(0) + " " + in.getArgs().get(1) + " " + in.getArgs().get(2);
                        i = 3;
                    }

                    if (game.BM.boardMap.containsKey(in.getArgs().get(i))) {
                        to = in.getArgs().get(i);
                        i += 1;
                    }
                    else if (game.BM.boardMap.containsKey(in.getArgs().get(i) + " " + in.getArgs().get(i+1))) {
                        to = in.getArgs().get(i) + " " + in.getArgs().get(i+1);
                        i += 2;
                    }
                    else if (game.BM.boardMap.containsKey(in.getArgs().get(i) + " " + in.getArgs().get(i+1) + " " + in.getArgs().get(i+2))) {
                        to = in.getArgs().get(i) + " " + in.getArgs().get(i+1) + " " + in.getArgs().get(i+2);
                        i += 3;
                    }

                    int turnNo = game.turn % game.playerDirectory.size();
                    Player player = game.playerDirectory.get(turnNo);
                    int transfer = Integer.parseInt(in.getArgs().get(i));
                    game.BM.fortifyTerritory(from,to,transfer);
                }


                // message format -> /buycredit (credit amount)
                case "/buycredit": {
                    Game game = getGame(update);
                    int turnNo = game.turn % game.playerDirectory.size();
                    Player player = game.playerDirectory.get(turnNo);
                    player.addMoney(Double.parseDouble(in.getArgs().get(0)));
                }

                // format -> /buyshit (# undos to buy) (# cards to buy)
                case "/buystuff": {
                    Game game = getGame(update);
                    int turnNo = game.turn % game.playerDirectory.size();
                    Player player = game.playerDirectory.get(turnNo);

                    Double cash = player.getWallet();

                    int undos = Integer.parseInt(in.getArgs().get(0));
                    if (undos * 1000 < cash) {
                        player.addUndos(undos);
                        player.addMoney( undos * 1000 * -1);
                    }
                    Double cards = Double.valueOf(in.getArgs().get(1));
                    if (cards * 100 < cash) {
                        for (int i = 0; i < cards; i++) {
                            Card c = game.BM.getGameDeck().draw();
                            if(c != null) player.getHand().get(c.getUnit()).push(c);
                        }
                        player.addMoney( cards * 100 * -1);
                    }
                }

                case "/endturn": {
                    Game game = getGame(update);

                    // Write game to save game file
                    try {
                        JSONhandler JSONhandler = new JSONhandler(game);
                        JSONhandler.JSONwriter();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // broadcast to Twitter
                    Twitter tw = new Twitter();
                    try {
                        tw.broadcastToTwitter(game.currentTurn, game.currentTurn.player);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }

                    // move to next turn
                    game.turn += 1;
                    int turnNo = game.turn % game.playerDirectory.size();
                    Player player = game.playerDirectory.get(turnNo);
                    message.setText("Player " +player.getUsername()+ " it is now your turn, type /beginTurn to begin your turn");
                    break;
                }

                case "/beginTurn": {
                    Game game = getGame(update);
                    int turnNo = game.turn % game.playerDirectory.size();
                    Player player = getPlayer(game);
                    message.setText("Player " +player.getUsername()+ " may /reinforce then /attack then /fortify then /buycredit then /buyshit only in that order or / then type /endturn to move to next player, ending your turn\n");
                    Turn turn = new Turn(game.BM, player, game.turn);
                    game.setCurrentTurn(turn);
                    try {
                        player.addArmies(turn.getArmiesFromCards() + turn.getFreeArmiesFromTerritories());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    message.setText("You have " + player.getNumberOfArmies() + " available to place\n");
                    message.setText(turn.getAttackableTerritories());
                    message.setText("Your hand currently includes: ");
                    ArrayList<Card> cards = player.getHandListing();
                    for (Card c: cards) {
                        message.setText(c.getOrigin() + ": " + c.getUnit());
                    }
                    message.setText("You currently have:\n");
                    message.setText("\t" + player.getUndos() + " undos\n");
                    message.setText("\t" + player.getWallet() + " credit");
                    break;
                }

                default:
                    message.setText("Command " + in.getCommand() + " not found.\n\n" + Responses.onHelp());
                    break;
            }

            try{
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            String res = "";

            // follow up messages
                // GAME START ANNOUNCEMENT -- GameState.START is when this /join triggered the game start, then take the gameID and broadcast to all chats
                if (in.getCommand().equals("/join") && _GameMaster.gamesListing.containsKey(in.args.get(0)) && _GameMaster.gamesListing.get(in.args.get(0)).playerDirectory.size() == 2 && in.args.size() > 0) {

                    String context = in.args.get(0);

                    ArrayList<Long> chat_reply_list = new ArrayList<>();
                    SendMessage announcement = new SendMessage();

                    // add most sender of join command to the playerDirectory
                    message.setText(Responses.onJoin(in, update.getMessage().getFrom().getId(), update.getMessage().getFrom().getUserName(), update.getMessage().getChatId()));

                    for (int user_id : _GameMaster.gamesListing.get(context).playerDirectory.keySet()) {
                        if (!chat_reply_list.contains(_GameMaster.gamesListing.get(context).playerDirectory.get(user_id).chat_id)) {
                            chat_reply_list.add(_GameMaster.gamesListing.get(context).playerDirectory.get(user_id).chat_id);
                        }
                        res += "@";
                        res += _GameMaster.gamesListing.get(context).playerDirectory.get(user_id).username;
                        res += " ";
                    }
                    res += "\n";

                    _GameMaster.gamesListing.get(context).start();

                    String fromMessenger = _GameMaster.gamesListing.get(context).messenger.getMessage();
                    // fromMessenger should never be null for init, otherwise logical error
                    if (fromMessenger != null) {
                        res += "\n";
                        res += fromMessenger;
                    }

                    res += "\n\n";
                    res += "To begin claiming your initial territories, enter /listFreeTerritories to get the list of available territories again." +
                            " The list is automatically shown below. \n\n" +
                            "__AVAILABLE TERRITORIES__";

                    Messenger tempMSG = _GameMaster.gamesListing.get(context).messenger;
                    tempMSG.putMessage(_GameMaster.gamesListing.get(context).BM.getFreeTerritories());

                    fromMessenger = _GameMaster.gamesListing.get(context).messenger.getMessage();
                    if (fromMessenger != null) {
                        res += "\n";
                        res += fromMessenger;
                    }

                    ArrayList<Integer> tempListing = new ArrayList<>();
                    tempListing.addAll(_GameMaster.gamesListing.get(context).playerDirectory.keySet());
                    Game game = _GameMaster.gamesListing.get(context);
                    res += "\nIt is now player @" +
                            game.playerDirectory.get(tempListing.get(game.turn % game.playerDirectory.size())).username + "'s turn";
                    res += "\nEnter /pick <country> to select and capture your initial territories. ";

                    for (Long dest : chat_reply_list) {
                        announcement.setChatId(dest);
                        announcement.setText("Your game " + _GameMaster.gamesListing.get(context).gameID + " is now starting. " + res);
                        try {
                            execute(announcement);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if((in.getCommand().equals("/pick") || (in.getCommand().equals("/skipClaim")) && _GameMaster.gamesListing.get(getGame(update).gameID).BM.getFreeTerritories().size() == 0))
                {
                    Game thisGame = getGame(update);
                    SendMessage announcement = new SendMessage();
                    announcement.setChatId(update.getMessage().getChatId());
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

                    announcement.setText(out);
                    try {
                        execute(announcement);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    @Override
    public String getBotUsername(){
        try {
            Props k = new Props();
            return k.getBot_name();
        } catch(IOException e)
        {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String getBotToken(){
        try {
            Props k = new Props();
            return k.getBot_apiToken();
        } catch(IOException e)
        {
            e.printStackTrace();
            return "";
        }
    }

}
