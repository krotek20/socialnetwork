package socialnetwork.domain.enums;

/**
 * Enum of Notification statuses
 * with their Database representation.
 * <p>
 * SEEN - 0
 * UNSEEN - 1
 */
public enum NotificationStatus {
    SEEN((short) 0), UNSEEN((short) 1);

    private final short value;

    NotificationStatus(short value) {
        this.value = value;
    }

    /**
     * Parsing to application value.
     *
     * @param value Database value.
     * @return application value.
     */
    public static NotificationStatus fromValue(short value) {
        for (NotificationStatus status : NotificationStatus.values()) {
            if (status.value == value) return status;
        }
        return null;
    }

    /**
     * Parsing to database value.
     *
     * @param status Application value.
     * @return Database value.
     */
    public static short toValue(NotificationStatus status) {
        return status.value;
    }
}
