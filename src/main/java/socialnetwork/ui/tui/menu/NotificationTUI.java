package socialnetwork.ui.tui.menu;

import socialnetwork.domain.entities.Notification;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.NotificationService;
import socialnetwork.ui.tui.BaseTUI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationTUI extends BaseTUI {
    private final NotificationService notificationService;
    private final FriendshipService friendshipService;
    private final MessageService messageService;

    public NotificationTUI(NotificationService notificationService,
                           FriendshipService friendshipService,
                           MessageService messageService) {
        this.notificationService = notificationService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;

        if (loggedUser != null) {
            generateTUI("Notification TUI", new HashMap<String, Runnable>() {{
                put("New notifications", NotificationTUI.this::displayNotificationsSize);
                put("Display all notifications", NotificationTUI.this::displayAllNotifications);
            }});
        }
    }

    private void displayNotificationsSize() {
        List<Notification> notifications = notificationService.readAllNotifications(loggedUser);
        if (notifications.size() == 0) {
            System.out.println("You don't have any new notification");
        } else {
            System.out.println("You have " + notifications.size() + " new notification(s)");
        }
    }

    private void displayAllNotifications() {
        List<Notification> notifications = notificationService.readAllNotifications(loggedUser);
        Map<String, Notification> notificationsHelper = new HashMap<>();
        long count = 1;
        for (Notification notification : notifications) {
            notificationsHelper.put(String.valueOf(count), notification);
            count++;
        }
        for (Map.Entry<String, Notification> notification : notificationsHelper.entrySet()) {
            System.out.println(notification.getKey() + ". " + notification.getValue().getNotificationText());
        }
        ReplyNotificationTUI replyNotificationTUI = new ReplyNotificationTUI(
                notificationService, friendshipService, messageService, notificationsHelper);
        replyNotificationTUI.run();
    }
}
