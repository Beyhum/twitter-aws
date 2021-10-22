package beyhum.twitter.service;

import beyhum.twitter.core.Tweet;
import beyhum.twitter.core.User;
import beyhum.twitter.repo.TweetRepo;

import java.time.Instant;
import java.util.List;

public class TweetService {

    private final TweetRepo tweetRepo;

    public TweetService(TweetRepo tweetRepo) {
        this.tweetRepo = tweetRepo;
    }

    public void addTweet(String content, AuthToken authToken) {
        User author = new User(authToken.userId, authToken.username);
        Tweet tweet = new Tweet(content, author, Instant.now());
        tweetRepo.addTweet(tweet);
    }


    /**
     * Gets the tweets of a given user, sorted from latest to oldest
     *
     * @param authToken The token of the user making the request
     * @return The tweets of other users, sorted from latest to oldest
     */
    public List<Tweet> getTweetFeed(AuthToken authToken) {
        return tweetRepo.getTweetFeed(authToken.userId);
    }

    /**
     * Gets the tweets of a given user, sorted from latest to oldest
     *
     * @param authToken The token of the user making the request
     * @return The tweets of a given user, sorted from latest to oldest
     */
    public List<Tweet> getUserTweets(AuthToken authToken) {
        return tweetRepo.getUserTweets(authToken.userId);
    }
}