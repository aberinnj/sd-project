public interface Subject {
    //methods to register and unregister observers
    public void attach(Observer obj);
    public void detach(Observer obj);

    //method to notify observers of change
    public void notifyAllPlayers();

    public void setDefendingPlayer (int state);
    public int getDefendingPlayer();
}
