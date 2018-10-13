import junit.framework.TestCase;
import org.junit.Test;

/*////////////////////////////////////////////////////////////////////////////////
Deck Test
*///////////////////////////////////////////////////////////////////////////////*/
public class DeckTest extends TestCase {
    @Test
    public void testCard(){
        Card k = new Card("INDIA", "INFANTRY");
        assertEquals("INFANTRY", k.getUnit());
        assertEquals("INDIA", k.getOrigin());
    }

    @Test
    public void testDeck() {
        Deck deck = new Deck();

        assertNotNull(deck.draw());
        assertNotNull(deck.draw());
        assertNotNull(deck.draw());
        assertNotNull(deck.draw());
        assertNotNull(deck.draw());
        assertNotSame(deck.draw(), Deck.draw());
    }



}
