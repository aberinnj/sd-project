import junit.framework.TestCase;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class NewGameTest extends TestCase{

    @Test
    public void testNewGame() {
        NewGame ng = new NewGame();

        Scanner scanner = NewGame.getScanner();
        assertNotNull(scanner);

        String base = NewGame.getBase();
        assertNotNull(base);
        
        BoardManager bm = NewGame.getBoardManager(base);
        assertNotNull(bm);
    }

}
