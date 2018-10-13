import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

/*///////////////////////////////////////////////////////////////////////////////
 *//////////////////////////////////////////////////////////////////////////////
public class TurnTest extends TestCase {

    @Test
    public void testTurn(){

        String base = System.getProperty("user.dir");
        GameManager GM = new GameManager(base, 3);
        ByteArrayInputStream playerTerritoriesInput = new ByteArrayInputStream((
                "SIAM\n"+
                "ALASKA\n"+
                "QUEBEC\n").getBytes());
        System.setIn(playerTerritoriesInput);

        GM.getPlayer(2).addTerritories("");

        System.setIn(System.in);
    }
}
