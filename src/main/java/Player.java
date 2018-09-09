public class Player {

    private int id;
    private int infantryCount;

    /* Getters: getInfantryCount used for battles*/
    public int getInfantryCount() {
        return infantryCount;
    }

    /* Getters: getID used for setting a territory to a player*/
    public int getId() {
        return id;
    }

    /* Mutator: setting infantry */
    public int loseInfantry(int i) {
        return infantryCount - i;
    }

    Player(int id, int infantryCount)
    {
        this.id = id;
        this.infantryCount=infantryCount;
    }
}
