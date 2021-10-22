package beyhum.twitter.core;

import org.junit.Test;

import java.time.Instant;
import java.util.UUID;

public class TweetTest {

    @Test(expected = IllegalArgumentException.class)
    public void tweetWithMoreThanMaxCharsIsInvalid() {
        String tooLargeTweetContent = new String(new char[Tweet.TWEET_MAX_CHARS + 1]);

        new Tweet(tooLargeTweetContent, new User(UUID.randomUUID(), "any"), Instant.now());
    }

    @Test(expected = NullPointerException.class)
    public void tweetWithNullContentIsInvalid() {
        new Tweet(null, new User(UUID.randomUUID(), "any"), Instant.now());
    }

    @Test(expected = NullPointerException.class)
    public void tweetWithNullUserIsInvalid() {
        new Tweet("any", null, Instant.now());
    }

    @Test(expected = NullPointerException.class)
    public void tweetWithNullTimestampIsInvalid() {
        new Tweet("any", new User(UUID.randomUUID(), "any"), null);
    }
}