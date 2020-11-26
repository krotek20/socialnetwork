package socialnetwork.domain.validators;

/**
 * This class should handle Validator occurred exceptions.
 * It extends {@code RuntimeException} so instances of
 * this class are throwable.
 */
public class ValidationException extends RuntimeException {
    public ValidationException() {
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message. It can be called
     *                using {@link #getMessage()} method.
     */
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
