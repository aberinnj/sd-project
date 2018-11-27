import junit.framework.TestCase;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;

public class PropsTest extends TestCase {

    @Test
    public void testProps() throws IOException {

        Props props = new Props();
        props.resetConfigPath(System.getProperty("user.dir") +  "/secrets_TeamOne_demo.prop");

        assertEquals("", props.getTwitter_apiKey());

        assertEquals("",props.getTwitter_accessToken());

        assertEquals("",props.getTwitter_apiSecretKey());

        assertEquals("",props.getTwitter_accessTokenSecret());

        assertEquals("",props.getAws_access_key_id());

        assertEquals("",props.getAws_secret_access_key());

        assertEquals("",props.getBot_name());

        assertEquals("",props.getBot_apiToken());

    }

}
