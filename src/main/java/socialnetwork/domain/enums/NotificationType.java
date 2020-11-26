package socialnetwork.domain.enums;

public enum NotificationType {
    FRIENDSHIP((short) 0), CHAT((short) 1), MESSAGE((short) 2);

    private final short value;

    NotificationType(short value) {
        this.value = value;
    }

    public static NotificationType fromValue(short value) {
        for (NotificationType type : NotificationType.values()) {
            if (type.value == value) return type;
        }
        return null;
    }

    public static short toValue(NotificationType type) {
        return type.value;
    }
}
