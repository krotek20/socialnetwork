package socialnetwork.service;

import socialnetwork.Utils.Graph;
import socialnetwork.Utils.Parse;
import socialnetwork.domain.*;
import socialnetwork.domain.entities.Chat;
import socialnetwork.domain.entities.Friendship;
import socialnetwork.domain.entities.Notification;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Friendship service class
 * Main friendship functionalities are implemented here
 */
public class FriendshipService extends NotificationService {
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private final Repository<Long, Chat> chatRepository;
    private final Repository<Long, User> userRepository;

    public FriendshipService(Repository<Tuple<Long, Long>, Friendship> friendshipRepository,
                             Repository<Long, User> userRepository,
                             Repository<Long, Chat> chatRepository,
                             Repository<User, Notification> notificationRepository) {
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
     * Creates a new chat between two new friends.
     * The name of the new created chat will be formed of
     * the users firstname separated by comma.
     *
     * @param friends tuple of new friends (users).
     * @throws ValidationException when chat data are not valid.
     */
    private void createChatBetweenFriends(Tuple<User, User> friends) throws ValidationException {
        Chat chat = new Chat(friends.getLeft().getFirstName() + ", " + friends.getRight().getFirstName());
        chat.setCount(chat.getID());
        chat.setUsers(Set.of(friends.getLeft(), friends.getRight()));
        chatRepository.save(chat);
    }

    /**
     * Add a new friendship between two users from the input data.
     * This method validates the input users. If they are valid
     * then a new chat between them will be created.
     *
     * @param friendshipMap key-value pairs representing the friendship attributes.
     * @return true if saved; false otherwise
     * @throws ValidationException when at least one value is invalid.
     * @throws RepositoryException when id does not exist in User repository.
     * @throws ServiceException    when id is not a Long parsable value.
     */
    public boolean addFriendship(Map<String, String> friendshipMap)
            throws ValidationException, RepositoryException, ServiceException {
        Tuple<User, User> newFriends = localValidation(friendshipMap);
        createChatBetweenFriends(newFriends);
        Friendship friendship = new Friendship(newFriends.getLeft().getID(), newFriends.getRight().getID());
        return friendshipRepository.save(friendship) == null;
    }

    /**
     * Requests a new Friendship between two users.
     * It temporary saves the new Friendship to get its data for a notification
     * and then deletes it until the notifiedUser will accept the request.
     *
     * @param friendshipMap key-value pairs representing the friendship attributes.
     * @return true if the request has been successfully sent; otherwise false.
     * @throws ValidationException when at least one value is invalid.
     * @throws RepositoryException when id does not exist in User repository.
     * @throws ServiceException    when id is not a Long parsable value.
     */
    public boolean requestFriendship(Map<String, String> friendshipMap)
            throws ValidationException, RepositoryException, ServiceException {
        Tuple<User, User> newFriends = localValidation(friendshipMap);
        Friendship friendship = new Friendship(newFriends.getLeft().getID(), newFriends.getRight().getID());
        if (friendshipRepository.save(friendship) == null) {
            deleteFriendship(friendshipMap);
            super.createNotification(newFriends.getLeft(), newFriends.getRight(),
                    null, friendship, "New friend request!");
            return true;
        }
        return false;
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
        return friendship != null ? friendship.toString() : "";
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
        return friendshipRepository.delete(new Tuple<>(id1, id2)) != null;
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
}
