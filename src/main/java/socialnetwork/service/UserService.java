package socialnetwork.service;

import socialnetwork.Utils.Parse;
import socialnetwork.domain.entities.Friendship;
import socialnetwork.domain.enums.Gender;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.dto.FriendshipDTO;
import socialnetwork.domain.dto.UserDTO;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.time.LocalDate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * User service class
 * Main user functionalities are implemented here
 */
public class UserService {
    private final Repository<Long, User> userRepository;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;

    public UserService(Repository<Long, User> userRepository,
                       Repository<Tuple<Long, Long>, Friendship> friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * Verify the inserted credentials when someone tries to login.
     * It loops through all users found in memory
     * and matches the credentials with every user found.
     *
     * @param loginMap contains pairs of key-value where keys are "email" and "password"
     *                 and values are the user inserted email and password
     * @return the User found by his credentials; null otherwise.
     */
    public User verifyCredentials(Map<String, String> loginMap) {
        String email = loginMap.get("email");
        String password = loginMap.get("password");
        Iterable<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Finds all friendships of the user with the given id.
     *
     * @param id id of the user
     * @return list of the current user's friendships.
     */
    private List<Friendship> findFriendships(Long id) {
        Iterable<Friendship> friendships = friendshipRepository.findAll();
        List<Friendship> currentUserFriendships = new ArrayList<>();
        for (Friendship friendship : friendships) {
            if (friendship.getID().getLeft().equals(id) || friendship.getID().getRight().equals(id)) {
                currentUserFriendships.add(friendship);
            }
        }
        return currentUserFriendships;
    }

    /**
     * Creates a new user from the input data.
     *
     * @param userMap key-value pairs corresponding to the users
     * @return true if saved; false otherwise
     * @throws ValidationException when at least one value is invalid.
     * @throws ServiceException    when input birthdate or gender are not
     *                             LocalDate or {@code Gender} parsable values.
     */
    public boolean createUser(Map<String, String> userMap) throws ValidationException, ServiceException {
        LocalDate birthdate = Parse.safeParseLocalDate(userMap.get("birthdate"));
        Gender gender = Parse.safeParseGender(userMap.get("gender"));
        User user = new User(
                userMap.get("firstname"),
                userMap.get("lastname"),
                userMap.get("email"),
                userMap.get("password"),
                birthdate,
                gender
        );
        user.setCount(user.getID());
        return userRepository.save(user) == null;
    }

    /**
     * Read the user with the given ID.
     *
     * @param id number representing the user ID
     * @return found user as string
     * @throws RepositoryException when id string is empty or contains an invalid number.
     * @throws ServiceException    when id is not a Long parsable value
     *                             or when the founded user is null.
     */
    public String readOneUser(String id) throws RepositoryException, ServiceException {
        User user = userRepository.findOne(Parse.safeParseLong(id));
        if (user == null) {
            throw new ServiceException("User not found!");
        }
        return convertToUserDTO(user).toString();

    }

    /**
     * Read all users.
     *
     * @return a string list of all users.
     */
    public List<String> readAllUsers() {
        List<String> list = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(this::convertToUserDTO)
                .map(UserDTO::toString)
                .collect(Collectors.toList());
        return list.size() != 0 ? list : new ArrayList<>(Collections.singletonList("The list of users is empty"));
    }

    /**
     * Delete the user with the given ID.
     *
     * @param id number representing the user ID
     * @return true if user is found and deleted; false otherwise
     * @throws RepositoryException when id string is empty or contains an invalid number.
     * @throws ServiceException    when id is not a Long parsable value.
     */
    public boolean deleteUser(String id) throws RepositoryException, ServiceException {
        Long safeId = Parse.safeParseLong(id);
        User deletedUser = userRepository.delete(safeId);
        if (deletedUser == null) {
            return false;
        }
        List<Friendship> friendships = findFriendships(safeId);
        for (Friendship friendship : friendships) {
            friendshipRepository.delete(friendship.getID());
        }
        return true;
    }

    /**
     * This method searches through an input {@code User} data
     * and gets the information that needs to be shown
     * for the current user request. It uses a {@code UserDTO}
     * to keep user's data by setting up his attributes
     * with the according user information.
     *
     * @param user instance of user to be transferred.
     * @return an {@code UserDTO}. It keeps user's
     * firstname, lastname, email, birthdate and gender
     * in its implementation.
     */
    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();

        userDTO.setFirstname(user.getFirstName());
        userDTO.setLastname(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setBirthDate(user.getBirthDate());
        userDTO.setGender(user.getGender());

        return userDTO;
    }

    /**
     * This method searches through an input {@code friendship} data
     * and gets the friend of the user with id {@code userID}
     * along with the date when the friendship was created.
     *
     * @param friendship friendship entity
     * @param userID     the id of the user with the given friendship
     * @return a {@code FriendshipDTO} object with the friend
     * firstname and lastname data and the date when his friendship
     * with the desired user has been created
     */
    private FriendshipDTO convertToFriendshipDTO(Friendship friendship, long userID) {
        FriendshipDTO friendshipDTO = new FriendshipDTO();

        long friendID = (friendship.getID().getLeft().equals(userID) ?
                friendship.getID().getRight() : friendship.getID().getLeft());
        User friend = userRepository.findOne(friendID);

        friendshipDTO.setFirstname(friend.getFirstName());
        friendshipDTO.setLastname(friend.getLastName());
        friendshipDTO.setDate(friendship.getDate());

        return friendshipDTO;
    }

    /**
     * If {@code filtered} is false read all friendships of the user with {@code id};
     * otherwise read all friendships filtered by {@code filter} property.
     *
     * @param id       String of the user ID
     * @param filter   a String representing the month when a friendship
     *                 was created; it's used for filtering the
     *                 {@code FriendshipDTO} list whenever
     *                 {@code filtered} is true
     * @param filtered boolean; if true the filtered list should be returned;
     *                 otherwise (false) the complete friend list should be returned.
     * @return list of {@code FriendshipDTO} containing
     * the firstname(s) and the lastname(s) of the user's friend.
     * It also contains the date when the friendship was created.
     * @throws ServiceException when id or filter are not Long parsable values.
     */
    public List<FriendshipDTO> readFilteredFriends(String id, String filter, boolean filtered) throws ServiceException {
        long userID = Parse.safeParseLong(id);
        long filterMonth = (filter.equals("") ? 0 : Parse.safeParseLong(filter));
        return (filtered ?
                findFriendships(userID)
                        .stream()
                        .map(x -> convertToFriendshipDTO(x, userID))
                        .filter(x -> x.getDate().getMonthValue() == filterMonth)
                        .collect(Collectors.toList())
                :
                findFriendships(userID)
                        .stream()
                        .map(x -> convertToFriendshipDTO(x, userID))
                        .collect(Collectors.toList()));
    }
}
