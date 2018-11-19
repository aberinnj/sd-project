import com.google.gson.stream.JsonWriter;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class JSONHandlerTest  extends TestCase {

    @Test
    public void testTurnInit() {
        GameManager GM = new GameManager();
        BoardManager BM = GM.getBM();


        /*
        JSONhandler jsohandler = new JSONhandler(BM, Player[] playerList, int[] playerTurnPattern, String base) throws IOException {
            this.bm = bm;
            this.playerList = playerList;
            this.playerTurnPattern = playerTurnPattern;
            this.fileName = base + "/Risk.json";
            this.file = new FileWriter(fileName, false);
            this.jw = new JsonWriter(file);
        }*/
    }

}
