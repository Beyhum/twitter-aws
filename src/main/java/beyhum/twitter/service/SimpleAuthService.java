package beyhum.twitter.service;

import beyhum.twitter.core.User;
import beyhum.twitter.repo.UserRepo;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static beyhum.twitter.service.AuthToken.USERNAME_CLAIM;

/**
 * Authenticates users by checking if they exist in the database and their password matches their username
 */
public class SimpleAuthService implements AuthService {

    private static final String JWT_ISSUER = "beyhum";
    private static final long ONE_DAY_IN_SECONDS = 60 * 60 * 24;

    private final Algorithm hmacAlgorithm;
    private final UserRepo userRepo;
    private final long jwtLifetimeSecs;
    private final Supplier<Instant> clock;

    public SimpleAuthService(String secretKey, UserRepo userRepo) {
        this.hmacAlgorithm = Algorithm.HMAC256(secretKey);
        this.userRepo = userRepo;
        this.clock = Instant::now;
        this.jwtLifetimeSecs = ONE_DAY_IN_SECONDS;
    }

    SimpleAuthService(String secretKey, UserRepo userRepo, long jwtLifetimeSecs, Supplier<Instant> clock) {
        this.hmacAlgorithm = Algorithm.HMAC256(secretKey);
        this.userRepo = userRepo;
        this.jwtLifetimeSecs = jwtLifetimeSecs;
        this.clock = clock;
    }

    @Override
    public AuthToken login(String username, String password) throws BadCredentialsException {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        if (!username.equals(password)) {
            throw new BadCredentialsException(username);
        }

        Optional<User> user = userRepo.findUserByName(username);
        if (user.isPresent()) {
            UUID userId = user.get().id;
            var jwt = JWT.create()
                    .withIssuer(JWT_ISSUER)
                    .withSubject(userId.toString())
                    .withExpiresAt(Date.from(clock.get().plusSeconds(jwtLifetimeSecs)))
                    .withClaim(USERNAME_CLAIM, username)
                    .sign(hmacAlgorithm);
            return new AuthToken(jwt);
        } else {
            throw new BadCredentialsException(username);
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            JWT.require(hmacAlgorithm)
                    .withIssuer(JWT_ISSUER)
                    .acceptExpiresAt(5)
                    .withClaimPresence(USERNAME_CLAIM)
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}
