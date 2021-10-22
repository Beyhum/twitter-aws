package beyhum.twitter.api.tweet;


import beyhum.twitter.core.Tweet;
import beyhum.twitter.service.AuthService;
import beyhum.twitter.service.AuthToken;
import beyhum.twitter.service.TweetService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/tweets")
public class TweetResource {
    public static final String AUTHORIZATION = "Authorization";
    public static final String AUTH_TYPE_PREFIX = "Bearer ";
    @Inject
    public AuthService authService;

    @Inject
    public TweetService tweetService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.WILDCARD)
    public Response postTweet(@Valid @NotNull TweetPostDto tweetPostDto, @HeaderParam(AUTHORIZATION) String authHeader) {
            Optional<AuthToken> authToken = validateAuthHeader(authHeader);
            if (authToken.isEmpty()) {
                return Response.status(401).build();
            }
            tweetService.addTweet(tweetPostDto.content, authToken.get());
            return Response.status(201).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.WILDCARD)
    public Response getTweets(@HeaderParam(AUTHORIZATION) String authHeader, @DefaultValue("false") @QueryParam("self") boolean ownTweets) {
        Optional<AuthToken> authToken = validateAuthHeader(authHeader);
        if (authToken.isEmpty()) {
            return Response.status(401).build();
        }

        List<Tweet> tweets = null;
        if (ownTweets) {
            tweets = tweetService.getUserTweets(authToken.get());
        } else {
            tweets = tweetService.getTweetFeed(authToken.get());
        }

        List<TweetGetDto> tweetsDto = tweets.stream().map(t -> TweetGetDto.create(t)).collect(Collectors.toList());
        return Response.ok(tweetsDto).build();

    }

    private Optional<AuthToken> validateAuthHeader(String authHeader) {
        if (authHeader != null && authHeader.contains(AUTH_TYPE_PREFIX)) {
            String token = authHeader.substring(AUTH_TYPE_PREFIX.length());
            if (authService.validateToken(token)) {
                return Optional.of(new AuthToken(token));
            }
        }
        return Optional.empty();
    }

}
