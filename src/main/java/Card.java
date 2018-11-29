/*///////////////////////////////////////////////////////////////////////////////
Card Class
 *//////////////////////////////////////////////////////////////////////////////
public class Card {
    private String origin;
    private String unit;

    Card(String o, String u)
    {
        this.origin = o;
        assert(origin == o);
        this.unit = u;
        assert(unit == u);
    }
    public String getOrigin(){
        return origin;
    }
    public String getUnit(){
        return unit;
    }
}