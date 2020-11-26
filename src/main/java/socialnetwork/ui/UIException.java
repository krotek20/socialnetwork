package socialnetwork.ui;

/**
 * This class should handle UI occurred exceptions.
 * It extends {@code RuntimeException} so instances of
 * this class are throwable.
 */
public class UIException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message. It can be called
     *                using {@link #getMessage()} method.
     */
    public UIException(String message) {
        super(message);
    }
}