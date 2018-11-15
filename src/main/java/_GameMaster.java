import javafx.beans.Observable;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.UUID;

enum GameState {
    QUEUE,
    INIT,
    WAIT,
    PAUSE,
    END
}

/*////////////////////////////////////////////////////////////////////////////////
Game holds all each game's information and the list of players in the game
*///////////////////////////////////////////////////////////////////////////////*/
class Game {
    static _GameStarter game;
    static ArrayList<Integer> playerList;
    static String gameID;
    static GameState state;

    Game(String id) {
        game = new _GameStarter();
        playerList = new ArrayList<>();
        gameID = id;
        state = GameState.QUEUE;
    }
}

/*////////////////////////////////////////////////////////////////////////////////
_GameMaster is the BOT that handles all chat commands and game hosting/handling
todo: make sure only one instance is currently running
*///////////////////////////////////////////////////////////////////////////////*/
public class _GameMaster {
    static ArrayList<Integer> GameCreationLimitingArray;
    static ArrayList<Game> gamesListing;
    final static int MIN_PLAYERS_PER_GAME = 2;
    final static int MAX_PLAYERS_PER_GAME = 6;
    final public static String backendBucket = "risk-game-team-one";

    public static void main(String[] args) {
        gamesListing = new ArrayList<>();
        GameCreationLimitingArray = new ArrayList<Integer>();

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
                    message.setText(Responses.onListAllGames(_GameMaster.gamesListing));
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
                    if(CommandUtils.validateArgumentCount(in, 1)) {
                        message.setText(Responses.onJoin(in, update.getMessage().getFrom().getId()));
                    }
                    else
                        message.setText("You did not provide any gameID. " + Responses.onListAllGames(_GameMaster.gamesListing));
                    break;
                }

                case "/getStatus": {
                    int id = update.getMessage().getFrom().getId();
                    message.setText(Responses.onGetStatus(id));
                    break;
                }

                case "/listMyGames": {
                    int id = update.getMessage().getFrom().getId();
                    message.setText(Responses.onListMyGames(id));
                    break;
                }

                case "/create": {
                    int user = update.getMessage().getFrom().getId();

                    if(!_GameMaster.GameCreationLimitingArray.contains(user)) {
                        String id = "risk-game-" + UUID.randomUUID().toString();

                        _GameMaster.GameCreationLimitingArray.add(user);
                        _GameMaster.gamesListing.add(new Game(id));
                        for (Game k : _GameMaster.gamesListing) {
                            if (k.gameID.equals(id)) {
                                k.playerList.add(user);
                            }
                        }
                        message.setText(Responses.onCreate(id));
                    } else {
                        message.setText("Uh Oh! You have made a game recently and the game has not yet concluded.\n You can only make one game at one point but join many others.");
                    }
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
        }
    }

    @Override
    public String getBotUsername(){
        return "teamOneRiskBot";
    }

    @Override
    public String getBotToken(){
        return "792967351:AAGqk3F95jeKt5Zww6ufI4-6OLdxtomlLEA";
    }

}
