
import junit.framework.TestCase;
import org.junit.Test;

public class GameManagerTest extends TestCase {

    @Test
    public void testGameManager() throws Exception {
        Object army = new Army(10);
        assertEquals(10, ((Army) army).getInfantryCount());
    }
    @Test
    public void testGameManager1() throws Exception {
        Object army2 = new Army(10000);
        assertEquals(10000, ((Army) army2).getInfantryCount());
    }
    @Test
    public void testGameManager2() throws Exception {
        Object player = new Player(0, 40, hand);
        assertEquals(40, ((Player) player).getRemainingArmies());
    }
    @Test
    public void testGameManager3() throws Exception {
        Object player2 = new Player(0, 40, hand);
        assertEquals(false, ((Player) player2).isBaseEmpty());
    }
    @Test
    public void testGameManager4() throws Exception {
        Object dice = new Dice();
        ((Dice) dice).roll();
        int roll = ((Dice) dice).getDiceValue();
        int high = 7;
        int low = 0;
        assertTrue("Error, random is too high", high >= roll);
        assertTrue("Error, random is too low",  low  <= roll);
    }
}