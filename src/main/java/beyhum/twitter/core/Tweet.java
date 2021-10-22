package beyhum.twitter.core;

import java.time.Instant;
import java.util.Objects;

public class Tweet {

    public static final int TWEET_MAX_CHARS = 160;
    public final String content;
    public final User author;
    public final Instant timestamp;


    public Tweet(String content, User author, Instant timestamp) {
        this.content = Objects.requireNonNull(content);
        this.author = Objects.requireNonNull(author);
        this.timestamp = Objects.requireNonNull(timestamp);
        if (content.length() > TWEET_MAX_CHARS) {
            throw new IllegalArgumentException("Tweet cannot exceed" + TWEET_MAX_CHARS + "characters");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tweet tweet = (Tweet) o;
        return content.equals(tweet.content) &&
                author.equals(tweet.author) &&
                timestamp.equals(tweet.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, author, timestamp);
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "content='" + content + '\'' +
                ", author=" + author +
                ", timestamp=" + timestamp +
                '}';
    }
}
