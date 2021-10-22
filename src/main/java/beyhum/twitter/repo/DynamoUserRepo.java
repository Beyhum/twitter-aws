package beyhum.twitter.repo;

import beyhum.twitter.core.User;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Optional;
import java.util.UUID;

public class DynamoUserRepo implements UserRepo {

    private final DynamoDbEnhancedClient enhancedClient;

    public DynamoUserRepo(DynamoDbClient dbClient) {
        this.enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dbClient).build();
    }

    @Override
    public Optional<User> findUserById(UUID userId) {
        var user = userTable().getItem(Key.builder().partitionValue(userId.toString()).build());
        return Optional.ofNullable(user).map(u -> new User(u.getId(), u.getUsername()));
    }

    @Override
    public Optional<User> findUserByName(String username) {
        var user = userTable().index(UserItem.USERS_BY_NAME_INDEX)
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(username)))
                .stream().flatMap(p -> p.items().stream()).findFirst();
        return user.map(u -> new User(u.getId(), u.getUsername()));
    }

    @Override
    public void addUser(User user) {
        userTable().putItem(UserItem.create(user));
    }

    private DynamoDbTable<UserItem> userTable() {
        return enhancedClient.table(UserItem.TABLE_NAME, UserItem.TABLE_SCHEMA);
    }
}
