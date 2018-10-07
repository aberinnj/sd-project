import junit.framework.TestCase;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class NewGameTest extends TestCase{

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    @Test
    public void testNewGame() throws Exception {
        NewGame ng = new NewGame();

        Scanner scanner = NewGame.getScanner();
        assertNotNull(scanner);

        String base = NewGame.getBase();
        assertNotNull(base);

        BoardManager bm = NewGame.getBoardManager(base);
        assertNotNull(bm);

        Scanner test = new Scanner("2");
        int p = NewGame.getNumberPlayers(test);
        assertEquals(2, p);

        Player[] playerList = NewGame.setupPlayerList(p);
        assertNotNull(playerList);

        Dice dice = new Dice();

        int highest = NewGame.rollForSetup(dice, p);
        assertNotSame(10, highest);

        int[] playerTurnPattern = new int[p];

        for(int b=0; b<p; b++)
        {
            playerTurnPattern[b] = (highest+b)% p;
            System.out.println((b+1)+ ". Player#" + playerTurnPattern[b]);
        }

        String content = readFile(base + "/src/test/java/input.txt", StandardCharsets.UTF_8);

        Scanner territories = new Scanner(content);
        NewGame.initializeTerritories(bm, territories, playerTurnPattern, playerList);

    }

}
