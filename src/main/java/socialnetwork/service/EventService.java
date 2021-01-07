package socialnetwork.service;

import socialnetwork.Utils.design.NotifyStatus;
import socialnetwork.Utils.design.Observable;
import socialnetwork.domain.entities.Event;
import socialnetwork.domain.entities.Notification;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.enums.EventSubscription;
import socialnetwork.domain.enums.NotificationType;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.RepositoryException;
import socialnetwork.repository.database.EventDBRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EventService extends Observable {
    private final EventDBRepository eventRepository;

    public EventService(EventDBRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Creates a new event entity and saves it in repository.
     *
     * @param title      event title (string).
     * @param startAt    the date and time when the event will took place.
     * @param loggedUser currently logged in user.
     * @return the new created event entity if saved successfully; null otherwise.
     * @throws ValidationException when at least one value is invalid.
     */
    public Event createEvent(String title, LocalDateTime startAt, User loggedUser)
            throws ValidationException {
        Set<User> users = new HashSet<>();
        users.add(loggedUser);

        Notification notification = NotificationService.createNotification(
                users, loggedUser, String.format("You have created a new event: %s", title),
                NotificationType.EVENT, "New event!", LocalDateTime.now());

        Event event = new Event(title, startAt, notification.getID());
        event.setCount(event.getID());
        event.addUser(loggedUser);
        if (eventRepository.save(event) == null) {
            setChanged();
            notifyObservers(NotifyStatus.EVENT);
            return event;
        }
        return null;
    }

    /**
     * Reads all the events from database
     * and collects them into a list.
     *
     * @return a list of events.
     */
    public List<Event> readAllEvents() {
        return StreamSupport.stream(eventRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * Reads the events {@code user} joined.
     *
     * @param user instance of the user that wants to read his events.
     * @return a list of events.
     */
    public List<Event> readAllUserEvents(User user) {
        return StreamSupport.stream(eventRepository.findAll().spliterator(), false)
                .filter(x -> x.getUsers()
                        .entrySet()
                        .stream()
                        .anyMatch(y -> y.getKey().getID().equals(user.getID())))
                .collect(Collectors.toList());
    }

    /**
     * Adds a new user to an existing event.
     *
     * @param event an instance of the event.
     * @param user  user to be added.
     * @return the event entity if the user has been added successfully; null otherwise
     * @throws ValidationException if the event entity is not valid.
     */
    public Event addUserToEvent(Event event, User user) throws ValidationException {
        event.addUser(user);
        if (eventRepository.saveUser(event, user) == null) {
            setChanged();
            notifyObservers(NotifyStatus.EVENT);
            return event;
        }
        return null;
    }

    /**
     * Changes the subscription of {@code user} in
     * {@code event} with the new {@code subscription}.
     *
     * @param event        instance of desired event.
     * @param user         the user who wants to change his subscription.
     * @param subscription the new subscription (EventSubscription).
     * @throws ValidationException if event entity is not valid.
     * @throws RepositoryException if event ID is invalid.
     */
    public void changeUserSubscription(Event event, User user, EventSubscription subscription)
            throws ValidationException, RepositoryException {
        eventRepository.updateSubscription(event, user, subscription);
        setChanged();
        notifyObservers(NotifyStatus.EVENT);
    }

    /**
     * Creates a new notification for {@code event}
     * with the {@code remainingTime}.
     *
     * @param event         the current event entity.
     * @param remainingTime short value to describe the remaining time
     *                      until the event will start
     *                      0 - one day, 1 - one hour, 2 - 10 minutes.
     */
    public void notifyEventUsers(Event event, short remainingTime) {
        String time = (remainingTime == 0 ? "One day" : (remainingTime == 1 ? "One hour" : "10 minutes"));
        List<User> users = new ArrayList<>();
        for (Map.Entry<User, EventSubscription> entry : event.getUsers().entrySet()) {
            if (entry.getValue() == EventSubscription.SUBSCRIBE) {
                users.add(entry.getKey());
            }
        }
        if (users.size() != 0) {
            Notification notification = NotificationService.createNotification(
                    new HashSet<>(users), users.get(0),
                    String.format("%s left until the event %s will start!", time, event.getTitle()),
                    NotificationType.EVENT, "Event will start soon!", LocalDateTime.now());

            event.setNotificationID(notification.getID());
            if (eventRepository.update(event) == null) {
                setChanged();
                notifyObservers(NotifyStatus.EVENT);
            }
        }
    }
}
