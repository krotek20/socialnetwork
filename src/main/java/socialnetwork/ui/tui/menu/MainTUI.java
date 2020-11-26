package socialnetwork.ui.tui.menu;

import socialnetwork.service.*;
import socialnetwork.ui.UI;
import socialnetwork.ui.tui.BaseTUI;

import java.util.HashMap;

public class MainTUI extends BaseTUI {
    private final NotificationTUI notificationTUI;
    private final FriendshipTUI friendshipTUI;
    private final ChatTUI chatTUI;
    private final UserTUI userTUI;

    public MainTUI(UserService userService, FriendshipService friendshipService, MessageService messageService,
                   NotificationService notificationService, ChatService chatService) {
        this.notificationTUI = new NotificationTUI(notificationService, friendshipService, messageService);
        this.friendshipTUI = new FriendshipTUI(friendshipService);
        this.chatTUI = new ChatTUI(messageService, chatService);
        this.userTUI = new UserTUI(userService);

        generateTUI("Main TUI", new HashMap<String, UI>() {{
            put("Notification menu", MainTUI.this.notificationTUI);
            put("Friendship menu", MainTUI.this.friendshipTUI);
            put("Chat Menu", MainTUI.this.chatTUI);
            put("User menu", MainTUI.this.userTUI);
        }});
    }
}
