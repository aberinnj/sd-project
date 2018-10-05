public class Army {
    private int INFANTRY;
    private int CAVALRY;
    private int ARTILLERY;

    Army(int j){
        INFANTRY =j;
        CAVALRY =0;
        ARTILLERY =0;
    }

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

}
