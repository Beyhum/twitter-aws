package beyhum.twitter.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.UUID;

public class AuthToken {

    public static final String USERNAME_CLAIM = "username";

    public final String token;
    public final UUID userId;
    public final String username;

    public AuthToken(String token) {
        this.token = token;
        DecodedJWT decoded = JWT.decode(token);
        this.userId = UUID.fromString(decoded.getSubject());
        this.username = decoded.getClaim(USERNAME_CLAIM).asString();
    }

    /**
     * For testing purposes
     */
    AuthToken(UUID userId, String username) {
        this.userId = userId;
        this.username = username;
        this.token = username;
    }

    public static AuthToken mockToken(UUID userId, String username) {
        return new AuthToken(userId, username);
    }
}
