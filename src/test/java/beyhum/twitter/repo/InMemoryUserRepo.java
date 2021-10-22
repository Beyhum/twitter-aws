package beyhum.twitter.repo;

import beyhum.twitter.core.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryUserRepo implements UserRepo {

    private final List<User> users = new ArrayList<>();

    @Override
    public Optional<User> findUserById(UUID userId) {
        return users.stream().filter(u -> u.id.equals(userId)).findFirst();
    }

    @Override
    public Optional<User> findUserByName(String username) {
        return users.stream().filter(u -> u.username.equals(username)).findFirst();
    }

    @Override
    public void addUser(User user) {
        if (users.stream().anyMatch(u -> u.equals(user))) {
            throw new IllegalStateException("User already exists");
        }
        users.add(user);
    }
}
