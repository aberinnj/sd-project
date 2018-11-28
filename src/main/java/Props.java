/*///////////////////////////////////////////////////////////////////////
GameManager sets up game

On a high-level, this class sets up the game by Initializing Players and the Board
as well as the Deck


todo: make changes to current_turn, UPDATE JH.JSON_writer
*//////////////////////////////////////////////////////////////////////*/

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

class Props{
    String configPath;
    Properties props = new Properties();

    Props() throws IOException
    {
        this.configPath = System.getProperty("user.dir") +  "/secrets_TeamOne.prop";
        this.props.load(new FileInputStream(configPath));
    }

    // function for reseting the path for testing
    public void resetConfigPath(String in) throws IOException {
        this.configPath = in;
        this.props.load(new FileInputStream(configPath));
    }

    public String getTwitter_apiKey() { return props.getProperty("twitter_apiKey"); }

    public String getTwitter_accessToken() { return props.getProperty("twitter_accessToken"); }

    public String getTwitter_apiSecretKey() { return props.getProperty("twitter_apiSecretKey"); }

    public String getTwitter_accessTokenSecret() { return props.getProperty("twitter_accessTokenSecret"); }

    public String getAws_access_key_id() { return props.getProperty("aws_access_key_id"); }

    public String getAws_secret_access_key() { return props.getProperty("aws_secret_access_key"); }

    public String getBot_name() { return props.getProperty("bot_name");}

    public String getBot_apiToken() { return props.getProperty("bot_token");}

}
