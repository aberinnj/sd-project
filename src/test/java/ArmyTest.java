
import junit.framework.TestCase;
import org.junit.Test;

public class ArmyTest extends TestCase {

    @Test
    public void testArmy() {
        Army army = new Army(10);
        assertEquals(10, army.getInfantryCount());

        army.addInfantryCount(20);
        assertEquals(30, (army).getInfantryCount());

        army.loseInfantry(20);
        assertEquals(10, army.getInfantryCount());
    }
}