package beyhum.twitter.core;

import org.junit.Test;

import java.util.UUID;


public class UserTest {

    @Test(expected = NullPointerException.class)
    public void userWithNullIdIsInvalid() {
        new User(null, "any");
    }

    @Test(expected = NullPointerException.class)
    public void userWithNullUsernameIsInvalid() {
        new User(UUID.randomUUID(), null);
    }
}