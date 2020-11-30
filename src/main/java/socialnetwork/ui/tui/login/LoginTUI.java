package socialnetwork.ui.tui.login;

import socialnetwork.service.*;
import socialnetwork.ui.tui.BaseTUI;
import socialnetwork.ui.tui.menu.MainTUI;

import java.util.HashMap;
import java.util.Map;

/**
 * Login and Register TUI.
 * This textual user interface will pop up when the application starts.
 * It's purpose is to store publicly the {@code loggedUser} if a client uses valid credentials.
 * Also, a client can register with a new account.
 */
public class LoginTUI extends BaseTUI {
    private final NotificationService notificationService;
    private final FriendshipService friendshipService;
    private final MessageService messageService;
    private final UserService userService;
    private final ChatService chatService;

    /**
     * Within constructor, main app services are parsed and sent to MainTUI.
     * Constructor also generates the Login TUI.
     *
     * @param userService         instance of {@code UserService}
     * @param friendshipService   instance of {@code FriendshipService}
     * @param messageService      instance of {@code MessageService}
     * @param notificationService instance of {@code NotificationService}
     */
    public LoginTUI(UserService userService, FriendshipService friendshipService, MessageService messageService,
                    NotificationService notificationService, ChatService chatService) {
        this.notificationService = notificationService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.userService = userService;
        this.chatService = chatService;

        generateTUI("Login TUI", new HashMap<String, Runnable>() {{
            put("Login", LoginTUI.this::login);
            put("Register", LoginTUI.this::register);
        }});
    }

    private void login() {
        Map<String, String> loginMap = readMap("email", "password");
        loggedUser = userService.verifyCredentials(loginMap);
        if (loggedUser == null) {
            System.out.println("Login failed!");
        } else {
            // start app
            MainTUI mainTUI = new MainTUI(userService, friendshipService,
                    messageService, notificationService, chatService);
            System.out.println("Login successful!");
            mainTUI.run();
        }
    }

    private void register() {
        Map<String, String> userMap = readMap("firstname", "lastname", "email", "password", "birthdate", "gender");
        System.out.println("Register " + (userService.createUser(userMap) ? "successful!" : "failed!"));
    }
}
