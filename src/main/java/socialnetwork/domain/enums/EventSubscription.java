package socialnetwork.domain.enums;

/**
 * Enum of Event Subscription statuses
 * with their Database representation.
 * <p>
 * SUBSCRIBE - 0
 * UNSUBSCRIBE - 1
 */
public enum EventSubscription {
    SUBSCRIBE((short) 0), UNSUBSCRIBE((short) 1);

    private final short value;

    EventSubscription(short value) {
        this.value = value;
    }

    /**
     * Parsing to application value.
     *
     * @param value Database value.
     * @return application value.
     */
    public static EventSubscription fromValue(short value) {
        for (EventSubscription subscription : EventSubscription.values()) {
            if (subscription.value == value) return subscription;
        }
        return null;
    }

    /**
     * Parsing to database value.
     *
     * @param subscription Application value.
     * @return Database value.
     */
    public static short toValue(EventSubscription subscription) {
        return subscription.value;
    }

}
