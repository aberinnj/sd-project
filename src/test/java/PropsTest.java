import junit.framework.TestCase;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;

public class PropsTest extends TestCase {

    @Test
    public void testProps() throws IOException {

        Props props = new Props();
        props.resetConfigPath(System.getProperty("user.dir") +  "/secrets_TeamOne.prop");

        assertNull(props.getTwitter_apiKey());

        assertNull(props.getTwitter_accessToken());

        assertNull(props.getTwitter_apiSecretKey());

        assertNull(props.getTwitter_accessTokenSecret());

        assertNull(props.getAws_access_key_id());

        assertNull(props.getAws_secret_access_key());

        assertNull(props.getBot_name());

        assertNull(props.getBot_apiToken());

    }

}
