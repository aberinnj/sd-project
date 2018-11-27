import com.google.gson.JsonObject;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

public class JSONhandlerTest extends TestCase {

    @Test
    public void testJSONhandler() throws IOException {
        Game game = new Game();
        JSONhandler handler = new JSONhandler(game);
        handler.fileName = handler.base + "/src/files//testRisk.json";
        JsonObject junk = handler.JSONreader();
        handler.JSONwriter();
    }
}
