package beyhum.twitter.repo;

import beyhum.twitter.core.Tweet;

import java.util.List;
import java.util.UUID;

public interface TweetRepo {

    void addTweet(Tweet tweet);


    List<Tweet> getTweetFeed(UUID userId);

    /**
     * Gets the tweets of a given user, sorted from latest to oldest
     * @param userId The ID of the user whose tweets are queried
     * @return
     */
    List<Tweet> getUserTweets(UUID userId);
}
