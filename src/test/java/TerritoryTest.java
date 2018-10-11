import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;


public class TerritoryTest extends TestCase{
    Object mainTerritory = new Territory(
            true,
            0,
            new Army(1),
            new ArrayList<String>() {{
                add("KAMCHATKA");
                add("NORTH WEST TERRITORY");
                add("ALBERTA");
    }});

    @Test
    public void TestSimpleGets() throws Exception {
        assertEquals(0, ((Territory) mainTerritory).getOccupantID());
    }

    @Test void TestAddOccupants() throws Exception {
        ((Territory) mainTerritory).addOccupants(5, "INFANTRY");
        assertEquals(6, ((Territory) mainTerritory).ArmyCount());
        ((Territory) mainTerritory).addOccupants(4, "INFANTRY");
        assertEquals(2, ((Territory) mainTerritory).ArmyCount());
    }

    @Test
    public void testGetArmy() throws Exception {
        assertEquals(1, ((Territory) mainTerritory).getArmy().getInfantryCount());

        assertEquals("1", ((Territory) mainTerritory).seeArmyCount());
    }

    @Test
    public void testOwnership() throws Exception {
        ((Territory) mainTerritory).transferOwnership(1);
        assertEquals(1, ((Territory) mainTerritory).getOccupantID());
        ((Territory) mainTerritory).transferOwnership(0);
    }

    @Test
    public void testIsOccupied() throws Exception {
        assertTrue(((Territory) mainTerritory).isOccupied());
    }

    @Test
    public void testArmyCount() throws Exception {
        assertEquals(1, ((Territory) mainTerritory).ArmyCount());
    }


}
