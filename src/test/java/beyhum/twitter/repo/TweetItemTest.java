package beyhum.twitter.repo;

import beyhum.twitter.core.Tweet;
import beyhum.twitter.core.User;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.Assert.*;

public class TweetItemTest {

    @Test
    public void createBuildsTweetItem() {
        User author = new User(UUID.randomUUID(), "player1");
        Tweet tweet = new Tweet("some content", author, Instant.now());

        TweetItem actual = TweetItem.create(tweet);

        assertEquals(tweet.author.id, actual.getAuthorId());
        assertEquals(tweet.author.username, actual.getAuthorUsername());
        assertEquals(tweet.content, actual.getContent());
        assertEquals(tweet.timestamp, actual.getTimestamp());
        assertEquals(LocalDate.ofInstant(tweet.timestamp, ZoneOffset.UTC), actual.getDate());
    }
}