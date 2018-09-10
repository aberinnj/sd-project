import java.util.Random;

public class Dice {
    private int diceValue;

    public void roll(){
        diceValue = new Random().nextInt(6)+1;
    }
    public int getDiceValue(){
        return diceValue;
    }
}
