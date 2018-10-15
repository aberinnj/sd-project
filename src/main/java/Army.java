/*///////////////////////////////////////////////////////////////////////////////
/*///////////////////////////////////////////////////////////////////////////////
enum ArmyType {
    INFANTRY
}

/*////////////////////////////////////////////////////////////////////////////////
Army class

todo: Add functionality for CAVALRY/ARTILLERY
*///////////////////////////////////////////////////////////////////////////////*/
public class Army {
    private int INFANTRY;

    // army constructor sets the value of army
    Army(int j){
        INFANTRY =j;
    }

    // returns the current infantry count;
    public int getInfantryCount(){
        return INFANTRY;
    }
    // adds a k-number of infantry to army
    public void addInfantryCount(int k) {
        INFANTRY += k;
    }

    // removes k-number of infantry from army
    public void loseInfantry(int k){
        INFANTRY -= k;
    }
}
