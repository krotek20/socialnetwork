package socialnetwork.ui.tui.menu;

import socialnetwork.domain.dto.NotificationDTO;
import socialnetwork.domain.entities.Notification;
import socialnetwork.domain.entities.User;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.ui.UI;
import socialnetwork.ui.UIException;
import socialnetwork.ui.tui.BaseTUI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code ReplyNotification} TUI menu.
 * This menu should provide a textual user interface
 * for the {@code User} connected with
 * notification replies functionalities.
 * <p>
 * An instance of this class will be created within {@code NotificationTUI}
 * when {@code loggedUser} is not null.
 */
public class ReplyNotificationTUI extends BaseTUI {
    private final Map<String, Notification> notificationsHelper;
    private final FriendshipService friendshipService;
    private final MessageService messageService;

    /**
     * Constructor receives an {@code FriendshipService} instance
     * along with an {@code MessageService} instance
     * to handle Notifications. Also it receives a Map
     * with all {@code loggedUser} notifications with their IDs
     * <p>
     * It generates the TUI for the {@code loggedUser}
     *
     * @param messageService      instance of {@code MessageService}.
     * @param friendshipService   instance of {@code FriendshipService}.
     * @param notificationsHelper Map with all {@code loggedUser} notifications.
     */
    public ReplyNotificationTUI(FriendshipService friendshipService,
                                MessageService messageService,
                                Map<String, Notification> notificationsHelper) {
        this.notificationsHelper = notificationsHelper;
        this.friendshipService = friendshipService;
        this.messageService = messageService;

        generateTUI("Notification TUI", new HashMap<String, UI>() {{
            put("Read notification", ReplyNotificationTUI.this::displayOneNotification);
            put("Reply to notification", ReplyNotificationTUI.this::replyToNotification);
        }});
    }

    /**
     * Method that displays a notification via. its ID
     * from {@code notificationsHelper}. This function will
     * print out the notification requested data via. {@code NotificationDTO}
     */
    private void displayOneNotification() {
        String id = readOne("id");
        Notification notification = notificationsHelper.get(id);
        NotificationDTO notificationDTO = new NotificationDTO(notification.getFriendship(), notification.getMessage());
        notificationDTO.setFrom(notification.getFrom());
        if (notification.getFriendship() == null) {
            notificationDTO.setTimestamp(notification.getMessage().getTimestamp());
            notificationDTO.setMessageText(notification.getMessage().getMessageText());
        }
        System.out.println(notificationDTO);
    }

    /**
     * Method that handle replying to a notification
     * via. its ID from {@code notificationsHelper}.
     */
    private void replyToNotification() {
        String id = readOne("id");
        Notification notification = notificationsHelper.get(id);
        if (notification.getFriendship() == null) {
            replyMessage(notification);
        } else {
            replyFriendRequest(notification);
        }
    }

    /**
     * This Method will handle replying a message notification.
     *
     * @param notification the Notification the {@code loggedUser}
     *                     wants to reply to. It should store a {@code Message} entity.
     */
    private void replyMessage(Notification notification) {
        Map<String, String> chatMap;
        List<User> chatUsers = notification.getMessage().getID().getUsers();
        String to = null;
        for (User user : chatUsers) {
            if (!user.getID().equals(loggedUser.getID())) {
                to = user.getID().toString();
            }
        }
        chatMap = readMap("message");
        chatMap.put("to", to);
        chatMap.put("reply", notification.getMessage().getMessageText());
        System.out.println("Sending...");
        System.out.println("Message sent at " + messageService.sendMessage(loggedUser, chatMap).getTimestamp());
    }

    /**
     * This Method will handle replying a friend request notification.
     * If the {@code loggedUser} inserts "YES" then the new friendship will be created and stored;
     * otherwise prints "Okay".
     *
     * @param notification the Notification the {@code loggedUser}
     *                     wants to reply to. It should store a {@code Friendship} entity.
     */
    private void replyFriendRequest(Notification notification) {
        String request = readOne(OPTION_PREFIX + "Accept this friend request? [YES/NO]");
        switch (request.toUpperCase()) {
            case "YES":
                String id1 = notification.getFriendship().getID().getLeft().toString();
                String id2 = notification.getFriendship().getID().getRight().toString();
                Map<String, String> friendshipMap = new HashMap<String, String>() {{
                    put("id1", id1);
                    put("id2", id2);
                }};
                friendshipService.addFriendship(friendshipMap);
                System.out.println("Friendship created!");
                break;
            case "NO":
                System.out.println("Okay");
                break;
            default:
                throw new UIException("Please choose between YES or NO!");
        }
    }
}
