import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;

public class Twitter {

    TwitterFactory tf;
    twitter4j.Twitter twitter;

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


    public String broadcastToTwitter(Turn k, Player p) throws TwitterException
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
        result += gains;
        if(gains == 1)
            result += " territory.";
        else
            result += " territories.";

        if (gains > 0) {
            twitter.updateStatus(result);
            return status + result;
        }
        return status + result + " no territories this turn.";
    }

}
