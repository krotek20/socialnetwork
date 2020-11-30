package socialnetwork.domain.enums;

/**
 * Enum of friendship statuses
 * with their Database representation.
 * <p>
 * PENDING - 0
 * APPROVED - 1
 * REJECTED - 2
 */
public enum FriendshipStatus {
    PENDING((short) 0), APPROVED((short) 1), REJECTED((short) 2);

    private final short value;

    FriendshipStatus(short value) {
        this.value = value;
    }

    /**
     * Parsing to application value.
     *
     * @param value Database value.
     * @return application value.
     */
    public static FriendshipStatus fromValue(short value) {
        for (FriendshipStatus status : FriendshipStatus.values()) {
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
    public static short toValue(FriendshipStatus status) {
        return status.value;
    }
}
