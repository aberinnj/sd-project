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
        try {
            Props props = new Props();

            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(props.getTwitter_apiKey())
                    .setOAuthConsumerSecret(props.getTwitter_apiSecretKey())
                    .setOAuthAccessToken(props.getTwitter_accessToken())
                    .setOAuthAccessTokenSecret(props.getTwitter_accessTokenSecret());

            // twitter setup
            tf = new TwitterFactory(cb.build());
            twitter = tf.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void broadcastToTwitter(Turn k, Player p) throws TwitterException
    {
        int gains = 0;
        String result = "Turn("+ k.turnId + "):Player " + p.getId() + " captured ";
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

        System.out.println("\nTurn Summary: ");
        if (gains > 0) {
            Status status = twitter.updateStatus(result);
            System.out.println(status.getText());
        } else
            System.out.println(result + "no territories this turn.");
    }

}
