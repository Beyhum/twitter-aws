package beyhum.twitter.service;

import beyhum.twitter.core.Tweet;
import beyhum.twitter.core.User;
import beyhum.twitter.repo.InMemoryTweetRepo;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class TweetServiceTest {

    @Test
    public void addTweetInsertsTweetForUserInToken() {

        InMemoryTweetRepo tweetRepo = new InMemoryTweetRepo();
        TweetService tweetService = new TweetService(tweetRepo);
        AuthToken userToken = new AuthToken(UUID.randomUUID(), "userA");
        String tweetContent = "tweet msg";

        tweetService.addTweet(tweetContent, userToken);

        List<Tweet> userTweets = tweetRepo.getUserTweets(userToken.userId);
        assertEquals(1, userTweets.size());
        assertEquals(tweetContent, userTweets.get(0).content);
        assertEquals(userToken.userId, userTweets.get(0).author.id);
        assertEquals(userToken.username, userTweets.get(0).author.username);
    }

    @Test
    public void getTweetFeedReturnsTweetsOfOtherUsersSortedByMostRecent() {
        InMemoryTweetRepo tweetRepo = new InMemoryTweetRepo();
        TweetService tweetService = new TweetService(tweetRepo);
        AuthToken user1Token = new AuthToken(UUID.randomUUID(), "user1");
        AuthToken user2Token = new AuthToken(UUID.randomUUID(), "user2");

        tweetRepo.addTweet(new Tweet("a", user(user1Token), Instant.now()));
        tweetRepo.addTweet(new Tweet("b", user(user1Token), Instant.now()));
        tweetRepo.addTweet(new Tweet("c", user(user2Token), Instant.now()));
        tweetRepo.addTweet(new Tweet("d", user(user2Token), Instant.now().plusSeconds(10)));

        List<Tweet> tweetFeed = tweetService.getTweetFeed(user1Token);

        assertEquals(2, tweetFeed.size());
        assertTrue(tweetFeed.stream().allMatch(t -> t.author.id.equals(user2Token.userId)));
        assertEquals("d", tweetFeed.get(0).content);
        assertEquals("c", tweetFeed.get(1).content);

    }

    @Test
    public void getUserTweetsReturnsOwnTweetsSortedByMostRecent() {
        InMemoryTweetRepo tweetRepo = new InMemoryTweetRepo();
        TweetService tweetService = new TweetService(tweetRepo);
        AuthToken user1Token = new AuthToken(UUID.randomUUID(), "user1");
        AuthToken user2Token = new AuthToken(UUID.randomUUID(), "user2");

        tweetRepo.addTweet(new Tweet("a", user(user1Token), Instant.now()));
        tweetRepo.addTweet(new Tweet("b", user(user1Token), Instant.now().plusSeconds(10)));
        tweetRepo.addTweet(new Tweet("c", user(user2Token), Instant.now()));
        tweetRepo.addTweet(new Tweet("d", user(user2Token), Instant.now()));

        List<Tweet> tweetFeed = tweetService.getUserTweets(user1Token);

        assertEquals(2, tweetFeed.size());
        assertTrue(tweetFeed.stream().allMatch(t -> t.author.id.equals(user1Token.userId)));
        assertEquals("b", tweetFeed.get(0).content);
        assertEquals("a", tweetFeed.get(1).content);
    }

    private static User user(AuthToken token) {
        return new User(token.userId, token.username);
    }

}