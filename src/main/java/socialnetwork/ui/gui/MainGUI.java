package socialnetwork.ui.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import socialnetwork.service.*;
import socialnetwork.ui.UI;

public class MainGUI extends Application implements UI {
    private static UserService userService;
    private static ChatService chatService;
    private static MessageService messageService;
    private static FriendshipService friendshipService;
    private static NotificationService notificationService;
    private static MainGUI mainGUI;

    public static MainGUI getInstance(UserService userService, ChatService chatService, MessageService messageService,
                                      FriendshipService friendshipService, NotificationService notificationService) {
        if (mainGUI == null) {
            mainGUI = new MainGUI();
            MainGUI.userService = userService;
            MainGUI.chatService = chatService;
            MainGUI.messageService = messageService;
            MainGUI.friendshipService = friendshipService;
            MainGUI.notificationService = notificationService;
        }
        return mainGUI;
    }

    public static UserService getUserService() {
        return userService;
    }

    public static ChatService getChatService() {
        return chatService;
    }

    public static MessageService getMessageService() {
        return messageService;
    }

    public static FriendshipService getFriendshipService() {
        return friendshipService;
    }

    public static NotificationService getNotificationService() {
        return notificationService;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/loginLayout.fxml"));
        BorderPane root = loader.load();

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    @Override
    public void run() {
        main(null);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
