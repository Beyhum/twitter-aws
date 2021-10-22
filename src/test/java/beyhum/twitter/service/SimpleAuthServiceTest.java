package beyhum.twitter.service;

import beyhum.twitter.core.User;
import beyhum.twitter.repo.InMemoryUserRepo;
import org.junit.Test;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class SimpleAuthServiceTest {

    @Test
    public void loginReturnsValidJwtWhenUserExists() throws BadCredentialsException {
        InMemoryUserRepo userRepo = new InMemoryUserRepo();
        User existingUser = new User(UUID.randomUUID(), "user1");
        userRepo.addUser(existingUser);
        SimpleAuthService authService = new SimpleAuthService("secret", userRepo);

        AuthToken jwt = authService.login(existingUser.username, existingUser.username);
        assertEquals(existingUser.id, jwt.userId);
        assertEquals(existingUser.username, jwt.username);
        assertTrue(authService.validateToken(jwt.token));
    }

    @Test(expected = BadCredentialsException.class)
    public void loginFailsWhenUserNotFound() throws BadCredentialsException {
        InMemoryUserRepo userRepo = new InMemoryUserRepo();
        User nonexistentUser = new User(UUID.randomUUID(), "userthatdoesntexist");
        SimpleAuthService authService = new SimpleAuthService("secret", userRepo);

        authService.login(nonexistentUser.username, nonexistentUser.username);
    }

    @Test(expected = BadCredentialsException.class)
    public void loginFailsWhenUsernameAndPasswordArentEqual() throws BadCredentialsException {
        InMemoryUserRepo userRepo = new InMemoryUserRepo();
        User existingUser = new User(UUID.randomUUID(), "user1");
        userRepo.addUser(existingUser);
        SimpleAuthService authService = new SimpleAuthService("secret", userRepo);

        authService.login(existingUser.username, "some other password");
    }

    @Test
    public void validateTokenReturnsFalseOnInvalidToken() {
        InMemoryUserRepo userRepo = new InMemoryUserRepo();
        SimpleAuthService authService = new SimpleAuthService("secret", userRepo);

        assertFalse(authService.validateToken("some invalid token"));
    }

    @Test
    public void validateTokenReturnsFalseOnTokenSignedWithOtherKey() throws BadCredentialsException {
        InMemoryUserRepo userRepo = new InMemoryUserRepo();
        User existingUser = new User(UUID.randomUUID(), "user1");
        userRepo.addUser(existingUser);
        SimpleAuthService authService = new SimpleAuthService("secret_key_1", userRepo);
        SimpleAuthService authServiceWithDifferentKey = new SimpleAuthService("secret_key_2", userRepo);

        AuthToken jwtWithDifferentKey = authServiceWithDifferentKey.login(existingUser.username, existingUser.username);
        boolean validationResult = authService.validateToken(jwtWithDifferentKey.token);

        assertFalse(validationResult);
    }

    @Test
    public void validateTokenReturnsFalseOnExpiredToken() throws BadCredentialsException {
        InMemoryUserRepo userRepo = new InMemoryUserRepo();
        User existingUser = new User(UUID.randomUUID(), "user1");
        userRepo.addUser(existingUser);
        Supplier<Instant> clockSupplyingExpiredDates = () -> Instant.now().minusSeconds(500);
        SimpleAuthService authService = new SimpleAuthService("secret_key_1", userRepo, 0, clockSupplyingExpiredDates);

        AuthToken expiredJwt = authService.login(existingUser.username, existingUser.username);
        boolean validationResult = authService.validateToken(expiredJwt.token);

        assertFalse(validationResult);
    }
}