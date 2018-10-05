import java.util.Random;

public class Dice {
    private int diceValue;

    /*////////////////////////////////////////////////////////////////////////////////
    Roll function seeds a random number from 1-6
    *///////////////////////////////////////////////////////////////////////////////*/
    public void roll(){
        diceValue = new Random().nextInt(6)+1;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Method returns dice value
    *///////////////////////////////////////////////////////////////////////////////*/
    public int getDiceValue(){
        return diceValue;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Returns the index of the highest roll in the number of iterations provided as an argument
    Indices are used for
    *///////////////////////////////////////////////////////////////////////////////*/
    public int getIndexOfHighestRollIn(int iterations){
        int indexOfHighestRoll = -1;
        int valueOfHighestRoll = 1;
        for(int i=0; i<iterations; i++)
        {
            roll();
            if (getDiceValue() > valueOfHighestRoll)
            {
                valueOfHighestRoll = getDiceValue();
                indexOfHighestRoll = i;
            }
        }
        return indexOfHighestRoll;
    }

}
