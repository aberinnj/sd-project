import junit.framework.TestCase;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ConsoleInputReaderTest extends TestCase {

    ConsoleInputReader IN;
    String input;

    @Test
    public void testInput() {


        IN = new ConsoleInputReader();
        try {
            ByteArrayInputStream in = new ByteArrayInputStream("ACTION".getBytes());
            System.setIn(in);
            System.setIn(System.in);
            input = IN.call();
        }catch (IOException e){
            System.out.print("Timer error!");
        }

        assertEquals("ACTION",input);

    }
}
