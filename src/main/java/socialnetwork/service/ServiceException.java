package socialnetwork.service;

/**
 * This class should handle Service occurred exceptions.
 * It extends {@code RuntimeException} so instances of
 * this class are throwable.
 */
public class ServiceException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message. It can be called
     *                using {@link #getMessage()} method.
     */
    public ServiceException(String message) {
        super(message);
    }
}