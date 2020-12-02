package socialnetwork.Utils.Observer;

import socialnetwork.Utils.Events.Event;

public interface Observable <E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
