package beyhum.twitter.api.tweet;

import beyhum.twitter.core.Tweet;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public class TweetGetDto {

    @JsonProperty
    public String content;

    @JsonProperty
    public UUID authorId;

    @JsonProperty
    public String authorUsername;

    @JsonProperty
    public Instant timestamp;

    public static TweetGetDto create(Tweet tweet) {
        TweetGetDto dto = new TweetGetDto();
        dto.content = tweet.content;
        dto.authorId = tweet.author.id;
        dto.authorUsername = tweet.author.username;
        dto.timestamp = tweet.timestamp;

        return dto;
    }
}
