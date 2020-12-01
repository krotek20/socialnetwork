package socialnetwork;

import socialnetwork.config.ApplicationContext;

import socialnetwork.domain.entities.*;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.*;

import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;

import socialnetwork.service.*;

import socialnetwork.ui.UI;
import socialnetwork.ui.gui.MainGUI;
import socialnetwork.ui.tui.login.LoginTUI;

public class Main {
    public static void main(String[] args) {
        // Database handlers
        String url = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.url");
        String username = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.username");
        String password = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.password");

        // Database repositories
        Repository<Long, User> userDBRepository = new UserDBRepository(
                url, username, password, new UserValidator());
        Repository<Tuple<Long, Long>, Friendship> friendshipDBRepository = new FriendshipDBRepository(
                url, username, password, new FriendshipValidator());
        Repository<Long, Chat> chatDBRepository = new ChatDBRepository(
                url, username, password, new ChatValidator());
        Repository<Long, Message> messageDBRepository = new MessageDBRepository(
                url, username, password, new MessageValidator());
        Repository<Long, Notification> notificationDBRepository = new NotificationDBRepository(
                url, username, password, new NotificationValidator());

        // Services
        UserService userService = new UserService(
                userDBRepository, friendshipDBRepository);
        FriendshipService friendshipService = new FriendshipService(
                friendshipDBRepository, userDBRepository, chatDBRepository, notificationDBRepository);
        ChatService chatService = new ChatService(
                chatDBRepository, userDBRepository, notificationDBRepository);
        MessageService messageService = new MessageService(
                messageDBRepository, chatDBRepository, notificationDBRepository);
        NotificationService notificationService = new NotificationService(
                notificationDBRepository);

        // UI
        UI mainUI = MainGUI.getInstance(userService, chatService,
                messageService, friendshipService, notificationService);

        // RUN APPLICATION
        mainUI.run();
    }
}


