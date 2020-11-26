package socialnetwork.domain.enums;

public enum FriendshipStatus {
    PENDING((short) 0), APPROVED((short) 1), REJECTED((short) 2);

    private final short value;

    FriendshipStatus(short value) {
        this.value = value;
    }

    public static FriendshipStatus fromValue(short value) {
        for (FriendshipStatus status : FriendshipStatus.values()) {
            if (status.value == value) return status;
        }
        return null;
    }

    public static short toValue(FriendshipStatus status) {
        return status.value;
    }
}
