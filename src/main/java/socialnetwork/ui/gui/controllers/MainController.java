package socialnetwork.ui.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import socialnetwork.Utils.Constants;
import socialnetwork.Utils.design.NotifyStatus;
import socialnetwork.Utils.design.Observer;
import socialnetwork.domain.entities.*;
import socialnetwork.domain.enums.FriendshipStatus;
import socialnetwork.domain.enums.Gender;
import socialnetwork.domain.enums.NotificationType;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.RepositoryException;
import socialnetwork.service.*;
import socialnetwork.ui.gui.MainGUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainController implements Observer {
    private UserService userService;
    private ChatService chatService;
    private MessageService messageService;
    private FriendshipService friendshipService;
    private NotificationService notificationService;

    private final ObservableList<User> usersModel = FXCollections.observableArrayList();
    private final ObservableList<Chat> chatsModel = FXCollections.observableArrayList();
    private final ObservableList<User> friendsModel = FXCollections.observableArrayList();
    private final ObservableList<Notification> notificationsModel = FXCollections.observableArrayList();

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab chatsTab;
    @FXML
    private TextField filterUsersTextField;
    @FXML
    private TextField filterFriendsTextField;
    @FXML
    private TextField messageTextField;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> userTableColumnName;
    @FXML
    private TableColumn<User, String> userTableColumnSurname;
    @FXML
    private TableView<User> friendsTable;
    @FXML
    private TableColumn<User, String> friendsTableColumnName;
    @FXML
    private TableColumn<User, String> friendsTableColumnSurname;
    @FXML
    private Button addFriendButton;
    @FXML
    private Button deleteFriendButton;
    @FXML
    private Button acceptButton;
    @FXML
    private Button rejectButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button openFriendChatButton;
    @FXML
    private Button openChatButton;
    @FXML
    private Button activeChat;
    @FXML
    private Button sendMessageButton;
    @FXML
    private ListView<Notification> notificationList;
    @FXML
    private ListView<Chat> chatList;
    @FXML
    private TextArea messagesTextArea;
    @FXML
    private Button createGroupChatButton;
    @FXML
    private ImageView friendReqImage;

    public void setServices() {
        this.userService = MainGUI.getUserService();
        this.chatService = MainGUI.getChatService();
        this.messageService = MainGUI.getMessageService();
        this.friendshipService = MainGUI.getFriendshipService();
        this.notificationService = MainGUI.getNotificationService();

        userService.register(this);
        chatService.register(this);
        messageService.register(this);
        friendshipService.register(this);
        notificationService.register(this);

        initModel();
    }

    @FXML
    public void initialize() {
        acceptButton.setVisible(false);
        rejectButton.setVisible(false);
        addFriendButton.setVisible(false);
        deleteFriendButton.setVisible(false);
        openChatButton.setVisible(false);
        openFriendChatButton.setVisible(false);
        activeChat.setText("");

        tabPane.widthProperty().addListener((observable, oldValue, newValue) ->
        {
            tabPane.setTabMinWidth(tabPane.getWidth() / tabPane.getTabs().size());
            tabPane.setTabMaxWidth(tabPane.getWidth() / tabPane.getTabs().size());
        });

        Image image = new Image(getClass().getResourceAsStream((
                LoginController.loggedUser.getGender() == Gender.MALE ?
                        "/images/male_fr.png" : "/images/female_fr.png")));
        friendReqImage.setImage(image);

        userTableColumnName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        userTableColumnSurname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        friendsTableColumnName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        friendsTableColumnSurname.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        chatList.setItems(chatsModel);
        usersTable.setItems(usersModel);
        friendsTable.setItems(friendsModel);
        notificationList.setItems(notificationsModel);
    }

    private void initModel() {
        initAccountTab();
        initNotificationsModel();
        initChatsModel();
    }

    private void initAccountTab() {
        List<User> userList = StreamSupport
                .stream(userService.findAllUsers().spliterator(), false)
                .collect(Collectors.toList());

        List<Friendship> friendships = userService.findFriendships(LoginController.loggedUser.getID());
        List<User> friendList = friendships
                .stream()
                .filter(x -> x.getStatus().equals(FriendshipStatus.APPROVED))
                .map(x -> (x.getID().getLeft().equals(LoginController.loggedUser.getID()) ?
                        userService.findOneUser(x.getID().getRight().toString()) :
                        userService.findOneUser(x.getID().getLeft().toString())))
                .collect(Collectors.toList());

        userList.removeIf(friendList::contains);

        usersModel.setAll(userList);
        friendsModel.setAll(friendList);
    }

    private void initChatLog() {
        Chat chat = chatList.getSelectionModel().getSelectedItem();
        if (chat != null) {
            List<String> messagesList = messageService.readAllMessages(
                    LoginController.loggedUser, chat.getID().toString());
            StringBuilder builder = new StringBuilder();
            for (String message : messagesList) {
                builder.append("\n").append(message);
            }
            messagesTextArea.setText(builder.toString());
        }
    }

    private void initNotificationsModel() {
        List<Notification> notificationList = notificationService.readAllNotifications(LoginController.loggedUser);
        notificationsModel.setAll(notificationList);
    }

    private void initChatsModel() {
        List<Chat> chatsList = chatService.readAllChats(LoginController.loggedUser);
        chatsModel.setAll(chatsList);
    }

    @Override
    public void update(NotifyStatus status) {
        if (status == NotifyStatus.MESSAGE) {
            initChatLog();
        } else if (status == NotifyStatus.CHAT) {
            initChatsModel();
        } else if (status == NotifyStatus.FRIEND_REQUEST || status == NotifyStatus.CREATE_USER) {
            initAccountTab();
            initChatsModel();
        }
        initNotificationsModel();
    }

    // Account tab functionalities
    public void handleAddFriend(MouseEvent mouseEvent) {
        User user = usersTable.getSelectionModel().getSelectedItem();
        if (user == null) {
            AlertBox.showErrorMessage(null, "No user selected!");
        } else {
            String id1 = user.getID().toString();
            String id2 = LoginController.loggedUser.getID().toString();
            Map<String, String> friendshipMap = new HashMap<>();
            friendshipMap.put("id1", id1);
            friendshipMap.put("id2", id2);
            try {
                friendshipService.requestFriendship(friendshipMap);
                AlertBox.showMessage(null, Alert.AlertType.CONFIRMATION,
                        "Request sent!", "Your friend request has been sent.");
            } catch (RepositoryException | ValidationException e) {
                AlertBox.showErrorMessage(null, e.getMessage());
            }
        }
        addFriendButton.setVisible(false);
    }

    public void filterUsers(KeyEvent keyEvent) {
        String filter = filterUsersTextField.getText();
        System.out.println(filter);
        List<User> userList = StreamSupport
                .stream(userService.findAllUsers().spliterator(), false)
                .filter(x -> (x.getFirstName() + " " + x.getLastName()).toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList());
        userList.removeIf(friendsModel::contains);
        usersModel.setAll(userList);
    }

    public void filterFriends(KeyEvent keyEvent) {
        String filter = filterFriendsTextField.getText();
        List<Friendship> friendships = userService.findFriendships(LoginController.loggedUser.getID());
        List<Friendship> acceptedFriendships = friendships
                .stream()
                .filter(x -> x.getStatus().equals(FriendshipStatus.APPROVED))
                .collect(Collectors.toList());
        List<User> friendList = acceptedFriendships
                .stream()
                .map(x -> (x.getID().getLeft().equals(LoginController.loggedUser.getID()) ?
                        userService.findOneUser(x.getID().getRight().toString()) :
                        userService.findOneUser(x.getID().getLeft().toString())))
                .filter(x -> (x.getFirstName() + " " + x.getLastName()).toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList());
        friendsModel.setAll(friendList);
    }

    public void handleRemoveFriendButton(MouseEvent mouseEvent) {
        User friend = friendsTable.getSelectionModel().getSelectedItem();
        if (friend == null) {
            AlertBox.showErrorMessage(null, "No selected friend!");
        } else {
            String id2 = friend.getID().toString();
            String id1 = LoginController.loggedUser.getID().toString();

            Map<String, String> friendshipMap = new HashMap<>();
            friendshipMap.put("id1", id1);
            friendshipMap.put("id2", id2);
            if (friendshipService.deleteFriendship(friendshipMap)) {
                AlertBox.showMessage(null, Alert.AlertType.CONFIRMATION, "Friend removed!",
                        friend.getLastName() + " " + friend.getFirstName() +
                                " has been removed from your friend list.");
            } else {
                AlertBox.showErrorMessage(null, "An error occurred.");
            }
        }
        deleteFriendButton.setVisible(false);
        openFriendChatButton.setVisible(false);
    }

    public void handleLogoutButton(MouseEvent mouseEvent) throws IOException {
        LoginController.loggedUser = null;
        Stage mainStage = (Stage) logoutButton.getScene().getWindow();
        mainStage.close();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/loginLayout.fxml"));
        BorderPane root = loader.load();

        Stage loginStage = new Stage();
        loginStage.setScene(new Scene(root));
        loginStage.setTitle("Login");
        loginStage.show();
    }

    public void handleFriendRequests(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/friendRequestsLayout.fxml"));
        AnchorPane root = loader.load();

        Stage friendRequestsStage = new Stage();
        friendRequestsStage.setTitle("Friend requests");

        Scene friendRequestsScene = new Scene(root);
        friendRequestsStage.setScene(friendRequestsScene);
        friendRequestsStage.show();
    }

    public void showUsersButtons(MouseEvent mouseEvent) {
        addFriendButton.setVisible(false);
        openChatButton.setVisible(true);
        User user = usersTable.getSelectionModel().getSelectedItem();
        if (user == null) {
            AlertBox.showErrorMessage(null, "No user selected!");
        } else {
            String id1 = user.getID().toString();
            String id2 = LoginController.loggedUser.getID().toString();
            Map<String, String> friendshipMap = new HashMap<>();
            friendshipMap.put("id1", id1);
            friendshipMap.put("id2", id2);
            Friendship friendship = friendshipService.findOneFriendship(friendshipMap);
            if (friendship == null) {
                addFriendButton.setVisible(true);
            }
        }
    }

    public void showFriendlistButtons(MouseEvent mouseEvent) {
        deleteFriendButton.setVisible(true);
        openFriendChatButton.setVisible(true);
    }

    public void handleOpenChatFriends(MouseEvent mouseEvent) {
        User friend = friendsTable.getSelectionModel().getSelectedItem();
        tabPane.getSelectionModel().select(chatsTab);
        List<Chat> chats = chatService.readAllChats(LoginController.loggedUser);
        for (Chat chat : chats) {
            List<User> users = new ArrayList<>(chat.getUsers());
            if (chat.getChatSize() == 2 && (users.get(0).getID().equals(friend.getID())
                    || users.get(1).getID().equals(friend.getID()))) {
                chatList.getSelectionModel().select(chat);
                displayChatName(null);
                break;
            }
        }
    }

    public void handleOpenChatUsers(MouseEvent mouseEvent) {
        User user = usersTable.getSelectionModel().getSelectedItem();
        tabPane.getSelectionModel().select(chatsTab);
        List<Chat> chats = chatService.readAllChats(LoginController.loggedUser);
        for (Chat chat : chats) {
            List<User> users = new ArrayList<>(chat.getUsers());
            if (chat.getChatSize() == 2 && (users.get(0).getID().equals(user.getID())
                    || users.get(1).getID().equals(user.getID()))) {
                chatList.getSelectionModel().select(chat);
                displayChatName(null);
                return;
            }
        }
        Chat chat = chatService.createChat("Private chat", new HashMap<String, String>() {{
            put("id1", LoginController.loggedUser.getID().toString());
            put("id2", user.getID().toString());
        }}, LoginController.loggedUser);
        chatList.getSelectionModel().select(chat);
        displayChatName(null);
    }

    // Notifications tab functionalities
    public void handleNotificationsClick(MouseEvent mouseEvent) {
        Notification notification = notificationList.getSelectionModel().getSelectedItem();
        if (notification != null) {
            if (notification.getType() == NotificationType.FRIENDSHIP) {
                acceptButton.setVisible(true);
                rejectButton.setVisible(true);
            }
            if (mouseEvent.getClickCount() == 2) {
                AlertBox.showMessage(null, Alert.AlertType.INFORMATION, notification.getType() + " " +
                                notification.getTimestamp().format(Constants.DATE_TIME_FORMATTER),
                        notification.getEntityText());
            }
        }
    }

    public void handleAcceptFriendRequest(MouseEvent mouseEvent) {
        Notification notification = notificationList.getSelectionModel().getSelectedItem();

        notification.setUserNotificationStatus(LoginController.loggedUser);
        NotificationService.updateSeenNotification(notification);

        Friendship friendship = friendshipService.readFriendshipByNotification(notification.getID());
        friendshipService.acceptFriendship(friendship);

        AlertBox.showMessage(null, Alert.AlertType.CONFIRMATION, "Friend request!", "Friend added!");

        acceptButton.setVisible(false);
        rejectButton.setVisible(false);
    }

    public void handleRejectFriendRequest(MouseEvent mouseEvent) {
        Notification notification = notificationList.getSelectionModel().getSelectedItem();

        notification.setUserNotificationStatus(LoginController.loggedUser);
        NotificationService.updateSeenNotification(notification);

        Friendship friendship = friendshipService.readFriendshipByNotification(notification.getID());
        friendshipService.rejectFriendship(friendship);

        AlertBox.showMessage(null, Alert.AlertType.CONFIRMATION,
                "Friend request!", "Friendship rejected!");

        acceptButton.setVisible(false);
        rejectButton.setVisible(false);
    }

    // Chats tab functionalities
    public void displayChatName(MouseEvent mouseEvent) {
        Chat selectedChat = chatList.getSelectionModel().getSelectedItem();
        if (selectedChat != null) {
            activeChat.setText(selectedChat.toString());
            initChatLog();
            chatList.getSelectionModel().select(selectedChat);
        }
    }

    public void handleSendMessage(MouseEvent mouseEvent) {
        Chat selectedChat = chatList.getSelectionModel().getSelectedItem();
        if (!messageTextField.getText().trim().equals("")) {
            messageService.sendMessage(LoginController.loggedUser, new HashMap<String, String>() {{
                put("to", selectedChat.getID().toString());
                put("message", messageTextField.getText());
                put("reply", null);
            }});
            messageTextField.setText("");
        }
        chatList.getSelectionModel().select(selectedChat);
    }

    public void handleCreateGroupChat(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/groupChatLayout.fxml"));
        AnchorPane root = loader.load();

        Stage groupChatStage = new Stage();
        groupChatStage.setTitle("Create Group Chat");


        Scene groupChatScene = new Scene(root);
        groupChatStage.setScene(groupChatScene);
        groupChatStage.show();
    }
}
