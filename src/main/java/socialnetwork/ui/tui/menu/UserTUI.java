package socialnetwork.ui.tui.menu;

import socialnetwork.domain.enums.Role;
import socialnetwork.domain.dto.FriendshipDTO;
import socialnetwork.service.UserService;
import socialnetwork.ui.UI;
import socialnetwork.ui.UIException;
import socialnetwork.ui.tui.BaseTUI;

import java.util.*;

/**
 * {@code User} TUI menu.
 * This menu should provide a textual user interface
 * for the {@code User} connected with
 * all {@code User} functionalities.
 * <p>
 * An instance of this class will be created within {@code MainTUI}
 * when {@code loggedUser} is not null.
 */
public class UserTUI extends BaseTUI {
    private final UserService userService;

    /**
     * Constructor receives an {@code UserService} instance
     * and generates the TUI for according User with
     * {@code loggedUser} role (ADMIN/USER).
     *
     * @param userService instance of {@code UserService}
     */
    public UserTUI(UserService userService) {
        this.userService = userService;
        if (loggedUser != null) {
            if (loggedUser.getRole() == Role.ADMIN) {
                generateTUI("User TUI", new HashMap<String, UI>() {{
                    put("Display all friends", UserTUI.this::displayAllFriends);
                    put("Display all users", UserTUI.this::displayAllUsers);
                    put("Search one user", UserTUI.this::searchOneUser);
                    put("Create new user", UserTUI.this::createUser);
                    put("Delete user", UserTUI.this::deleteUser);
                }});
            } else {
                generateTUI("User TUI", new HashMap<String, UI>() {{
                    put("Display all friends", UserTUI.this::displayAllFriends);
                    put("Search one user", UserTUI.this::searchOneUser);
                    put("Delete account", UserTUI.this::deleteUser);
                }});
            }
        }
    }

    /**
     * Method that creates a new User.
     * If the new User was created, it prints "Operation successful";
     * otherwise "Operation failed".
     */
    private void createUser() {
        Map<String, String> userMap = readMap("firstname", "lastname", "email", "password", "birthdate", "gender");
        System.out.println("Operation " + (userService.createUser(userMap) ? "successful" : "failed"));
    }

    /**
     * Method that searches for a User.
     * It prints out the User founded data via. {@code UserDTO}.
     */
    private void searchOneUser() {
        String userID = readOne("id");
        System.out.println(userService.readOneUser(userID));
    }

    /**
     * Method that displays all Users.
     * It prints out the list of Users data via. {@code UserDTO}.
     */
    private void displayAllUsers() {
        for (String user : userService.readAllUsers()) {
            System.out.println(user);
        }
    }

    /**
     * Method that deletes the {@code loggedUser}.
     * It prints "Operation successful" if the account was successfully deleted;
     * otherwise prints "Operation failed".
     */
    private void deleteUser() {
        String userID = (loggedUser.getRole() == Role.ADMIN ? readOne("id") : loggedUser.getID().toString());
        System.out.println("Operation " + (userService.deleteUser(userID) ? "successful" : "failed"));
        if (loggedUser.getID().toString().equals(userID)) {
            System.exit(0);
        }
    }

    /**
     * Method that displays all friends of a User.
     * The {@code loggedUser} can decide either to filter his list of friends by month
     * and then display it or just to display the full list of his friends.
     */
    private void displayAllFriends() {
        String userID = (loggedUser.getRole() == Role.ADMIN ? readOne("id") : loggedUser.getID().toString());
        String request = readOne(OPTION_PREFIX + " Would you like to filter your friend list? [YES/NO]");
        List<FriendshipDTO> friends;

        switch (request.toUpperCase()) {
            case "YES":
                String filter = readOne("month");
                friends = userService.readFilteredFriends(userID, filter, true);
                break;
            case "NO":
                friends = userService.readFilteredFriends(userID, "", false);
                break;
            default:
                throw new UIException("Please choose between YES or NO!");
        }

        friends.forEach(System.out::println);
    }
}
