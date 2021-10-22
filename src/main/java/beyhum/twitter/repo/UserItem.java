package beyhum.twitter.repo;

import beyhum.twitter.core.User;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.util.UUID;

@DynamoDbBean
public class UserItem {
    static final String TABLE_NAME = "User";
    static final TableSchema<UserItem> TABLE_SCHEMA = TableSchema.fromBean(UserItem.class);
    static final String USERS_BY_NAME_INDEX = "UsersByName";

    static final String ID_ATTR = "Id";
    static final String USERNAME_ATTR = "Username";


    private UUID id;
    private String username;

    static UserItem create(User user) {
        UserItem userItem = new UserItem();
        userItem.id = user.id;
        userItem.username = user.username;
        return  userItem;
    }

    /**
     * Required for dynamodb mapper reflection
     */
    public UserItem() { }

    @DynamoDbPartitionKey
    @DynamoDbAttribute(ID_ATTR)
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = USERS_BY_NAME_INDEX)
    @DynamoDbAttribute(USERNAME_ATTR)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
