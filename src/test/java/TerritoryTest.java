import junit.framework.TestCase;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;

/*////////////////////////////////////////////////////////////////////////////////
NewGame Class starts a NewGame by initializing GameManager and runs a game
*///////////////////////////////////////////////////////////////////////////////*/
public class TerritoryTest extends TestCase{
    private Territory mainTerritory;

    @Test
    public void testSimpleGets() {
        String[] k = new String[]{"INDIA", "CHINA", "INDONESIA"};
        mainTerritory = new Territory(k);
        mainTerritory.setTerritory(true, 0, new Army(1));
        assertEquals(0, mainTerritory.getOccupantID());
        assertTrue(mainTerritory.isOccupied());
        assertEquals(1, mainTerritory.getArmy().getInfantryCount());

        for(String c: k)
        {
            assertTrue(mainTerritory.getNeighbors().contains(c));
        }
    }

    @Test
    public void testInitialization() throws Exception {
        Territory territoryInitialized;
        territoryInitialized = new Territory(new String[]{"KAMCHATKA", "NORTH WEST TERRITORY", "ALBERTA"});
        assertFalse(territoryInitialized.isOccupied());
        assertEquals(-1, territoryInitialized.getOccupantID());
        assertNull(territoryInitialized.getArmy());
    }

    @Test
    public void testAddOccupants() throws Exception {
        mainTerritory = new Territory(new String[]{"INDIA", "CHINA", "INDONESIA"});
        mainTerritory.setTerritory(true, 2, new Army(1));
        mainTerritory.addOccupants(5, "INFANTRY");
        assertEquals(6, mainTerritory.getArmy().getInfantryCount());
    }

    @Test
    public void testLoseOccupants() throws Exception {
        mainTerritory = new Territory(new String[]{"INDIA", "CHINA", "INDONESIA"});
        mainTerritory.setTerritory(true, 2, new Army(6));
        assertNotNull(mainTerritory.getArmy());
        mainTerritory.loseOccupants(2, "INFANTRY");
        assertEquals(4, mainTerritory.getArmy().getInfantryCount());
    }


    @Test
    public void testOwnership() throws Exception {
        mainTerritory = new Territory(new String[]{"INDIA", "CHINA", "INDONESIA"});
        mainTerritory.setTerritory(true, 2, new Army(1));
        mainTerritory.transferOwnership(3);
        assertEquals(3, mainTerritory.getOccupantID());
        mainTerritory.transferOwnership(4);
        assertEquals(4, mainTerritory.getOccupantID());
    }

    @Test
    public void testArmyCount() throws Exception {
        mainTerritory = new Territory(new String[]{"INDIA", "CHINA", "INDONESIA"});
        mainTerritory.setTerritory(true, 2, new Army(1));
        assertEquals(1, mainTerritory.getArmy().getInfantryCount());
    }

    @Test
    public void testSeeArmyCount() throws Exception {
        mainTerritory = new Territory(new String[]{"INDIA", "CHINA", "INDONESIA"});
        mainTerritory.setTerritory(true, 2, new Army(4));
        assertEquals("4", mainTerritory.seeArmyCount());
    }

    @Test
    public void testExceptionForOccupants(){
        mainTerritory = new Territory(new String[]{"INDIA", "CHINA", "INDONESIA"});
        mainTerritory.setTerritory(true, 2, new Army(1));
        try {
            mainTerritory.addOccupants(1, "NOT INFANTRY");
            fail("Expecting an Exception to be thrown, invalid army type provided");
        } catch (Exception e)
        {
            assertThat(e.getMessage(), e.getMessage().equals("Invalid Army Type "));
        }


        try {
            mainTerritory.loseOccupants(1, "NOT INFANTRY");
            fail("Expecting an Exception to be thrown, invalid army type provided");
        } catch (Exception e)
        {
            assertThat(e.getMessage(), e.getMessage().equals("Invalid Army Type "));
        }
    }

}
