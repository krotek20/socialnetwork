package socialnetwork.repository.database;

import org.apache.commons.dbutils.handlers.MapListHandler;
import org.postgresql.util.PSQLException;
import socialnetwork.domain.Entity;
import socialnetwork.domain.enums.NotificationStatus;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.RepositoryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Abstract class which implements the workflow
 * of the database repository.
 *
 * @param <ID> unique identifier type
 * @param <E>  entities type
 */
public abstract class AbstractDBRepository<ID, E extends Entity<ID>> extends MapListHandler {
    private final Validator<E> validator;

    public AbstractDBRepository(Validator<E> validator) {
        this.validator = validator;
    }

    public abstract E extractEntity(Map<String, Object> resultSet);

    /**
     * Gets the users according to the
     * specified table in the given query.
     *
     * @param query database query to retrieve
     *              user IDs of the specified table.
     * @return set of user ids.
     */
    public Set<Long> getUsersID(String query) {
        Set<Long> usersID = new HashSet<>();
        try (Connection connection = PooledDataSource.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                usersID.add(resultSet.getLong("ID_USER"));
            }
        } catch (PSQLException ignored) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usersID;
    }

    /**
     * Gets users with the according status of the notification
     * with the specified notification ID.
     *
     * @param query database query to retrieve user IDs
     *              and their notification status of the specified table.
     * @return map of user ids with their according notification status.
     */
    public Map<Long, NotificationStatus> getNotifiedUsersID(String query) {
        Map<Long, NotificationStatus> usersID = new HashMap<>();
        try (Connection connection = PooledDataSource.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                usersID.put(resultSet.getLong("ID_USER"), NotificationStatus.fromValue(
                        resultSet.getShort("NOTIFICATION_STATUS")));
            }
        } catch (PSQLException ignored) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usersID;
    }

    public E findOne(String query) {
        Map<String, Object> result = new HashMap<>();
        try (Connection connection = PooledDataSource.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }
            result = handleRow(resultSet);
        } catch (PSQLException ignored) {
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return extractEntity(result);
    }

    public Iterable<E> findAll(String query) {
        Set<E> entity = new HashSet<>();
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection connection = PooledDataSource.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(handleRow(resultSet));
            }
        } catch (PSQLException ignored) {
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Map<String, Object> row : result) {
            entity.add(extractEntity(row));
        }
        return entity;
    }

    public E save(E entity, String query) throws ValidationException {
        validator.validate(entity);
        try (Connection connection = PooledDataSource.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
            entity = null;
        } catch (PSQLException ignored) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public void delete(ID id, String query) throws RepositoryException {
        if (id == null) {
            throw new RepositoryException("id must be not null");
        }
        try (Connection connection = PooledDataSource.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
        } catch (PSQLException ignored) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public E update(E entity, String query) throws ValidationException {
        validator.validate(entity);
        try (Connection connection = PooledDataSource.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
            entity = null;
        } catch (PSQLException ignored) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }
}
