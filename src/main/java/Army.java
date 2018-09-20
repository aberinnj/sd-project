public class Army {
    private int INFANTRY;
    private int CAVALRY;
    private int ARTILLERY;

    /*////////////////////////////////////////////////////////////////////////////////
    Contructor for building an army
    *///////////////////////////////////////////////////////////////////////////////*/
    Army(int j){
        INFANTRY =j;
        CAVALRY =0;
        ARTILLERY =0;
    }

    /*////////////////////////////////////////////////////////////////////////////////
    Function for getting values, incrementing and decrementing
    *///////////////////////////////////////////////////////////////////////////////*/
    public int getInfantryCount(){
        return INFANTRY;
    }
    public int getCavalryCount(){
        return CAVALRY;
    }
    public int getArtilleryCount(){
        return ARTILLERY;
    }
    public void addInfantryCount(int k) { INFANTRY += k; }
    public void addCavalryCount(int k) { CAVALRY += k ; }
    public void addArtilleryCount(int k) { ARTILLERY += k; }
    public void loseInfantry(int k){ INFANTRY -= k;}
    public void loseCavalry(int k){ CAVALRY -= k;}
    public void loseArtillery(int k){ ARTILLERY -= k;}

    /*////////////////////////////////////////////////////////////////////////////////
    Exchanging number of pieces

    Not yet implemented.
    *///////////////////////////////////////////////////////////////////////////////*/

    public void exchange(int type){
        switch (type){
            case 1:
                // option 1: exchange 5 INFANTRY for 1 CAVALRY
                break;
            case 2:
                // option 2: exchange 2 CAVALRY for 1 ARTILLERY
                break;
            case 3:
                // option 3: exchange 1 CAVALRY and 5 INFANTRY for 1 ARTILLERY
                break;

            default:
                // throw error
                break;
        }
    }
}
