import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;

public class Twitter {

    TwitterFactory tf;
    static twitter4j.Twitter twitter;
    static twitter4j.Status res;

    Twitter() {

        ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(_GameMaster.props.getTwitter_apiKey())
                    .setOAuthConsumerSecret(_GameMaster.props.getTwitter_apiSecretKey())
                    .setOAuthAccessToken(_GameMaster.props.getTwitter_accessToken())
                    .setOAuthAccessTokenSecret(_GameMaster.props.getTwitter_accessTokenSecret());

            // twitter setup
            tf = new TwitterFactory(cb.build());
            twitter = tf.getInstance();
    }


    public static String broadcastToTwitter(Turn k, Player p) throws TwitterException
    {
        int gains = 0;
        String result = "Turn("+ k.turnId + "):Player " + p.getId() + " captured ";
        String status = "\nTurn Summary: ";
        for(String terr: p.getTerritories())
        {
            if (!k.previousTerritories.contains(terr)) {
                gains++;
            }
        }

        if(gains == 1) {
            result += gains;
            result += " territory.";
        }
        else if (gains > 1) {
            result += gains;
            result += " territories.";
        }

        if (gains > 0) {
            res = twitter.updateStatus(result);
            return status + res.getText();
        }
        return status + result + "no territories this turn.";
    }

}
