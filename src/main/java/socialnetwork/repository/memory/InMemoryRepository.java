package socialnetwork.repository.memory;

import socialnetwork.design.Observable;
import socialnetwork.domain.Entity;
import socialnetwork.domain.entities.Message;
import socialnetwork.domain.entities.Notification;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.util.HashMap;
import java.util.Map;

/**
 * General implementation of an in-memory CRUD repository
 *
 * @param <ID> the id type used by the entities in this repository
 * @param <E>  the type of entity to save in this repository; must extend Entity
 */
public class InMemoryRepository<ID, E extends Entity<ID>> extends Observable implements Repository<ID, E> {
    private final Validator<E> validator;
    private final Map<ID, E> entities;

    /**
     * Constructor
     * Initializing the entities map
     *
     * @param validator validator instance to check entities in before CRUD actions
     */
    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<>();
    }

    /**
     * Get the entity with the given id from the repository
     *
     * @param id the id of the entity to be returned; id must not be null
     * @return the entity with the given id if found; throws RepositoryException otherwise
     * @throws RepositoryException if id is null, not exists or malformed
     */
    @Override
    public E findOne(ID id) throws RepositoryException {
        if (id == null) {
            throw new RepositoryException("id must be not null");
        }
        for (Map.Entry<ID, E> entry : entities.entrySet()) {
            if (entry.getKey().equals(id)) {
                return entry.getValue();
            }
        }
        throw new RepositoryException("id is invalid (not exists or malformed)");
    }

    /**
     * Get all entities in the repository
     *
     * @return an iterable over the in memory repository contents
     */
    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    /**
     * Validate and save an entity in the repository
     *
     * @param entity entity must be not null
     * @return null if successful
     * @throws ValidationException if the entity is not valid
     * @throws RepositoryException if the entity already exists
     */
    @Override
    public E save(E entity) throws ValidationException, RepositoryException {
        validator.validate(entity);
        for (Map.Entry<ID, E> entry : entities.entrySet()) {
            if (entry.getValue().equals(entity)) {
                throw new RepositoryException("Already exists");
            }
        }
        entities.put(entity.getID(), entity);
        setChanged();
        notifyObservers();
        return null;
    }

    /**
     * Remove one entity from the repository
     *
     * @param id id must be not null
     * @return the removed entity if successful; null otherwise
     * @throws RepositoryException if the given id is null or if there is no entity with the given id
     */
    @Override
    public E delete(ID id) throws RepositoryException {
        if (id == null) {
            throw new RepositoryException("id must be not null");
        }
        E result = null;
        for (Map.Entry<ID, E> entry : entities.entrySet()) {
            if (entry.getKey().equals(id)) {
                result = entities.remove(entry.getKey());
                break;
            }
        }
        if (result == null) {
            throw new RepositoryException("id does not exist");
        }
        setChanged();
        notifyObservers();
        return result;
    }

    /**
     * Update one entity in the repository
     *
     * @param entity entity must not be null
     * @return null if successful; the given entity otherwise
     * @throws RepositoryException if id is null or if there is no entity with the given id
     */
    @Override
    public E update(E entity) throws ValidationException {
        validator.validate(entity);
        if (entities.get(entity.getID()) != null) {
            entities.put(entity.getID(), entity);
            setChanged();
            notifyObservers();
            return null;
        }
        return entity;
    }
}
