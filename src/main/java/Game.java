import java.util.*;

enum GameState {
    QUEUE, // the default state
    START, // when a game gets 3 players, this is the state until functions are called (a brief window) and the state is changed again
    INIT, // the state when the game has started
    CLAIM, // the state when the game allows for players to claim territories
    ARMIES, // same as CLAIM except armies instead of territories
    TURNS, // state for running actual turns
    WAIT, // the state when the gmae is waiting on a player
    THINK, // the neutral state to switch from WAIT or to WAIT -- thinking
    PAUSE, // the state when the timer is set off (future functionality)
    END, // the state when the game has ended until functions are called (another brief window)
    CLOSED // the final state, when all functions are called
}

/*////////////////////////////////////////////////////////////////////////////////
Game holds all each game's information and the list of players in the game
todo: check if static is causing problems
*///////////////////////////////////////////////////////////////////////////////*/
class Game extends Observable {
    HashMap<Integer, Player> playerDirectory;
    BoardManager BM;
    ArrayList<Integer> users;
    //ArrayList<Integer> turnPattern;
    ArrayList<Player> turnPattern;
    int turn;
    String gameID;
    GameState state;
    Messenger messenger;
    Turn currentTurn;
    Deck deck;


    Game() {
        messenger = new Messenger();
        playerDirectory = new HashMap<>();
        BM = new BoardManager();
        users = new ArrayList<>();
        turnPattern = new ArrayList<>();
        turn = 0;
        state = GameState.QUEUE;
    }

    //funciton to reset the game ID if reloading
    public void setGameID(String id) {
        gameID = id;
    }

    // function to reset the turn if reloading
    public void setTurn(int turn) {
        turn = turn;
    }

    // telegram style, gives each players the appropriate number of armies on init
    public void setPlayerList(){
        int size = playerDirectory.size();
        int default_infantry = 0;
        switch(size){
            case 2:
                default_infantry=40;
                break;
            case 3:
                default_infantry=35;
                break;
            case 4:
                default_infantry=30;
                break;
            case 5:
                default_infantry=25;
                break;
            case 6:
                default_infantry=20;
                break;
        }
        for(int a=0; a<size; a++){
            playerDirectory.get(a).addArmies(default_infantry);
        }
    }

    // shuffle players to create random turn order
    public void shufflePlayers() {
        List<Player> valueList = new ArrayList<Player>(playerDirectory.values());
        Collections.shuffle(valueList);
        Iterator<Player> valueIt = valueList.iterator();
        messenger.putMessage("Turn-Order:");
        for(Map.Entry<Integer,Player> e : playerDirectory.entrySet()) {
            Player thisPlayer = valueIt.next();
            messenger.putMessage("@"+thisPlayer.username);
            e.setValue(thisPlayer);
        }
    }

    public void start() {
        shufflePlayers();
        setPlayerList();
    }

    public void addUser(Integer user_id, String username, long chat_id){

        //playerDirectory.put(user_id, new User(user_id, username, chat_id));

        // Create new player to add to the list of players for the game
        playerDirectory.put(playerDirectory.size(), new Player(user_id, username, chat_id, 0));
        users.add(user_id);

        if(playerDirectory.size() == _GameMaster.MIN_PLAYERS_PER_GAME)
        {
            state = GameState.START;
            setChanged();
            notifyObservers();
        }

    }

    public void beginTurns() {
        state = GameState.TURNS;
        setChanged();
        notifyObservers();
    }

    public void beginDistribute() {
        state = GameState.ARMIES;
        setChanged();
        notifyObservers();
    }

    public void setTurnPattern(int i, int roll) {
        turnPattern.set(i, playerDirectory.get(roll));
    }

    public void setCurrentTurn(Turn turn) { this.currentTurn = turn;}

    //function to determine it all players have distributed their armies
    public boolean checkArmies() {
        for (int key: playerDirectory.keySet()) {
            if (playerDirectory.get(key).getNumberOfArmies() > 0) return false;
        }
        return true;
    }
}
