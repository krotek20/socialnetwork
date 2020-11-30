package socialnetwork.ui.tui.menu;

import socialnetwork.Utils.Constants;
import socialnetwork.domain.entities.Friendship;
import socialnetwork.domain.entities.Message;
import socialnetwork.domain.entities.Notification;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.NotificationService;
import socialnetwork.ui.UI;
import socialnetwork.ui.UIException;
import socialnetwork.ui.tui.BaseTUI;

import java.util.HashMap;
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
    private final NotificationService notificationService;
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
    public ReplyNotificationTUI(NotificationService notificationService,
                                FriendshipService friendshipService,
                                MessageService messageService,
                                Map<String, Notification> notificationsHelper) {
        this.notificationsHelper = notificationsHelper;
        this.notificationService = notificationService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;

        generateTUI("Notification TUI", new HashMap<String, Runnable>() {{
            put("Read notification", ReplyNotificationTUI.this::displayOneNotification);
            put("Reply to notification", ReplyNotificationTUI.this::replyToNotification);
        }});
    }

    /**
     * Method that displays a notification via. its ID
     * from {@code notificationsHelper}. This function will
     * print out the notification requested data via. {@code NotificationDTO}
     *
     * @throws UIException if the input id is invalid.
     */
    private void displayOneNotification() throws UIException {
        String id = readOne("id");
        Notification notification = notificationsHelper.get(id);
        if (notification == null) {
            throw new UIException("Invalid ID");
        }
        System.out.println(notification.getEntityText());
    }

    /**
     * Method that handle the reply to a notification functionality
     * via. its ID from {@code notificationsHelper}.
     *
     * @throws UIException if the input id is invalid.
     */
    private void replyToNotification() throws UIException {
        String id = readOne("id");
        Notification notification = notificationsHelper.get(id);
        if (notification == null) {
            throw new UIException("Invalid ID");
        }

        switch (notification.getType()) {
            case MESSAGE:
                replyMessage(notification);
                break;
            case FRIENDSHIP:
                replyFriendRequest(notification);
                break;
            case CHAT:
                break;
            default:
                throw new UIException("Invalid option!");
        }
        notificationService.updateSeenNotification(notification);
    }

    /**
     * This Method will handle the reply functionality for a message.
     *
     * @param notification the Notification the {@code loggedUser}
     *                     wants to reply to. It should store a {@code Message} entity.
     */
    private void replyMessage(Notification notification) {
        Message message = messageService.readMessageByNotification(notification.getID());
        Map<String, String> chatMap = readMap("message");

        chatMap.put("to", message.getChat().getID().toString());
        chatMap.put("reply", message.getMessageText());

        System.out.println("Sending...");
        System.out.println("Message sent at " +
                messageService.sendMessage(loggedUser, chatMap).getTimestamp()
                        .format(Constants.DATE_TIME_FORMATTER));
    }

    /**
     * This Method will handle the reply functionality for a friend request.
     * If the {@code loggedUser} inserts "YES" then the friendship
     * will be APPROVED; otherwise prints it will be REJECTED.
     *
     * @param notification the Notification the {@code loggedUser} wants to reply to.
     * @throws UIException if the insert option is not YES or NO.
     */
    private void replyFriendRequest(Notification notification) throws UIException {
        Friendship friendship = friendshipService.readFriendshipByNotification(notification.getID());
        String request = readOne(OPTION_PREFIX + "Accept this friend request? [YES/NO]");
        switch (request.toUpperCase().trim()) {
            case "YES":
                friendshipService.acceptFriendship(friendship);
                System.out.println("Friendship created!");
                break;
            case "NO":
                friendshipService.rejectFriendship(friendship);
                System.out.println("Friendship rejected!");
                break;
            default:
                throw new UIException("Please choose between YES or NO!");
        }
    }
}
