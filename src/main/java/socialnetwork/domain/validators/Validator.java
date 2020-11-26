package socialnetwork.domain.validators;

public interface Validator<T> {
    /**
     * The implementation should cast the entity to the desired type
     * and throw a validation exception if it is malformed
     *
     * @param entity entity
     * @throws ValidationException exception
     */
    void validate(T entity) throws ValidationException;
}