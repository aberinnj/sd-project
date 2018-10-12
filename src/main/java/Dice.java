import java.util.Random;

/*////////////////////////////////////////////////////////////////////////////////
Dice class
*///////////////////////////////////////////////////////////////////////////////*/
public class Dice {
    private int diceValue;

    // roll function
    public void roll(){
        diceValue = new Random().nextInt(6)+1;
    }

    // method returns current dice values
    public int getDiceValue(){
        return diceValue;
    }
}
