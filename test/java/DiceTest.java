import junit.framework.TestCase;
import org.junit.Test;


/*////////////////////////////////////////////////////////////////////////////////
Dice Test
*///////////////////////////////////////////////////////////////////////////////*/
public class DiceTest extends TestCase {

    @Test
    public void testDice() throws Exception {

        Dice dice = new Dice();
        dice.roll();
        int roll = dice.getDiceValue();
        int high = 7;
        int low = 0;
        assertTrue("Error, random is too high", high >= roll);
        assertTrue("Error, random is too low",  low  <= roll);
    }
}
