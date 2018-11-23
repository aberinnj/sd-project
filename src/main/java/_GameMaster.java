
import java.io.IOException;
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

import static java.lang.Math.toIntExact;

enum GameState {
    QUEUE, // the default state
    START, // when a game gets 3 players, this is the state until functions are called (a brief window) and the state is changed again
    INIT, // the state when the game has started
    CLAIM, // the state when the game allows for players to claim territories
    ARMIES, // same as CLAIM except armies instead of territories
    TURNS, // state for running actual turns
    WAIT, // the state when the gmae is waiting on a player
    THINK, // the neutral state to switch from WAIT or to WAIT -- thinking
    PAUSE, // the state when the timer is set off (future functionality)
    END, // the state when the game has ended until functions are called (another brief window)
    CLOSED // the final state, when all functions are called
}

/*////////////////////////////////////////////////////////////////////////////////
High-level way of managing user reply below in onUpdateReceived
Used to check if input is what was expected
More descriptive than GameState
todo: expected context is a todo
*///////////////////////////////////////////////////////////////////////////////*/
enum Context {

};

class ExpectedContext {
    Integer expectedReplier;
    String actualReply;

    ExpectedContext(){
        expectedReplier = 0;
        actualReply = "";
    }

    void setExpectedReplier(Integer id)
    {
        expectedReplier = id;
    }
    void setActualReply(String msg){
        actualReply = msg;
    }
    String getActualReply(){
        return actualReply;
    }

    Integer getExpectedReplier(){
        return expectedReplier;
    }
}

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
            try{
                System.out.print("Initializing... ");
                thisGame.game.initGame(thisGame);
            }
            catch(IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if(thisGame.state == GameState.CLAIM)
        {
            System.out.println("\nClaiming Territories... ");

        }
    }
}

/*////////////////////////////////////////////////////////////////////////////////
Game holds all each game's information and the list of players in the game
todo: check if static is causing problems
*///////////////////////////////////////////////////////////////////////////////*/
class Game extends Observable {
    _GameStarter game;
    HashMap<Integer, Player> playerDirectory;
    GameManager GM;
    BoardManager BM;
    ArrayList<Integer> users;
    //ArrayList<Integer> turnPattern;
    ArrayList<Player> turnPattern;
    int turn;
    String gameID;
    GameState state;
    Messenger messenger;
    ExpectedContext EC;
    Turn currentTurn;


    Game(String id) {
        messenger = new Messenger();
        game = new _GameStarter();
        playerDirectory = new HashMap<>();
        GM = new GameManager();
        BM = GM.getBM();
        users = new ArrayList<>();
        turnPattern = new ArrayList<>();
        turn = 0;
        gameID = id;
        state = GameState.QUEUE;
        EC = new ExpectedContext();

    }

    public void addUser(Integer user_id, String username, long chat_id){

        //playerDirectory.put(user_id, new User(user_id, username, chat_id));

        // Create new player to add to the list of players for the game
        playerDirectory.put(playerDirectory.size(), new Player(user_id, username, chat_id, 0));
        users.add(user_id);

        if(playerDirectory.size() == _GameMaster.MIN_PLAYERS_PER_GAME)
        {
            state = GameState.START;
            setChanged();
            notifyObservers();
        }

    }

    public void begin()
    {
        if(playerDirectory.size() == _GameMaster.MIN_PLAYERS_PER_GAME) {
                state = GameState.INIT;
                setChanged();
                notifyObservers();
        } else {
            System.out.println("ERROR: NOT ENOUGH PLAYERS SOMEHOW");
        }
    }

    public void claim()
    {
        if(playerDirectory.size() == _GameMaster.MIN_PLAYERS_PER_GAME && state == GameState.INIT) {
            state = GameState.CLAIM;
            setChanged();
            notifyObservers();
        } else {
            System.out.println("ERROR: NOT ENOUGH PLAYERS SOMEHOW");
        }
    }

    public void beginTurns() {
        state = GameState.TURNS;
        setChanged();
        notifyObservers();
    }

    public void beginDistribute() {
        state = GameState.ARMIES;
        setChanged();
        notifyObservers();
    }

    public void setTurnPattern(int i, int roll) {
        turnPattern.set(i, playerDirectory.get(roll));
    }

    public void setCurrentTurn(Turn turn) { this.currentTurn = turn;}

