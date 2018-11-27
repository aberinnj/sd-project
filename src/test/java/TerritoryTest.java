import junit.framework.TestCase;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;

/*////////////////////////////////////////////////////////////////////////////////
_GameStarter Class starts a _GameStarter by initializing GameManager and runs a game
*///////////////////////////////////////////////////////////////////////////////*/
public class TerritoryTest extends TestCase{
    private Territory mainTerritory;

    @Test
    public void testSimpleGets() {
        String[] k = new String[]{"INDIA", "CHINA", "INDONESIA"};
        mainTerritory = new Territory(k, "RANDOM");
        mainTerritory.setTerritory(true, 0, new Army(1));
        assertEquals(0, mainTerritory.getOccupantID());
        assertTrue(mainTerritory.isOccupied());
        assertEquals(1, mainTerritory.getArmy().getInfantryCount());
        assertEquals("RANDOM", mainTerritory.name);

        for(String c: k)
        {
            assertTrue(mainTerritory.getNeighbors().contains(c));
        }
    }

    @Test
    public void testInitialization() throws Exception {
        Territory territoryInitialized;
        territoryInitialized = new Territory(new String[]{"KAMCHATKA", "NORTH WEST TERRITORY", "ALBERTA"}, "UN-NAMED");
        assertFalse(territoryInitialized.isOccupied());
        assertEquals(-1, territoryInitialized.getOccupantID());
        assertNull(territoryInitialized.getArmy());
    }


    @Test
    public void testAddOccupants() throws Exception {
        mainTerritory = new Territory(new String[]{"INDIA", "CHINA", "INDONESIA"}, "UN-NAMED");
        mainTerritory.setTerritory(true, 2, new Army(1));
        mainTerritory.addOccupants(5, ArmyType.INFANTRY);
        assertEquals(6, mainTerritory.getArmy().getInfantryCount());
    }

    @Test
    public void testLoseOccupants() throws Exception {
        mainTerritory = new Territory(new String[]{"INDIA", "CHINA", "INDONESIA"}, "UN-NAMED");
        mainTerritory.setTerritory(true, 2, new Army(6));
        assertNotNull(mainTerritory.getArmy());
        mainTerritory.loseOccupants(2, ArmyType.INFANTRY);
        assertEquals(4, mainTerritory.getArmy().getInfantryCount());
    }


    @Test
    public void testOwnership() throws Exception {
        mainTerritory = new Territory(new String[]{"INDIA", "CHINA", "INDONESIA"}, "UN-NAMED");
        mainTerritory.setTerritory(true, 2, new Army(1));
        mainTerritory.transferOwnership(3);
        assertEquals(3, mainTerritory.getOccupantID());
        mainTerritory.transferOwnership(4);
        assertEquals(4, mainTerritory.getOccupantID());
    }

    @Test
    public void testArmyCount() throws Exception {
        mainTerritory = new Territory(new String[]{"INDIA", "CHINA", "INDONESIA"}, "UN-NAMED");
        mainTerritory.setTerritory(true, 2, new Army(1));
        assertEquals(1, mainTerritory.getArmy().getInfantryCount());
    }

    @Test
    public void testSeeArmyCount() throws Exception {
        mainTerritory = new Territory(new String[]{"INDIA", "CHINA", "INDONESIA"}, "UN-NAMED");
        mainTerritory.setTerritory(true, 2, new Army(4));
        assertEquals("4", mainTerritory.seeArmyCount());
    }

}