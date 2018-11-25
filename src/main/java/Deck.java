import java.util.*;

/*///////////////////////////////////////////////////////////////////////////////
Deck Class
 *//////////////////////////////////////////////////////////////////////////////
public class Deck {

    Stack<Card> GameDeck;

    public Card draw() {
        try{
            System.out.println("DRAWING from game-deck");
            return GameDeck.pop();
        } catch (EmptyStackException e) {
            System.out.println("Game-deck is now empty.");
            return null;
        }
    }

    Deck() {
        GameDeck = new Stack<Card>();
        GameDeck.push(new Card("ALASKA", "INFANTRY"));
        GameDeck.push(new Card("NORTH WEST TERRITORY", "CAVALRY"));
        GameDeck.push(new Card("GREENLAND", "ARTILLERY"));
        GameDeck.push(new Card("ALBERTA", "INFANTRY"));
        GameDeck.push(new Card("ONTARIO", "CAVALRY"));
        GameDeck.push(new Card("QUEBEC", "ARTILLERY"));
        GameDeck.push(new Card("WESTERN UNITED STATES", "INFANTRY"));
        GameDeck.push(new Card("EASTERN UNITED STATES", "CAVALRY"));
        GameDeck.push(new Card("CENTRAL AMERICA", "ARTILLERY"));
        GameDeck.push(new Card("VENEZUELA", "INFANTRY"));
        GameDeck.push(new Card("PERU", "CAVALRY"));
        GameDeck.push(new Card("BRAZIL", "ARTILLERY"));
        GameDeck.push(new Card("ARGENTINA", "INFANTRY"));
        GameDeck.push(new Card("NORTH AFRICA", "CAVALRY"));
        GameDeck.push(new Card("CONGO", "ARTILLERY"));
        GameDeck.push(new Card("EGYPT", "INFANTRY"));
        GameDeck.push(new Card("EAST AFRICA", "CAVALRY"));
        GameDeck.push(new Card("SOUTH AFRICA", "ARTILLERY"));
        GameDeck.push(new Card("MADAGASCAR", "INFANTRY"));
        GameDeck.push(new Card("MIDDLE EAST", "CAVALRY"));
        GameDeck.push(new Card("INDIA", "ARTILLERY"));
        GameDeck.push(new Card("SIAM", "INFANTRY"));
        GameDeck.push(new Card("INDONESIA", "CAVALRY"));
        GameDeck.push(new Card("WESTERN AUSTRALIA", "ARTILLERY"));
        GameDeck.push(new Card("EASTERN AUSTRALIA", "INFANTRY"));
        GameDeck.push(new Card("NEW GUINEA", "CAVALRY"));
        GameDeck.push(new Card("CHINA", "ARTILLERY"));
        GameDeck.push(new Card("AFGHANISTAN", "INFANTRY"));
        GameDeck.push(new Card("URAL", "CAVALRY"));
        GameDeck.push(new Card("SIBERIA", "ARTILLERY"));
        GameDeck.push(new Card("YAKUTSK", "INFANTRY"));
        GameDeck.push(new Card("KAMCHATKA", "CAVALRY"));
        GameDeck.push(new Card("IRKUTSK", "ARTILLERY"));
        GameDeck.push(new Card("MONGOLIA", "INFANTRY"));
        GameDeck.push(new Card("JAPAN", "CAVALRY"));
        GameDeck.push(new Card("UKRAINE", "ARTILLERY"));
        GameDeck.push(new Card("SCANDINAVIA", "INFANTRY"));
        GameDeck.push(new Card("ICELAND", "CAVALRY"));
        GameDeck.push(new Card("GREAT BRITAIN", "ARTILLERY"));
        GameDeck.push(new Card("NORTHERN EUROPE", "INFANTRY"));
        GameDeck.push(new Card("SOUTHERN EUROPE", "CAVALRY"));
        GameDeck.push(new Card("WESTERN EUROPE", "ARTILLERY"));
        GameDeck.push(new Card("WILD", "WILD"));
        GameDeck.push(new Card("WILD", "WILD"));


        Collections.shuffle(GameDeck);
    }

    // using for reseting the deck when reloading
    Deck(Stack<Card> stack) {
        GameDeck = stack;
        Collections.shuffle(GameDeck);   // Just in case
    }
}
