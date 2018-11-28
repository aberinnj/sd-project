/*///////////////////////////////////////////////////////////////////////////////
Card Class
 *//////////////////////////////////////////////////////////////////////////////
public class Card {
    private String origin;
    private String unit;

    Card(String o, String u)
    {
        this.origin = o;
        this.unit = u;
    }
    public String getOrigin(){
        return origin;
    }
    public String getUnit(){
        return unit;
    }
}