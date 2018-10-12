import junit.framework.TestCase;
import org.junit.Test;

/*////////////////////////////////////////////////////////////////////////////////
Deck Test
*///////////////////////////////////////////////////////////////////////////////*/
public class DeckTest extends TestCase {
    @Test
    public void testDeck() {
        String base = System.getProperty("user.dir");
        Deck deck = new Deck(base + "/src/files/deck.json");

        assertNotNull(deck.drawCard());
        assertNotNull(deck.drawCard());
        assertNotNull(deck.drawCard());
        assertNotNull(deck.drawCard());
        assertNotNull(deck.drawCard());
        assertNotSame(deck.drawCard(), Deck.drawCard());
    }

}
