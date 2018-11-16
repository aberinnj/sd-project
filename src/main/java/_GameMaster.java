import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Observer;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

enum GameState {
    QUEUE, // the default state
    START, // when a game gets 3 players, this is the state until functions are called (a brief window) and the state is changed again
    INIT, // the state when the game has started
    WAIT, // the state when the gmae is waiting on a player
    PAUSE, // the state when the timer is set off (future functionality)
    END, // the state when the game has ended until functions are called (another brief window)
    CLOSED // the final state, when all functions are called
}

/*////////////////////////////////////////////////////////////////////////////////
Container for user-info
*///////////////////////////////////////////////////////////////////////////////*/
class User {
    int id;
    String username;
    long chat_id;

    User(int id, String username, long chat_id){
        this.id = id;
        this.username = username;
        this.chat_id = chat_id; // send messages to this chat_id.
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
                thisGame.game.initGame(thisGame.gameID);
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}

/*////////////////////////////////////////////////////////////////////////////////
Game holds all each game's information and the list of players in the game
todo: check if static is causing problems
*///////////////////////////////////////////////////////////////////////////////*/
class Game extends Observable {
    _GameStarter game;
    HashMap<Integer, User> playerList;
    String gameID;
    GameState state;

    Game(String id) {
        game = new _GameStarter();
        playerList = new HashMap<>();
        gameID = id;
        state = GameState.QUEUE;

    }

    public void addUser(Integer user_id, String username, long chat_id){
        if(!playerList.containsKey(user_id))
        {
            playerList.put(user_id, new User(user_id, username, chat_id));
            if(playerList.size() == _GameMaster.MIN_PLAYERS_PER_GAME)
            {
                state = GameState.START;
                setChanged();
                notifyObservers();
            }
        }
    }

    public void begin()
    {
        if(playerList.size() == _GameMaster.MIN_PLAYERS_PER_GAME) {
                state = GameState.INIT;
                setChanged();
                notifyObservers();
        } else {
            System.out.println("ERROR: NOT ENOUGH PLAYERS SOMEHOW");
        }
    }
}

/*////////////////////////////////////////////////////////////////////////////////
_GameMaster is the BOT that handles all chat commands and game hosting/handling
todo: make sure only one instance is currently running
*///////////////////////////////////////////////////////////////////////////////*/
public class _GameMaster {
    static HashMap<String, Game> gamesListing;
    static Fetcher kineticEntity;
    final static int MIN_PLAYERS_PER_GAME = 2;
    final static int MAX_PLAYERS_PER_GAME = 6;

    public static void main(String[] args) {
        gamesListing = new HashMap<>();
        kineticEntity = new Fetcher();

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
                    String gameID = "risk-game-" + UUID.randomUUID().toString();
                    int user_id = update.getMessage().getFrom().getId();
                    long chat_id = update.getMessage().getChatId();
                    message.setText(Responses.onCreate(user_id, gameID, update.getMessage().getFrom().getUserName(), chat_id));

                    break;
                }

                case "/help": {
                    message.setText(Responses.onHelp());
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

            // guess context here

            if(in.args.size() > 0){
                // GAME START ANNOUNCEMENT -- GameState.START is when this /join triggered the game start, then take the gameID and broadcast to all chats
                if(in.getCommand().equals("/join") && _GameMaster.gamesListing.containsKey(in.args.get(0)) && _GameMaster.gamesListing.get(in.args.get(0)).state == GameState.START) {

                    String context = in.args.get(0);

                    ArrayList<Long> chat_reply_list = new ArrayList<>();
                    SendMessage announcement = new SendMessage();

                    for (int user_id : _GameMaster.gamesListing.get(context).playerList.keySet()) {
                        if(!chat_reply_list.contains(_GameMaster.gamesListing.get(context).playerList.get(user_id).chat_id)){
                            chat_reply_list.add(_GameMaster.gamesListing.get(context).playerList.get(user_id).chat_id);
                        }
                        res += "@";
                        res += _GameMaster.gamesListing.get(context).playerList.get(user_id).username;
                        res += "\n";
                    }



                    for (Long dest: chat_reply_list)
                    {
                        announcement.setChatId(dest);
                        announcement.setText("Your game " + _GameMaster.gamesListing.get(context).gameID + "is now starting. \n\nPlayers:\n" + res);
                        try {
                            execute(announcement);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    _GameMaster.gamesListing.get(context).begin();
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
