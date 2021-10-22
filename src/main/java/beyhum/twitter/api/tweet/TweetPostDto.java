package beyhum.twitter.api.tweet;

import beyhum.twitter.core.Tweet;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class TweetPostDto {

    @Size(min = 1, max = Tweet.TWEET_MAX_CHARS)
    @NotNull
    @JsonProperty
    public String content;

}
