package beyhum.twitter.repo;

import beyhum.twitter.core.User;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UserItemTest {

    @Test
    public void createBuildsUserItem() {
        User user = new User(UUID.randomUUID(), "player1");

        UserItem actual = UserItem.create(user);

        assertEquals(user.id, actual.getId());
        assertEquals(user.username, actual.getUsername());
    }
}