import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import Utilities.CommandUtils;
import Utilities.ChatInput;
import Utilities.Responses;

import javax.swing.text.Utilities;
import java.util.ArrayList;


/*////////////////////////////////////////////////////////////////////////////////
GameState is a state machine for game states
*///////////////////////////////////////////////////////////////////////////////*/
enum GameState {
    WAITING, INIT, TURN, PAUSED
}

/*////////////////////////////////////////////////////////////////////////////////
Chat Player holds the data for users that are in any of the current games
*///////////////////////////////////////////////////////////////////////////////*/
class ChatPlayer {
    String userID;
    String name;
}

/*////////////////////////////////////////////////////////////////////////////////
Game holds all each game's information and the list of players in the game
*///////////////////////////////////////////////////////////////////////////////*/
class Game {
    _GameStarter game;
    GameState status;
    ChatPlayer playerList[];
    String gameID;
}

/*////////////////////////////////////////////////////////////////////////////////
_GameMaster is the BOT that handles all chat commands and game hosting/handling
todo: make sure only one instance is currently running
*///////////////////////////////////////////////////////////////////////////////*/
public class _GameMaster {
    static Game currentGames[];
    static ChatPlayer currentPlayers[];
    final int MAX_GAMES = 2;
    final int MIN_PLAYERS_PER_GAME = 3;


    public static void main(String[] args) {
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
class CommandsHandler extends TelegramLongPollingBot {

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
                }
                case "/join": {

                }
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
