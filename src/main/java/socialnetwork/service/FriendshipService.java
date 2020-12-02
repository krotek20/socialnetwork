package socialnetwork.service;

import socialnetwork.Utils.Events.ChangeEventType;
import socialnetwork.Utils.Events.FriendshipChangeEvent;
import socialnetwork.Utils.Events.UsersChangeEvent;
import socialnetwork.Utils.Graph;
import socialnetwork.Utils.Observer.Observable;
import socialnetwork.Utils.Observer.Observer;
import socialnetwork.Utils.Parse;
import socialnetwork.domain.*;
import socialnetwork.domain.entities.Chat;
import socialnetwork.domain.entities.Friendship;
import socialnetwork.domain.entities.Notification;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.enums.FriendshipStatus;
import socialnetwork.domain.enums.NotificationType;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Friendship service class
 * Main friendship functionalities are implemented here.
 */
public class FriendshipService extends NotificationService implements Observable<FriendshipChangeEvent> {
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private final Repository<Long, Chat> chatRepository;
    private final Repository<Long, User> userRepository;

    private List<Observer<FriendshipChangeEvent>> observers = new ArrayList<>();

    public FriendshipService(Repository<Tuple<Long, Long>, Friendship> friendshipRepository,
                             Repository<Long, User> userRepository,
                             Repository<Long, Chat> chatRepository,
                             Repository<Long, Notification> notificationRepository) {
        super(notificationRepository);
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    /**
     * Locally validates the sent {@code friendshipMap} Map
     * that contains the desired {@code Friendship} data.
     *
     * @param friendshipMap key-value pairs representing the friendship attributes.
     * @return a new Tuple of User entities validated.
     * @throws RepositoryException if there are no users with such IDs.
     * @throws ServiceException    if the inserted ID values are not numbers.
     */
    private Tuple<User, User> localValidation(Map<String, String> friendshipMap)
            throws RepositoryException, ServiceException {
        Long id1 = Parse.safeParseLong(friendshipMap.get("id1"));
        Long id2 = Parse.safeParseLong(friendshipMap.get("id2"));
        User user1, user2;
        try {
            user1 = userRepository.findOne(id1);
            user2 = userRepository.findOne(id2);
        } catch (RepositoryException e) {
            throw new RepositoryException("Invalid user ID (not exist)");
        }
        return new Tuple<>(user1, user2);
    }

    /**
     * Creates a new chat between two new friends and notifies
     * the user who sent the friend request of the new chat.
     * The title of the new created chat will be formed of
     * the users firstname separated by comma.
     *
     * @param friends tuple of new friends (users).
     * @throws ValidationException when chat data are not valid.
     */
    private void createChatBetweenFriends(Tuple<User, User> friends) throws ValidationException {
        Notification notification = super.createNotification(Set.of(friends.getLeft()), friends.getRight(),
                String.format("You and %s %s are friends now. Start a new conversation in the private chat!",
                        friends.getRight().getFirstName(), friends.getRight().getLastName()),
                NotificationType.CHAT, "New chat!", LocalDateTime.now());
        super.updateSeenNotification(notification);

        Chat chat = new Chat(friends.getLeft().getFirstName() + ", " + friends.getRight().getFirstName(),
                notification.getID());
        chat.setCount(chat.getID());
        chat.setUsers(Set.of(friends.getLeft(), friends.getRight()));
        chatRepository.save(chat);
    }

    /**
     * Sets the status of the given friendship as APPROVED.
     * A new chat between the new friends will be created.
     *
     * @param friendship instance of friendship.
     * @throws ValidationException when at least one value is invalid.
     * @throws RepositoryException when the given friendship is not found in repository.
     */
    public void acceptFriendship(Friendship friendship)
            throws ValidationException, RepositoryException {
        User firstFriend = userRepository.findOne(friendship.getID().getLeft());
        User secondFriend = userRepository.findOne(friendship.getID().getRight());
        createChatBetweenFriends(new Tuple<>(firstFriend, secondFriend));
        friendship.setStatus(FriendshipStatus.APPROVED);

        friendshipRepository.update(friendship);
        notifyObservers(new FriendshipChangeEvent(ChangeEventType.UPDATE, friendship));
    }

    /**
     * Sets given friendship status as REJECTED.
     *
     * @param friendship instance of friendship.
     * @throws ValidationException when at least one value is invalid.
     * @throws RepositoryException when the given friendship is not found in repository.
     */
    public void rejectFriendship(Friendship friendship)
            throws ValidationException, RepositoryException {
        friendship.setStatus(FriendshipStatus.REJECTED);
        friendshipRepository.update(friendship);
        notifyObservers(new FriendshipChangeEvent(ChangeEventType.UPDATE, friendship));
    }

    /**
     * Requests a new Friendship between two users.
     * It saves the new Friendship to get its data for a notification
     * with the status PENDING and sends a notification to the requested user.
     *
     * @param friendshipMap key-value pairs representing the friendship attributes.
     * @return true if the request has been successfully sent
     * and the friendship has been saved with success; otherwise false.
     * @throws ValidationException when at least one value is invalid.
     * @throws RepositoryException when id does not exist in User repository.
     * @throws ServiceException    when id is not a Long parsable value.
     */
    public boolean requestFriendship(Map<String, String> friendshipMap)
            throws ValidationException, RepositoryException, ServiceException {
        Tuple<User, User> newFriends = localValidation(friendshipMap);
        Notification notification = super.createNotification(
                Set.of(newFriends.getLeft()), newFriends.getRight(), String.format(
                        "%s %s sent you a friend request",
                        newFriends.getRight().getFirstName(), newFriends.getRight().getLastName()),
                NotificationType.FRIENDSHIP, "New friend request!", LocalDateTime.now());
        Friendship friendship = new Friendship(newFriends.getLeft().getID(),
                newFriends.getRight().getID(), notification.getID());
        return friendshipRepository.save(friendship) == null;
    }

    /**
     * Read the friendship of the given user ids
     *
     * @param friendshipMap key-value pairs representing the friendship attributes
     * @return found friendship as string; empty string otherwise
     * @throws ValidationException when id string is empty or contains an invalid number
     * @throws ServiceException    when id is not a Long parsable value.
     */
    public String readOneFriendship(Map<String, String> friendshipMap) throws ValidationException, ServiceException {
        Long id1 = Parse.safeParseLong(friendshipMap.get("id1"));
        Long id2 = Parse.safeParseLong(friendshipMap.get("id2"));
        Friendship friendship = friendshipRepository.findOne(new Tuple<>(id1, id2));
        return friendship != null ? friendship.toString() : "Friendship not found!";
    }

    /**
     * Read all friendships
     *
     * @return a string list of all friendships
     */
    public List<String> readAllFriendships() {
        List<String> list = StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .map(Friendship::toString)
                .collect(Collectors.toList());
        return list.size() != 0 ? list : new ArrayList<>(Collections.singletonList("The list of friendships is empty"));
    }

    /**
     * Reads the friendship with the given notification ID.
     *
     * @param notificationID ID of notification.
     * @return instance of friendship found or null if there is
     * no friendship with the specified notification ID.
     */
    public Friendship readFriendshipByNotification(long notificationID) {
        Iterable<Friendship> friendships = friendshipRepository.findAll();
        for (Friendship friendship : friendships) {
            if (friendship.getNotificationID() == notificationID) {
                return friendship;
            }
        }
        return null;
    }

    /**
     * Delete the friendship of the given user ids
     *
     * @param friendshipMap key-value pairs representing the friendship attributes
     * @return true if friendship is found and deleted; false otherwise
     * @throws RepositoryException if the given id is null or if there is no entity with the given id
     * @throws ServiceException    when one of the ids is not a Long parsable value.
     */
    public boolean deleteFriendship(Map<String, String> friendshipMap) throws RepositoryException, ServiceException {
        long id1 = Parse.safeParseLong(friendshipMap.get("id1"));
        long id2 = Parse.safeParseLong(friendshipMap.get("id2"));
        Friendship friendship = friendshipRepository.findOne(new Tuple<>(id1, id2));
        boolean status = friendshipRepository.delete(new Tuple<>(id1, id2)) != null;
        if(status){
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE, friendship));
        }
        return status;
    }

    /**
     * Initializing a new {@code Graph} of users
     *
     * @return the new {@code Graph} of users
     */
    private Graph computeGraph() {
        Iterable<Friendship> friendships = friendshipRepository.findAll();
        Iterable<User> users = userRepository.findAll();
        Graph graph = new Graph(users);
        for (Friendship friendship : friendships) {
            graph.addEdge(friendship.getID().getLeft(), friendship.getID().getRight());
        }
        return graph;
    }

    /**
     * Find the number of communities
     *
     * @return the number of communities (long)
     */
    public long getNumberOfCommunities() {
        Graph graph = computeGraph();
        return graph.countConnectedComponents();
    }

    /**
     * Find the largest community (most sociable community)
     *
     * @return list of largest community users
     */
    public List<Long> getLargestCommunity() {
        Graph graph = computeGraph();
        return graph.getLargestComponent();
    }

    @Override
    public void addObserver(Observer<FriendshipChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendshipChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendshipChangeEvent t) {
        observers.stream().forEach(x -> x.update(t));
    }
}
