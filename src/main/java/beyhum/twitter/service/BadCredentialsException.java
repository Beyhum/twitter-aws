package beyhum.twitter.service;

public class BadCredentialsException extends Exception {

    public BadCredentialsException(String username) {
        super(String.format("The given username/password do not match or the username '%s' does not exist", username));
    }


}
