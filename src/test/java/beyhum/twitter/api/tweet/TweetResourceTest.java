package beyhum.twitter.api.tweet;

import beyhum.twitter.core.Tweet;
import beyhum.twitter.core.User;
import beyhum.twitter.service.MockAuthService;
import beyhum.twitter.service.TweetService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static beyhum.twitter.service.AuthToken.USERNAME_CLAIM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

public class TweetResourceTest {

    @Test
    public void postTweetsReturns201OnValidTweet() {
        TweetResource tweetResource = new TweetResource();
        tweetResource.authService = new MockAuthService(true);
        TweetService mockTweetService = Mockito.mock(TweetService.class);
        tweetResource.tweetService = mockTweetService;

        TweetPostDto tweetPostDto = new TweetPostDto();
        tweetPostDto.content = "abc";
        Response response = tweetResource.postTweet(tweetPostDto, "Bearer " + token());

        assertEquals(201, response.getStatus());
    }


    @Test
    public void apisReturns401OnInvalidToken() {
        TweetResource tweetResource = new TweetResource();
        tweetResource.authService = new MockAuthService(false);
        TweetService mockTweetService = Mockito.mock(TweetService.class);
        tweetResource.tweetService = mockTweetService;

        TweetPostDto tweetPostDto = new TweetPostDto();
        tweetPostDto.content = "abc";
        Response responseForBadToken = tweetResource.postTweet(tweetPostDto, "Bearer" + token());
        Response response2ForBadToken = tweetResource.getTweets("Bearer " + token(), false);

        assertEquals(401, responseForBadToken.getStatus());
        assertEquals(401, response2ForBadToken.getStatus());
    }

    @Test
    public void apisReturns401OnMissingToken() {
        TweetResource tweetResource = new TweetResource();
        tweetResource.authService = new MockAuthService(true);
        TweetService mockTweetService = Mockito.mock(TweetService.class);
        tweetResource.tweetService = mockTweetService;

        TweetPostDto tweetPostDto = new TweetPostDto();
        tweetPostDto.content = "abc";
        Response responseForMissingToken = tweetResource.postTweet(tweetPostDto, "");

        assertEquals(401, responseForMissingToken.getStatus());
    }

    @Test
    public void getTweetsReturnsOwnTweetsWhenSelfParamTrue() {

        TweetResource tweetResource = new TweetResource();
        tweetResource.authService = new MockAuthService(true);
        TweetService mockTweetService = Mockito.mock(TweetService.class);
        Tweet expectedTweet = new Tweet("x", new User(UUID.randomUUID(), "a"), Instant.now());
        Mockito.when(mockTweetService.getUserTweets(any())).thenReturn(List.of(expectedTweet));
        tweetResource.tweetService = mockTweetService;

        TweetPostDto tweetPostDto = new TweetPostDto();
        tweetPostDto.content = "abc";
        Response response = tweetResource.getTweets("Bearer " + token(), true);

        assertEquals(200, response.getStatus());
        List<TweetGetDto> tweetFeed = (List<TweetGetDto>) response.getEntity();
        assertEquals(1, tweetFeed.size());
        TweetGetDto actualTweet = tweetFeed.get(0);
        assertTweetValid(expectedTweet, actualTweet);
    }


    @Test
    public void getTweetsReturnsOtherTweetsWhenSelfParamFalse() {

        TweetResource tweetResource = new TweetResource();
        tweetResource.authService = new MockAuthService(true);
        TweetService mockTweetService = Mockito.mock(TweetService.class);
        Tweet expectedTweet = new Tweet("x", new User(UUID.randomUUID(), "a"), Instant.now());
        Mockito.when(mockTweetService.getTweetFeed(any())).thenReturn(List.of(expectedTweet));
        tweetResource.tweetService = mockTweetService;

        TweetPostDto tweetPostDto = new TweetPostDto();
        tweetPostDto.content = "abc";
        Response response = tweetResource.getTweets("Bearer " + token(), false);

        assertEquals(200, response.getStatus());
        List<TweetGetDto> tweetFeed = (List<TweetGetDto>) response.getEntity();
        assertEquals(1, tweetFeed.size());
        TweetGetDto actualTweet = tweetFeed.get(0);
        assertTweetValid(expectedTweet, actualTweet);
    }


    private void assertTweetValid(Tweet expectedTweet, TweetGetDto actualTweet) {
        assertEquals(expectedTweet.content, actualTweet.content);
        assertEquals(expectedTweet.timestamp, actualTweet.timestamp);
        assertEquals(expectedTweet.author.id, actualTweet.authorId);
        assertEquals(expectedTweet.author.username, actualTweet.authorUsername);
    }


    private static String token() {
        return JWT.create()
                .withIssuer("any")
                .withSubject(UUID.randomUUID().toString())
                .withClaim(USERNAME_CLAIM, "anyusername")
                .sign(Algorithm.HMAC256("any"));
    }
}