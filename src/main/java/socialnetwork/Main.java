package socialnetwork;

import socialnetwork.domain.entities.*;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.*;

import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;

import socialnetwork.service.*;

import socialnetwork.ui.UI;
import socialnetwork.ui.gui.MainGUI;

public class Main {
    public static void main(String[] args) {
        // Database repositories
        Repository<Long, User> userDBRepository = new UserDBRepository(new UserValidator());
        Repository<Tuple<Long, Long>, Friendship> friendshipDBRepository = new FriendshipDBRepository(new FriendshipValidator());
        Repository<Long, Chat> chatDBRepository = new ChatDBRepository(new ChatValidator());
        Repository<Long, Message> messageDBRepository = new MessageDBRepository(new MessageValidator());
        Repository<Long, Notification> notificationDBRepository = new NotificationDBRepository(new NotificationValidator());

        // Services
        UserService userService = new UserService(
                userDBRepository, friendshipDBRepository);
        FriendshipService friendshipService = new FriendshipService(
                friendshipDBRepository, userDBRepository, chatDBRepository);
        ChatService chatService = new ChatService(
                chatDBRepository, userDBRepository);
        MessageService messageService = new MessageService(
                messageDBRepository, chatDBRepository);
        NotificationService notificationService = new NotificationService(
                notificationDBRepository);

        // UI
        UI mainUI = MainGUI.getInstance(userService, chatService,
                messageService, friendshipService, notificationService);

        // RUN APPLICATION
        mainUI.run();
    }
}


