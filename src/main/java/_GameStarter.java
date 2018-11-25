import java.io.IOException;

import java.util.*;

/*////////////////////////////////////////////////////////////////////////////////
_GameStarter Class starts _GameStarter by calling Loader methods to get game-data and
calls GameManager methods to store them.

or by initializing GameManager and runs a game
*///////////////////////////////////////////////////////////////////////////////*/
public class _GameStarter {
    Messenger messenger;
    Scanner scanner;
    String base;
    String bucketName;
    String objectOfGameInstance;
    static int playerCount;
    static GameManager GM;

    /* Initialize Member Variables
     * Note that playerCount is initialized to 0. Call setNumberOfPlayers to initialize*/
    _GameStarter(){
        this.scanner = new Scanner(System.in);
        this.base = System.getProperty("user.dir");
        bucketName = "risk-game-team-one";
        playerCount = 0;
    }

    /*//////////////////////////////////////////////////////////////////////////////////
    Main function
    todo: enable functions below
    //////////////////////////////////////////////////////////////////////////////////*/
    public void initGame(Game thisGame) throws IOException, InterruptedException {
        this.messenger = thisGame.messenger;
        playerCount = thisGame.playerDirectory.size();
        // GM.setMessenger(thisGame);
        defaultStart(thisGame);
        System.out.println("Init done. \n");
        //GM.runGame(GM, thisGame.messenger, thisGame);
    }

    public void defaultStart(Game thisGame) throws InterruptedException {
        //while (setNumberOfPlayers()) {}
        GM.initializeAsNormal(playerCount, thisGame);
        GameManager.fileObjKeyName = thisGame.gameID;
    }
}

