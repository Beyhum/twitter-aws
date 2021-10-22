package beyhum.twitter.repo;

import beyhum.twitter.core.Tweet;
import beyhum.twitter.core.User;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DynamoTweetRepo implements TweetRepo {

    private final DynamoDbEnhancedClient enhancedClient;

    public DynamoTweetRepo(DynamoDbClient dbClient) {
        this.enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dbClient).build();
    }

    @Override
    public void addTweet(Tweet tweet) {
        tweetTable().putItem(TweetItem.create(tweet));
    }

    @Override
    public List<Tweet> getTweetFeed(UUID userId) {
        var dateIndex = tweetTable().index(TweetItem.TWEETS_BY_DATE_INDEX);
        var result = dateIndex.query(b -> b.scanIndexForward(false)
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(LocalDate.now(ZoneOffset.UTC).toString())))
                .filterExpression(Expression.builder().expression("AuthorId <> :userId")
                        .putExpressionValue(":userId", attribute(userId.toString())).build())
        );
        List<Tweet> tweets = result.stream().flatMap(p -> p.items().stream())
                .map(t -> new Tweet(t.getContent(), new User(t.getAuthorId(), t.getAuthorUsername()), t.getTimestamp()))
                .collect(Collectors.toList());
        return tweets;
    }

    @Override
    public List<Tweet> getUserTweets(UUID userId) {
        DynamoDbTable<TweetItem> table = tweetTable();
        var result = table.query(b -> b.consistentRead(true).scanIndexForward(false)
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(userId.toString())))
        );
        List<Tweet> tweets = result.items().stream()
                .map(t -> new Tweet(t.getContent(), new User(t.getAuthorId(), t.getAuthorUsername()), t.getTimestamp()))
                .collect(Collectors.toList());
        return tweets;
    }

    private DynamoDbTable<TweetItem> tweetTable() {
        return enhancedClient.table(TweetItem.TABLE_NAME, TweetItem.TABLE_SCHEMA);
    }

    private static AttributeValue attribute(String attributeVal) {
        return AttributeValue.builder().s(attributeVal).build();
    }
}
