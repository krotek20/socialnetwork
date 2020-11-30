package socialnetwork.repository.database;

import socialnetwork.domain.entities.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.enums.FriendshipStatus;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.sql.*;

public class FriendshipDBRepository
        extends AbstractDBRepository<Tuple<Long, Long>, Friendship>
        implements Repository<Tuple<Long, Long>, Friendship> {

    public FriendshipDBRepository(String url, String username, String password, Validator<Friendship> validator) {
        super(validator, username, password, url);
        this.findAll();
    }

    @Override
    public Friendship extractEntity(ResultSet resultSet) throws SQLException {
        Friendship friendship = new Friendship(
                resultSet.getLong("FIRST_ID"),
                resultSet.getLong("SECOND_ID"),
                resultSet.getLong("ID_NOTIFICATION"));
        friendship.setDate(resultSet.getTimestamp("DATE").toLocalDateTime());
        short dbStatus = resultSet.getShort("FRIENDSHIP_STATUS");
        friendship.setStatus(FriendshipStatus.fromValue(dbStatus));
        return friendship;
    }

    @Override
    public Friendship findOne(Tuple<Long, Long> id) throws RepositoryException {
        if (id == null) {
            throw new RepositoryException("ID must be not null");
        }
        return super.findOne(String.format(
                "select * from \"FRIENDSHIPS\" where" +
                        "(\"FIRST_ID\" = %d and \"SECOND_ID\" = %d) or " +
                        "(\"FIRST_ID\" = %d and \"SECOND_ID\" = %d)",
                id.getLeft(), id.getRight(),
                id.getRight(), id.getLeft()));
    }

    @Override
    public Iterable<Friendship> findAll() {
        return super.findAll("select * from \"FRIENDSHIPS\"");
    }

    @Override
    public Friendship save(Friendship friendship) throws ValidationException, RepositoryException {
        if (findOne(friendship.getID()) != null) {
            throw new RepositoryException("Friendship already exists");
        }
        return super.save(friendship, String.format(
                "insert into \"FRIENDSHIPS\" values(%d, %d, %d, '%s'::timestamp, %d)",
                friendship.getID().getLeft(), friendship.getID().getRight(), friendship.getNotificationID(),
                friendship.getDate(), FriendshipStatus.toValue(friendship.getStatus())));
    }

    @Override
    public Friendship delete(Tuple<Long, Long> id) throws RepositoryException {
        Friendship friendship = findOne(id);
        if (friendship == null) {
            throw new RepositoryException("ID does not exist");
        }
        super.delete(id, String.format("delete from \"FRIENDSHIPS\" where " +
                        "(\"FIRST_ID\" = %d and \"SECOND_ID\" = %d) or " +
                        "(\"FIRST_ID\" = %d and \"SECOND_ID\" = %d)",
                id.getLeft(), id.getRight(),
                id.getRight(), id.getLeft()));
        return friendship;
    }

    @Override
    public Friendship update(Friendship friendship) throws ValidationException, RepositoryException {
        if (findOne(friendship.getID()) == null) {
            throw new RepositoryException("ID is invalid");
        }
        return super.update(friendship, String.format("update \"FRIENDSHIPS\"" +
                        "set \"FRIENDSHIP_STATUS\" = %d where" +
                        "(\"FIRST_ID\" = %d and \"SECOND_ID\" = %d) or " +
                        "(\"FIRST_ID\" = %d and \"SECOND_ID\" = %d)",
                FriendshipStatus.toValue(friendship.getStatus()),
                friendship.getID().getLeft(), friendship.getID().getRight(),
                friendship.getID().getRight(), friendship.getID().getLeft()));
    }
}
