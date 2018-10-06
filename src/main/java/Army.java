public class Army {
    private int INFANTRY;

    /*////////////////////////////////////////////////////////////////////////////////
    Contructor for building an army
    *///////////////////////////////////////////////////////////////////////////////*/
    Army(int j){
        INFANTRY =j;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Function for getting values, incrementing and decrementing
    *///////////////////////////////////////////////////////////////////////////////*/
    public int getInfantryCount(){
        return INFANTRY;
    }
    public void addInfantryCount(int k) { INFANTRY += k; }
    public void loseInfantry(int k){ INFANTRY -= k;}
}
