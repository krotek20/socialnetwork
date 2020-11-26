package socialnetwork;

import socialnetwork.config.ApplicationContext;

import socialnetwork.domain.entities.*;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.*;

import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipDBRepository;
import socialnetwork.repository.database.UserDBRepository;

import socialnetwork.repository.file.*;
import socialnetwork.service.*;

import socialnetwork.ui.UI;
import socialnetwork.ui.tui.login.LoginTUI;

public class Main {
    public static void main(String[] args) {
        // Database handlers
//        String url = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.url");
//        String username = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.username");
//        String password = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.password");

        // Database repositories
//        Repository<Long, User> userDBRepository = new UserDBRepository(
//                url, username, password, new UserValidator());
//        Repository<Tuple<Long, Long>, Friendship> friendshipDBRepository = new FriendshipDBRepository(
//                url, username, password, new FriendshipValidator());

        // File repositories
        Repository<Long, User> userFileRepository = new UserFileRepository(
                new UserValidator());
        Repository<Tuple<Long, Long>, Friendship> friendshipFileRepository = new FriendshipFileRepository(
                new FriendshipValidator());
        Repository<Long, Chat> chatFileRepository = new ChatFileRepository(
                new ChatValidator());
        Repository<Chat, Message> messageFileRepository = new MessageFileRepository(
                new MessageValidator());
        Repository<User, Notification> notificationFileRepository = new NotificationFileRepository(
                new NotificationValidator());

        // Services
        UserService userService = new UserService(
                userFileRepository, friendshipFileRepository);
        FriendshipService friendshipService = new FriendshipService(
                friendshipFileRepository, userFileRepository, chatFileRepository, notificationFileRepository);
        MessageService messageService = new MessageService(
                messageFileRepository, userFileRepository, chatFileRepository, notificationFileRepository);
        NotificationService notificationService = new NotificationService(
                notificationFileRepository);
        ChatService chatService = new ChatService(
                chatFileRepository, userFileRepository);


        // UI
        UI loginUI = new LoginTUI(userService, friendshipService, messageService, notificationService, chatService);

        // RUN APPLICATION
        loginUI.run();
    }
}


