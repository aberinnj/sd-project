
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
/*
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
}*/

/*////////////////////////////////////////////////////////////////////////////////
_GameMaster is the BOT that handles all chat commands and game hosting/handling
todo: make sure only one instance is currently running
todo: directory is a dangerous way of mapping a user to a game. Only one game per person for now
*///////////////////////////////////////////////////////////////////////////////*/
public class _GameMaster {
    static HashMap<String, Game> gamesListing;
    static HashMap<Integer, String> allPlayersAndTheirGames;

    public static void main(String[] args) {
        gamesListing = new HashMap<>();
        //kineticEntity = new Fetcher();
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
                case "/skipReinforce": {
                    // THESE ARE FOR TESTING, REMOVE IF NEED BE
                    message.setText(Responses.onSkipReinforce(CommandUtils.getGame(update.getMessage().getFrom().getId())));
                    break;

                }

                case "/skipClaim": {
                    // THESE ARE FOR TESTING, REMOVE IF NEED BE
                    message.setText(Responses.onSkipClaim(CommandUtils.getGame(update.getMessage().getFrom().getId())));
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
                        Game game = CommandUtils.getGame(update.getMessage().getFrom().getId());
                        aws.upload(game.gameID);
                    } catch (IOException e) {
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
                    }
                    break;
                }
                case "/join": {
                    message.setText(Responses.onJoin(in, update.getMessage().getFrom().getId(), update.getMessage().getFrom().getUserName(), update.getMessage().getChatId()));
                    break;
                }

                case "/listMyGames": {
                    message.setText(Responses.onListMyGames(update.getMessage().getFrom().getId()));
                    break;
                }

                case "/create": {
                        message.setText(Responses.onCreate(
                                update.getMessage().getFrom().getId(),
                                "risk-game-" + UUID.randomUUID().toString(),
                                update.getMessage().getFrom().getUserName(),
                                update.getMessage().getChatId())
                        );
                    break;
                }

                case "/help": {
                    message.setText(Responses.onHelp());
                    break;
                }

                // function to pick territory, there may be a way to message a player directly and in turn order given a player's user_id and turn number
                case "/pick": {
                    message.setText(Responses.onPick(
                            in,
                            update.getMessage().getFrom().getId(),
                            CommandUtils.getGame(update.getMessage().getFrom().getId())));
                    break;
                }

                case "/reinforce": {
                    message.setText(Responses.onReinforce(
                            in,
                            update.getMessage().getFrom().getId(),
                            CommandUtils.getGame(update.getMessage().getFrom().getId())));

                    break;
                }

                // message should be formatted /attack (attack territory) (defend territory) (Number of armies to attack with) (number of armies to defend with)
                case "/attack": {
                    Game game = CommandUtils.getGame(update.getMessage().getFrom().getId());
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
                    Game game = CommandUtils.getGame(update.getMessage().getFrom().getId());
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

                // assumes it is your turn, checks your hand for three matching cards, pops them from your hand and gives you the armies
                case "/tradecards": {
                    Game game = CommandUtils.getGame(update.getMessage().getFrom().getId());
                    Player player = CommandUtils.getPlayer(game);
                    Turn turn = game.currentTurn;

                }

                // message format -> /buycredit (credit amount)
                case "/buycredit": {
                    Game game = CommandUtils.getGame(update.getMessage().getFrom().getId());
                    int turnNo = game.turn % game.playerDirectory.size();
                    Player player = game.playerDirectory.get(turnNo);
                    player.addMoney(Double.parseDouble(in.getArgs().get(0)));
                }

                // format -> /buyshit (# undos to buy) (# cards to buy)
                case "/buystuff": {
                    Game game = CommandUtils.getGame(update.getMessage().getFrom().getId());
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
                    Game game = CommandUtils.getGame(update.getMessage().getFrom().getId());
                    Turn turn = game.currentTurn;
                    try {
                        turn.earnCards();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

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
                    Game game = CommandUtils.getGame(update.getMessage().getFrom().getId());
                    int turnNo = game.turn % game.playerDirectory.size();
                    Player player = CommandUtils.getPlayer(game);
                    message.setText("Player " +player.getUsername()+ " may /tradecards /reinforce then /attack then /fortify then /buycredit then /buyshit only in that order or / then type /endturn to move to next player, ending your turn\n");
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
                else if((in.getCommand().equals("/pick") || (in.getCommand().equals("/skipClaim"))) && _GameMaster.gamesListing.get(CommandUtils.getGame(update.getMessage().getFrom().getId()).gameID).BM.getFreeTerritories().size() == 0)
                {
                    Game thisGame = CommandUtils.getGame(update.getMessage().getFrom().getId());
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
                else if (in.getCommand().equals("/reinforce") || in.getCommand().equals("/skipReinforce"))
                {
                    Game game = CommandUtils.getGame(update.getMessage().getFrom().getId());

                    if(CommandUtils.isReinforcingOver(game))
                    {
                        SendMessage announcement = new SendMessage();
                        announcement.setChatId(update.getMessage().getChatId());
                        String out = "Initial territory reinforcing is complete." +
                                "\nPlayers can now begin making turns\n" +
                                "\n/beginTurn to begin your turn" +
                                "\n/endTurn to end your turn" +
                                "\n\nIt is now player @" + game.playerDirectory.get(0).username + "'s turn:";

                        announcement.setText(out);
                        try {
                            execute(announcement);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
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
