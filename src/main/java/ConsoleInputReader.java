import java.io.*;
import java.util.concurrent.Callable;

public class ConsoleInputReader implements Callable<String> {
    public String call() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input;
        System.out.println("30 SECOND TIMER STARTED:");
        do {
            System.out.println("Please type something: ");
            try {
                // wait until we have data to complete a readLine()
                while (!br.ready()) {
                    Thread.sleep(200);
                }
                input = br.readLine();
            } catch (InterruptedException e) {
                return null;
            }
        } while ("".equals(input));
        return input;
    }
}
