import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

public class JSONHandlerTest  extends TestCase {

    Gson gson;
    BoardManager bm = null;
    Writer file;
    JsonWriter jw;
    Game game;
    String fileName;
    String base;

    @Test
    public void testJSONreader() {
        Game game = new Game();

    }

}
