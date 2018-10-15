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

    // Assumption:
    // There are 42 cards in the deck
    @Test
    public void testDeck() {
        Deck deck = new Deck();

        assertNotNull(deck.draw());
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        assertNotSame(deck.draw(), deck.draw());
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();

        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();

        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.draw();
        assertNull(deck.draw());
    }



}
