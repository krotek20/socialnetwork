package socialnetwork.design;

import java.util.HashSet;
import java.util.Set;

/**
 * It can be subclassed to represent an object that the application wants to have observed.
 * An observable object can have one or more observers.
 * An observer may be any object that implements interface {@code Observer}.
 */
public class Observable {
    private final Set<Observer> observers;
    private boolean changed = false;

    /**
     * Construct an Observable with zero Observers
     */
    public Observable() {
        this.observers = new HashSet<>();
    }

    /**
     * Registers an observer to the set of observers for this object, provided
     * that it is not the same as some observer already in the set.
     * The order in which notifications will be delivered to multiple
     * observers is not specified.
     *
     * @param observer an observer to be registered.
     * @throws NullPointerException if the observer is null.
     */
    public void register(Observer observer) {
        if (observer == null) {
            throw new NullPointerException("Null Observer");
        }
        observers.add(observer);
    }

    /**
     * Unregisters an observer from the set of observers of this object.
     * Passing {@code null} to this method will have no effect.
     *
     * @param observer the observer to be unregistered.
     */
    public void unregister(Observer observer) {
        observers.remove(observer);
    }

    /**
     * If this object has changed, then notify all of its observers
     * and then call the {@code clearChanged} method to indicate
     * that this object has no longer changed.
     */
    public void notifyObservers() {
        Set<Observer> localObservers;
        synchronized (this) {
            if (!this.changed) return;
            localObservers = new HashSet<>(this.observers);
            clearChanged();
        }
        for (Observer observer : localObservers) {
            observer.update(this);
        }
    }

    /**
     * Marks this {@code Observable} object as having been changed.
     */
    public void setChanged() {
        this.changed = true;
    }

    /**
     * Indicates that this object has no longer changed, or that it has
     * already notified all of its observers of its most recent change.
     * This method is called automatically by the {@code notifyObservers} methods.
     */
    public void clearChanged() {
        this.changed = false;
    }
}
