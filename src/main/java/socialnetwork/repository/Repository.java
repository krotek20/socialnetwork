package socialnetwork.repository;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.ValidationException;

/**
 * CRUD operations repository interface
 *
 * @param <ID> - type E must have an attribute of type ID
 * @param <E>  - type of entities saved in repository
 */

public interface Repository<ID, E extends Entity<ID>> {

    /**
     * @param id the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id or null if there is no entity with the given id
     * @throws RepositoryException if id is null or if there is no entity with the given id
     */
    E findOne(ID id) throws RepositoryException;

    /**
     * @return all entities
     */
    Iterable<E> findAll();

    /**
     * @param limit  number of entities on a page.
     * @param offset index of the first entity read.
     * @return all entities paged
     */
    Iterable<E> findPage(int limit, int offset);

    /**
     * @param entity to be saved; it must be not null.
     * @return null if the given entity is saved.
     * @throws ValidationException if the entity is not valid.
     * @throws RepositoryException if the entity already exists.
     */
    E save(E entity) throws ValidationException, RepositoryException;


    /**
     * removes the entity with the specified id
     *
     * @param id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws RepositoryException if the given id is null or if there is no entity with the given id
     */
    E delete(ID id) throws RepositoryException;

    /**
     * @param entity entity must not be null.
     * @return null - if the entity is updated,
     * otherwise returns the entity - (e.g id does not exist).
     * @throws ValidationException if the entity is null or not valid.
     * @throws RepositoryException if the entity does not exists.
     */
    E update(E entity) throws ValidationException, RepositoryException;
}
