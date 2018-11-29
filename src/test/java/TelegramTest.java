
import com.fasterxml.jackson.annotation.JsonProperty;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Mockito;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.*;
import org.telegram.telegrambots.meta.api.methods.games.GetGameHighScores;
import org.telegram.telegrambots.meta.api.methods.games.SetGameScore;
import org.telegram.telegrambots.meta.api.methods.groupadministration.*;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updates.GetWebhookInfo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.api.objects.inlinequery.ChosenInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultPhoto;
import org.telegram.telegrambots.meta.api.objects.passport.PassportData;
import org.telegram.telegrambots.meta.api.objects.payments.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.ws.rs.client.Entity;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.telegram.abilitybots.api.bot.BaseAbilityBot.USER_ID;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class TelegramTest extends TestCase {

    public static final int USER_ID = 1337;
    public static final long CHAT_ID = 1337L;
    public static final String firstName = "BOB";
    public static final boolean isBot = true;
    public static final String lastName = "ROSS";
    public static final String userName = "BOB ROSS";
    public static final String languageCode = "en";

    CommandsHandler bot;

    private MessageSender sender;

    @Test
    public void testOnUpdateReceived() throws Exception {
        bot = Mockito.mock(CommandsHandler.class);
        Mockito.doCallRealMethod().when(bot).onUpdateReceived(any());
        Update update1 = new Update();
        bot.onUpdateReceived(update1);
        Mockito.verify(bot).onUpdateReceived(update1);


    }

    /*
    @Test
    public void testOnUpdateReceived2() throws Exception {
        bot = Mockito.mock(CommandsHandler.class);
        Mockito.doCallRealMethod().when(bot).onUpdateReceived(any());
        Update upd = new Update();

        setFinalStaticField(Update.class, "MESSAGE_FIELD", "/help");

        Mockito.verify(bot).onUpdateReceived(upd);

    }

    private static void setFinalStaticField(Class<?> clazz, String fieldName, Object value)
            throws ReflectiveOperationException {

        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);

        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, value);
    }*/


}