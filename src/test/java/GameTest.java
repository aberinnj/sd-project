import junit.framework.TestCase;
import org.junit.Test;

public class GameTest extends TestCase {

    @Test
    public void testGame() {
        Game game = new Game();
        Player player1 = new Player(10,10);
        Player player2 = new Player(10,10);

        Turn turn = new Turn(game.BM,player1,1);
        game.setCurrentTurn(turn);

    }

    @Test
    public void testGameUserAdd () {
        Game game = new Game();
        Player player1 = new Player(10,10);
        Player player2 = new Player(10,10);
        Player player3 = new Player(10,10);

        game.addUser(1,"player1",123123);
        game.addUser(2,"player2",12312322);
        game.addUser(3,"player3",123123123);

        game.setPlayerList();

        game.addUser(4,"player4",1231233333);
        game.setPlayerList();

        game.addUser(5,"player5",12555333);
        game.setPlayerList();

        game.addUser(6,"player6",16655333);
        game.setPlayerList();

        assertNotNull(game.checkArmies());

    }
}