    //function to determine it all players have distributed their armies
    public boolean checkArmies() {
        for (int key: playerDirectory.keySet()) {
            if (playerDirectory.get(key).getNumberOfArmies() > 0) return false;
        }
        return true;
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

Contextual problems.
Remember: Once a user /joins or /creates in a chat, that is where they're gonna have
the game for the rest of the game.

It would help to have a list of known inputs to determine the context of a message.
This is when the player is playing multiple games in the same chat.
*///////////////////////////////////////////////////////////////////////////////*/
class CommandsHandler extends TelegramLongPollingBot{

    // function to get the game being communicated with
    public Game getGame(Update update) {
        int user_id = update.getMessage().getFrom().getId();
        String gameID = _GameMaster.allPlayersAndTheirGames.get(user_id);
        Game game = _GameMaster.gamesListing.get(gameID);
        return game;
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

                case "/debugMyID": {
                    int id = update.getMessage().getFrom().getId();
                    String name = update.getMessage().getFrom().getUserName();
                    String fullName = update.getMessage().getFrom().getFirstName() + update.getMessage().getFrom().getLastName() ;
                    message.setText("Your ID: "+id + "\n Your USERNAME: "+name+ "\n Your NAME: "+ fullName);
                    break;
                }
                case "/join": {
                    if(_GameMaster.allPlayersAndTheirGames.containsKey(update.getMessage().getFrom().getId())){
                        message.setText("@"+update.getMessage().getFrom().getUserName() + " sorry! you are already playing a game.");
                    } else
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
                    //message.setText(Responses.onPick(user_id, in));

                    List<String> territories = null;
                    if(_GameMaster.allPlayersAndTheirGames.containsKey(user_id)) {
                        territories = Responses.onPick(gameID, in);
                        if(territories == null) {
                            message.setText("You can't do that right now");
                            break;
                        }
                        else {
                            message.setText("Pick Your Territory");
                            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                            // add each territory available as a button
                            for(String territory: territories) {
                                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                                rowInline.add(new InlineKeyboardButton().setText(territory).setCallbackData("CLAIM " + territory));
                                // Set the keyboard to the markup
                                rowsInline.add(rowInline);
                            }
                            // Add it to the message
                            markupInline.setKeyboard(rowsInline);
                            message.setReplyMarkup(markupInline);
                            // Is this right? trying to send to a specific user based on the player id that sent the initial message
                            message.setReplyToMessageId(user_id);
                            break;
                        }
                    }

                    break;
                }

                case "/reinforce": {
                    int user_id = update.getMessage().getFrom().getId();
                    Game game = getGame(update);
                    if (game.state == GameState.ARMIES) {
                        int turn = game.turn % game.playerDirectory.size();
                        Player player = game.playerDirectory.get(turn);
                        int armies = player.getNumberOfArmies();
                        message.setText("You have " + armies + " left. Please select a territory to reinforce.");
                        List<String> territories = player.getTerritories();
                        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                        // add each territory available as a button
                        for(String territory: territories) {
                            List<InlineKeyboardButton> rowInline = new ArrayList<>();
                            rowInline.add(new InlineKeyboardButton().setText(territory).setCallbackData("REINFORCE " + territory));
                            // Set the keyboard to the markup
                            rowsInline.add(rowInline);
                        }
                        // Add it to the message
                        markupInline.setKeyboard(rowsInline);
                        message.setReplyMarkup(markupInline);

                        // Is this right? trying to send to a specific user based on the player id that sent the initial message
                        message.setReplyToMessageId(user_id);
                        break;
                    }
                    else {
                        message.setText("IT IS NOT TIME!!!!");
                        break;
                    }
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
                                msg.putMessage(_GameMaster.gamesListing.get(gameID).game.GM.getBM().getFreeTerritories());
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
                    String attacker = in.getArgs().get(0);
                    String defender = in.getArgs().get(1);
                    int attackDie = Integer.parseInt(in.getArgs().get(2));
                    int defenseDie = Integer.parseInt(in.getArgs().get(3));
                    Game game = getGame(update);
                    Turn turn = game.currentTurn;
                    Player player = turn.player;
                    message.setText(turn.battle(attacker, defender, attackDie, defenseDie));
                    break;
                }

                // message should be formatted /fortify (move from) (move to) (Num armies)
                case "/fortify": {
                    Game game = getGame(update);
                    int turnNo = game.turn % game.playerDirectory.size();
                    Player player = game.playerDirectory.get(turnNo);
                    String origin = in.getArgs().get(0);
                    String territory = in.getArgs().get(1);
                    int transfer = Integer.parseInt(in.getArgs().get(2));
                    game.BM.fortifyTerritory(origin,territory,transfer);
                }


                // message format -> /buycredit (credit amount)
                case "/buycredit": {
                    Game game = getGame(update);
                    int turnNo = game.turn % game.playerDirectory.size();
                    Player player = game.playerDirectory.get(turnNo);
                    player.addMoney(Double.parseDouble(in.getArgs().get(0)));
                }

                // format -> /buyshit (# undos to buy) (# cards to buy)
                case "/buyshit": {
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
                    // write turn to saved games
                    // save game to S3
                    game.turn += 1;
                    int turnNo = game.turn % game.playerDirectory.size();
                    Player player = game.playerDirectory.get(turnNo);
                    message.setText("Player " +player.getUsername()+ " it is now your turn, type /beginTurn to begin your turn");
                    break;
                }

                case "/beginTurn": {
                    Game game = getGame(update);
                    int turnNo = game.turn % game.playerDirectory.size();
                    Player player = game.playerDirectory.get(turnNo);
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
            if(in.args.size() > 0) {
                // GAME START ANNOUNCEMENT -- GameState.START is when this /join triggered the game start, then take the gameID and broadcast to all chats
                if (in.getCommand().equals("/join") && _GameMaster.gamesListing.containsKey(in.args.get(0)) && _GameMaster.gamesListing.get(in.args.get(0)).state == GameState.START) {

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

                    _GameMaster.gamesListing.get(context).begin();

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
                    tempMSG.putMessage(_GameMaster.gamesListing.get(context).game.GM.getBM().getFreeTerritories());

                    fromMessenger = _GameMaster.gamesListing.get(context).messenger.getMessage();
                    if (fromMessenger != null) {
                        res += "\n";
                        res += fromMessenger;
                    }

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

                    _GameMaster.gamesListing.get(context).claim();
                }
            }
        }

        else if (update.hasCallbackQuery()) {
            // Set variables
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            long user_id = update.getMessage().getFrom().getId();

            // if claiming territories callback
            if (call_data.contains("CLAIM")) {

                String[] data = call_data.split(" "); // format of data for claim territory -> "CLAIM (territory name)"
                String gameID = _GameMaster.allPlayersAndTheirGames.get(user_id);
                Game game = _GameMaster.gamesListing.get(gameID);

                // initialize territory with player and territory then add to turn number
                game.BM.initializeTerritory(game.playerDirectory.get(game.turn % game.playerDirectory.size()), data[1], 1);

                // game.turn += 1; // assume players can keep track of the order right now

                // if no more free territories begin distributing armies
                if (game.BM.getFreeTerritories().size() == 0) {
                    game.turn += 1;
                    game.beginDistribute();
                    String answer = "You chose " + data[1] + " ,type /reinforce to reinforce territory";

                    EditMessageText new_message = new EditMessageText()
                            .setChatId(chat_id)
                            .setMessageId(toIntExact(message_id))
                            .setText(answer);
                    try {
                        execute(new_message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

                String answer = "You chose " + data[1] + " ,type /pick to select the next territory";
                EditMessageText new_message = new EditMessageText()
                        .setChatId(chat_id)
                        .setMessageId(toIntExact(message_id))
                        .setText(answer);
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

            // if reinforcing callback
            if (call_data.contains("REINFORCE")) {
                String[] data = call_data.split(" "); // format of data for claim territory -> "REINFORCE (territory name)"
                String gameID = _GameMaster.allPlayersAndTheirGames.get(user_id);
                Game game = _GameMaster.gamesListing.get(gameID);
                Player player = game.playerDirectory.get(game.turn % game.playerDirectory.size());

                game.BM.strengthenTerritory(player, data[1], 1);

                EditMessageText new_message;

                // if no more armies begin next turn
                if (game.checkArmies()) {
                    game.turn += 1;
                    game.beginTurns();
                    String answer = "You chose " + data[1] + " ,type /beginTurn to start the next turn";

                    new_message = new EditMessageText()
                            .setChatId(chat_id)
                            .setMessageId(toIntExact(message_id))
                            .setText(answer);
                }
                else {
                    String answer = "You chose " + data[1] + " ,type /reinforce to select the next territory";
                    new_message = new EditMessageText()
                            .setChatId(chat_id)
                            .setMessageId(toIntExact(message_id))
                            .setText(answer);
                }

                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }


            else {
                String answer = "This code is broke, call Norman he'll know what to do";
                EditMessageText new_message = new EditMessageText()
                        .setChatId(chat_id)
                        .setMessageId(toIntExact(message_id))
                        .setText(answer);
                try {
                    execute(new_message);
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
