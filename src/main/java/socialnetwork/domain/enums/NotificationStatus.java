package socialnetwork.domain.enums;

public enum NotificationStatus {
    SEEN((short) 0), UNSEEN((short) 1);

    private final short value;

    NotificationStatus(short value) {
        this.value = value;
    }

    public static NotificationStatus fromValue(short value) {
        for (NotificationStatus status : NotificationStatus.values()) {
            if (status.value == value) return status;
        }
        return null;
    }

    public static short toValue(NotificationStatus status) {
        return status.value;
    }
}
