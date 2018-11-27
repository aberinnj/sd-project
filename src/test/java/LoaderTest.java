import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

public class LoaderTest extends TestCase {

    @Test
    public void testLoader() throws IOException {
        Loader load = new Loader("1");
        load.JH.fileName = load.JH.base + "/src/files/testRisk.json";
        load.LoadGame();
    }
}
