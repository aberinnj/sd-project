import junit.framework.TestCase;
import org.junit.Test;

public class CreditTest extends TestCase {
    @Test
    public void testCredit() {
        Player player = new Player(10, 10);
        player.addMoney(200.0);

        assertEquals(200.0,player.getWallet());

        Player player2 = new Player(10, 10);

        assertEquals(0.0, player2.getWallet());

        player.transferCredit(200.0, player2);

        assertEquals(200.0,player2.getWallet());
    }
}
