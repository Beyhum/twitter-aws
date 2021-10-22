package beyhum.twitter.service;

import java.util.UUID;

public class MockAuthService implements AuthService {

    private final boolean alwaysAuthorize;

    public MockAuthService(boolean alwaysAuthorize) {
        this.alwaysAuthorize = alwaysAuthorize;
    }

    @Override
    public AuthToken login(String username, String password) throws BadCredentialsException {
        if (alwaysAuthorize) {
            return AuthToken.mockToken(UUID.randomUUID(), username);
        } else {
            throw new BadCredentialsException(username);
        }
    }

    @Override
    public boolean validateToken(String token) {
        return alwaysAuthorize;
    }
}