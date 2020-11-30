package socialnetwork.domain.enums;

/**
 * Enum of Notification types
 * with their Database representation.
 * <p>
 * FRIENDSHIP - 0
 * CHAT - 1
 * MESSAGE - 2
 */
public enum NotificationType {
    FRIENDSHIP((short) 0), CHAT((short) 1), MESSAGE((short) 2);

    private final short value;

    NotificationType(short value) {
        this.value = value;
    }

    /**
     * Parsing to application value.
     *
     * @param value Database value.
     * @return application value.
     */
    public static NotificationType fromValue(short value) {
        for (NotificationType type : NotificationType.values()) {
            if (type.value == value) return type;
        }
        return null;
    }

    /**
     * Parsing to database value.
     *
     * @param type Application value.
     * @return Database value.
     */
    public static short toValue(NotificationType type) {
        return type.value;
    }
}
