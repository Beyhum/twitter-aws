package beyhum.twitter.repo;

import beyhum.twitter.core.Tweet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryTweetRepo implements TweetRepo {

    private final List<Tweet> tweets = new ArrayList<>();

    @Override
    public void addTweet(Tweet tweet) {
        tweets.add(tweet);
    }

    @Override
    public List<Tweet> getTweetFeed(UUID userId) {
        return tweets.stream()
                .filter(t -> !t.author.id.equals(userId))
                .sorted(Comparator.comparing((Tweet t) -> t.timestamp).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Tweet> getUserTweets(UUID userId) {
        return tweets.stream()
                .filter(t -> t.author.id.equals(userId))
                .sorted(Comparator.comparing((Tweet t) -> t.timestamp).reversed())
                .collect(Collectors.toList());
    }
}
