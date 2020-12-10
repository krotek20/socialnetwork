package socialnetwork.Utils.design;

/**
 * A class can implement the {@code Observer} interface when it
 * wants to be informed of changes in observable objects.
 */
public interface Observer {
    /**
     * Method to update the observer,
     * used by {@code Observable} (notifying actions)
     *
     * @param status the current update status.
     */
    void update(NotifyStatus status);
}
