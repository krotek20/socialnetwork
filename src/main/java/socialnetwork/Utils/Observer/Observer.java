package socialnetwork.Utils.Observer;

import socialnetwork.Utils.Events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}
