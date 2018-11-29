
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class TelegramTest extends TestCase {

    @Test
    public void testOnUpdateReceived() throws Exception {
        CommandsHandler bot = Mockito.mock(CommandsHandler.class);
        Mockito.doCallRealMethod().when(bot).onUpdateReceived(any());
        Update update1 = new Update();
        bot.onUpdateReceived(update1);
        Mockito.verify(bot).onUpdateReceived(update1);
    }

    @Test
    public void testOnUpdateReceived2() throws Exception {
        CommandsHandler bot = Mockito.mock(CommandsHandler.class);
        Mockito.doCallRealMethod().when(bot).onUpdateReceived(any());
        Update update1 = new Update();
        bot.onUpdateReceived(update1);
        Mockito.verify(bot).onUpdateReceived(update1);
    }

}
