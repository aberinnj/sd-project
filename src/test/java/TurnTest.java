import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

public class TurnTest extends TestCase {

    // test if Turn
    @Test
    public void testTurn(){

        String base = System.getProperty("user.dir");
        GameManager GM = new GameManager(base, 3);
        ByteArrayInputStream playerTerritoriesInput = new ByteArrayInputStream((
                "SIAM\n"+
                "ALASKA\n"+
                "QUEBEC\n").getBytes());
        System.setIn(playerTerritoriesInput);
        Scanner k = new Scanner(System.in);

        System.setIn(System.in);
    }
}
