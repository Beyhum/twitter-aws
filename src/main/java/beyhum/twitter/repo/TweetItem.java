package beyhum.twitter.repo;

import beyhum.twitter.core.Tweet;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

@DynamoDbBean
public class TweetItem {

    static final String TABLE_NAME = "Tweet";
    static final TableSchema<TweetItem> TABLE_SCHEMA = TableSchema.fromBean(TweetItem.class);

    static final String CONTENT_ATTR = "Content";
    static final String AUTHOR_ID_ATTR = "AuthorId";
    static final String AUTHOR_UNAME_ATTR = "AuthorUsername";
    static final String TIMESTAMP_ATTR = "Timestamp";
    static final String DATE_ATTR = "Date";
    public static final String TWEETS_BY_DATE_INDEX = "TweetsByDate";

    private String content;
    private UUID authorId;
    private String authorUsername;
    private Instant timestamp;
    private LocalDate date;

    static TweetItem create(Tweet tweet) {
        TweetItem tweetItem = new TweetItem();
        tweetItem.authorId = tweet.author.id;
        tweetItem.authorUsername = tweet.author.username;
        tweetItem.content = tweet.content;
        tweetItem.timestamp = tweet.timestamp;
        tweetItem.date = LocalDate.ofInstant(tweet.timestamp, ZoneOffset.UTC);
        return tweetItem;
    }

    /**
     * Required for dynamodb mapper reflection
     */
    public TweetItem() {}

    @DynamoDbAttribute(CONTENT_ATTR)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute(AUTHOR_ID_ATTR)
    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    @DynamoDbAttribute(AUTHOR_UNAME_ATTR)
    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    @DynamoDbSortKey
    @DynamoDbSecondarySortKey(indexNames = TWEETS_BY_DATE_INDEX)
    @DynamoDbAttribute(TIMESTAMP_ATTR)
    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = TWEETS_BY_DATE_INDEX)
    @DynamoDbAttribute(DATE_ATTR)
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
