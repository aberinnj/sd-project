
import java.util.*;


import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

