package beyhum.twitter.repo;

import beyhum.twitter.core.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo {
    Optional<User> findUserById(UUID userId);

    Optional<User> findUserByName(String username);

    void addUser(User user);

}
