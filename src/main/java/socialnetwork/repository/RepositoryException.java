package socialnetwork.repository;

/**
 * This class should handle Repository occurred exceptions.
 * It extends {@code RuntimeException} so instances of
 * this class are throwable.
 */
public class RepositoryException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message. It can be called
     *                using {@link #getMessage()} method.
     */
    public RepositoryException(String message) {
        super(message);
    }
}
