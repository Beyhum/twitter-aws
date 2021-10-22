package beyhum.twitter.service;

public interface AuthService {

    /**
     *
     * @param username
     * @param password
     * @return
     * @throws BadCredentialsException If the username and password are invalid or do not exist
     */
    AuthToken login(String username, String password) throws BadCredentialsException;

    /**
     * Checks if the given JWT is valid (signed by approved key, not expired)
     * @param token the JWT to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);
}
