import junit.framework.TestCase;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;

public class PropsTest extends TestCase {

    @Test
    public void testProps() throws IOException {

        Props props = new Props(System.getProperty("user.dir") +  "/src/files/secrets_example.prop");

        assertEquals("KEY3", props.getTwitter_apiKey());

        assertEquals("KEY5",props.getTwitter_accessToken());

        assertEquals("KEY4",props.getTwitter_apiSecretKey());

        assertEquals("KEY6",props.getTwitter_accessTokenSecret());

        assertEquals("KEY1",props.getAws_access_key_id());

        assertEquals("KEY2",props.getAws_secret_access_key());

        assertEquals("KEY8",props.getBot_name());

        assertEquals("KEY7",props.getBot_apiToken());

    }

}
