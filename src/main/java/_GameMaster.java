
import java.io.IOException;
import java.util.*;


import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/*////////////////////////////////////////////////////////////////////////////////
_GameMaster is the BOT that handles all chat commands and game hosting/handling
*///////////////////////////////////////////////////////////////////////////////*/
public class _GameMaster {
    static HashMap<String, Game> gamesListing;
    static HashMap<Integer, String> allPlayersAndTheirGames;
    static Props props;

    public static void main(String[] args) {
        gamesListing = new HashMap<>();
        //kineticEntity = new Fetcher();
        allPlayersAndTheirGames = new HashMap<>();
        try {
            props = new Props();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
