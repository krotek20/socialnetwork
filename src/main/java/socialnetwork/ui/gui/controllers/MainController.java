package socialnetwork.ui.gui.controllers;

import com.itextpdf.text.DocumentException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import socialnetwork.Utils.Constants;
import socialnetwork.Utils.ExportPDF;
import socialnetwork.Utils.design.NotifyStatus;
import socialnetwork.Utils.design.Observer;
import socialnetwork.domain.dto.UserDTO;
import socialnetwork.domain.entities.*;
import socialnetwork.domain.enums.EventSubscription;
import socialnetwork.domain.enums.FriendshipStatus;
import socialnetwork.domain.enums.Gender;
import socialnetwork.domain.enums.NotificationType;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.RepositoryException;
import socialnetwork.service.*;
import socialnetwork.ui.gui.MainGUI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainController implements Observer {
    private UserService userService;
    private ChatService chatService;
    private EventService eventService;
    private MessageService messageService;
    private FriendshipService friendshipService;
    private NotificationService notificationService;

    private final ObservableList<User> usersModel = FXCollections.observableArrayList();
    private final ObservableList<Chat> chatsModel = FXCollections.observableArrayList();
    private final ObservableList<User> friendsModel = FXCollections.observableArrayList();
    private final ObservableList<Event> eventsModel = FXCollections.observableArrayList();
    private final ObservableList<Event> userEventsModel = FXCollections.observableArrayList();
    private final ObservableList<Message> messagesModel = FXCollections.observableArrayList();
    private final ObservableList<Notification> notificationsModel = FXCollections.observableArrayList();

    private Message lastSelectedMessage = null;
    private final int limit = 9;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab chatsTab;
    @FXML
    private TextField filterUsersTextField;
    @FXML
    private TextField filterFriendsTextField;
    @FXML
    private TextField eventHourTextField;
    @FXML
    private TextField eventMinuteTextField;
    @FXML
    private TextField eventSecondTextField;
    @FXML
    private TextField eventTitleTextField;
    @FXML
    private TextField messageTextField;
    @FXML
    private TextField editTitleTextField;
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
    private Button joinEventButton;
    @FXML
    private Button subscribeButton;
    @FXML
    private Button unsubscribeButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button openFriendChatButton;
    @FXML
    private Button openChatButton;
    @FXML
    private Button activeChat;
    @FXML
    private Button editChatTitleButton;
    @FXML
    private ListView<Chat> chatList;
    @FXML
    private ListView<Event> eventsListView;
    @FXML
    private ListView<Event> userEventsListView;
    @FXML
    private ListView<Message> messagesListView;
    @FXML
    private ListView<Notification> notificationList;
    @FXML
    private ImageView friendReqImage;
    @FXML
    private DatePicker fromDate;
    @FXML
    private DatePicker toDate;
    @FXML
    private DatePicker eventDatePicker;
    @FXML
    private Circle circledImage;
    @FXML
    private Label loggedUserName;
    @FXML
    private Label infoLabel;
    @FXML
    private Pagination pagination;

    public void setServices() {
        this.userService = MainGUI.getUserService();
        this.chatService = MainGUI.getChatService();
        this.eventService = MainGUI.getEventService();
        this.messageService = MainGUI.getMessageService();
        this.friendshipService = MainGUI.getFriendshipService();
        this.notificationService = MainGUI.getNotificationService();

        userService.register(this);
        chatService.register(this);
        eventService.register(this);
        messageService.register(this);
        friendshipService.register(this);
        notificationService.register(this);
    }

    @FXML
    public void initialize() {
        // setting service instances with main values
        setServices();

        // setting environment buttons as not visible
        setButtonsOff();

        // initially no chat is selected
        activeChat.setText("");

        // setting only-numeric property on numeric textFields
        setOnlyNumericTextFields();

        // setting tab headers width to the maximum value
        tabPane.widthProperty().addListener((observable, oldValue, newValue) ->
        {
            tabPane.setTabMinWidth(tabPane.getWidth() / tabPane.getTabs().size());
            tabPane.setTabMaxWidth(tabPane.getWidth() / tabPane.getTabs().size());
        });

        // setting up friendReqButton image according to currently logged in user gender.
        Image image = new Image(getClass().getResourceAsStream((
                LoginController.loggedUser.getGender() == Gender.MALE ?
                        "/images/male_fr.png" : "/images/female_fr.png")));
        friendReqImage.setImage(image);

        // connected user data.
        displayUserContent();

        // setting users and friends table headers.
        userTableColumnName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        userTableColumnSurname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        friendsTableColumnName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        friendsTableColumnSurname.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        // default resize policy (disabling horizontal scroll-bar).
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        friendsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // setting up content in tables and lists.
        chatList.setItems(chatsModel);
        friendsTable.setItems(friendsModel);
        eventsListView.setItems(eventsModel);
        messagesListView.setItems(messagesModel);
        notificationList.setItems(notificationsModel);
        userEventsListView.setItems(userEventsModel);

        // setting up placeholders for tables and lists.
        chatList.setPlaceholder(new Label("No chats created yet!"));
        friendsTable.setPlaceholder(new Label("No one here yet!"));
        eventsListView.setPlaceholder(new Label("No events yet!"));
        messagesListView.setPlaceholder(new Label("Start a new conversation!"));
        notificationList.setPlaceholder(new Label("You don't have any new notifications!"));
        userEventsListView.setPlaceholder(new Label("You didn't join any event!"));

        // initializing the models.
        initModel();
    }

    private void setButtonsOff() {
        acceptButton.setVisible(false);
        rejectButton.setVisible(false);
        openChatButton.setVisible(false);
        addFriendButton.setVisible(false);
        joinEventButton.setVisible(false);
        subscribeButton.setVisible(false);
        unsubscribeButton.setVisible(false);
        deleteFriendButton.setVisible(false);
        editTitleTextField.setVisible(false);
        editChatTitleButton.setVisible(false);
        openFriendChatButton.setVisible(false);
    }

    private void setOnlyNumericTextFields() {
        eventHourTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                eventHourTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        eventMinuteTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                eventMinuteTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        eventSecondTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                eventSecondTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void displayUserContent() {
        // setting up avatar image with circle effects.
        circledImage.setStroke(Color.BLACK);
        Image avatar = new Image("/images/default_avatar.jpg", false);
        circledImage.setFill(new ImagePattern(avatar));
        circledImage.setEffect(new DropShadow(+10d, 0d, +2d, Color.BLACK));

        // connected user firstname and lastname display.
        UserDTO userDTO = userService.convertToUserDTO(LoginController.loggedUser);
        loggedUserName.setText(userDTO != null ? userDTO.toString() : "");
        loggedUserName.setMaxWidth(Double.MAX_VALUE);
        AnchorPane.setLeftAnchor(loggedUserName, 0.0);
        AnchorPane.setRightAnchor(loggedUserName, 0.0);
        loggedUserName.setAlignment(Pos.CENTER);

        // connected user info display.
        if (userDTO != null) userDTO.setInfo(true);
        infoLabel.setText(userDTO != null ? userDTO.toString() : "");
        infoLabel.setMaxWidth(Double.MAX_VALUE);
        AnchorPane.setLeftAnchor(infoLabel, 0.0);
        AnchorPane.setRightAnchor(infoLabel, 0.0);
        infoLabel.setAlignment(Pos.CENTER);
    }

    private Node createPage(int pageIndex) {
        int offset = pageIndex * limit;
        usersModel.setAll(StreamSupport
                .stream(userService.findUsersPage(limit, offset).spliterator(), false)
                .collect(Collectors.toList()));
        usersTable.setItems(usersModel);
        openChatButton.setVisible(false);
        addFriendButton.setVisible(false);
        return usersTable;
    }

    private void initModel() {
        initAccountTab();
        initNotificationsModel();
        initEventsModel();
        initChatsModel();
    }

    private void initAccountTab() {
        pagination.setPageCount((int) (StreamSupport
                .stream(userService.findAllUsers().spliterator(), false)
                .count() / limit + 1));
        pagination.setPageFactory(this::createPage);

        UserDTO userDTO = userService.convertToUserDTO(LoginController.loggedUser);
        List<User> friends = userDTO.getFriends();

        friendsModel.setAll(friends);
    }

    private void initNotificationsModel() {
        List<Notification> notificationList = notificationService.readAllNotifications(LoginController.loggedUser);
        notificationsModel.setAll(notificationList);
    }

    private void initEventsModel() {
        List<Event> eventList = eventService.readAllEvents();
        List<Event> userEventList = eventService.readAllUserEvents(LoginController.loggedUser);
        eventsModel.setAll(eventList);
        userEventsModel.setAll(userEventList);
    }

    private void initChatsModel() {
        List<Chat> chatsList = chatService.readAllChats(LoginController.loggedUser);
        chatsModel.setAll(chatsList);
    }

    private void initChatLog() {
        Chat chat = chatList.getSelectionModel().getSelectedItem();
        if (chat != null) {
            List<Message> messagesList = messageService.readAllMessagesFromChat(
                    LoginController.loggedUser, chat.getID().toString());
            messagesModel.setAll(messagesList);

            messagesListView.setCellFactory(cell -> new ListCell<Message>() {
                final Tooltip tooltip = new Tooltip();

                @Override
                protected void updateItem(Message message, boolean empty) {
                    super.updateItem(message, empty);
                    if (message == null || empty) {
                        setText(null);
                        setTooltip(null);
                        setGraphic(null);
                    } else {
                        // set the width's
                        setMinWidth(cell.getWidth());
                        setMaxWidth(cell.getWidth());
                        setPrefWidth(cell.getWidth());

                        // allow wrapping
                        setWrapText(true);

                        setText(message.toString());
                        String reply = "";
                        if (!message.getReply().equals("null")) {
                            reply = " REPLY TO: " + (message.getReply().length() > 30 ?
                                    (message.getReply().substring(0, 25) + "...") : message.getReply());

                        }
                        tooltip.setText(message.getTimestamp().format(Constants.DATE_TIME_FORMATTER) + (
                                message.getReply().equals("null") ? "" : reply
                        ));
                        setTooltip(tooltip);
                    }
                }
            });
            messagesListView.scrollTo((messagesListView.getItems().size() - 1));
        }
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
        } else if (status == NotifyStatus.EVENT) {
            Platform.runLater(this::initEventsModel);
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
        List<User> userList = StreamSupport
                .stream(userService.findAllUsers().spliterator(), false)
                .filter(x -> (x.getFirstName() + " " + x.getLastName()).toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList());
        usersModel.setAll(userList);
    }

    public void filterFriends(KeyEvent keyEvent) {
        String filter = filterFriendsTextField.getText();

        UserDTO userDTO = userService.convertToUserDTO(LoginController.loggedUser);
        List<User> friends = userDTO.getFriends()
                .stream()
                .filter(x -> (x.getFirstName() + " " + x.getLastName()).toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList());

        friendsModel.setAll(friends);
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
        openChatButton.setVisible(false);
        User user = usersTable.getSelectionModel().getSelectedItem();
        if (user != null) {
            String id1 = user.getID().toString();
            String id2 = LoginController.loggedUser.getID().toString();
            Map<String, String> friendshipMap = new HashMap<>();
            friendshipMap.put("id1", id1);
            friendshipMap.put("id2", id2);
            Friendship friendship = friendshipService.findOneFriendship(friendshipMap);
            if (!user.getID().equals(LoginController.loggedUser.getID())
                    && (friendship == null || friendship.getStatus() == FriendshipStatus.REJECTED)) {
                addFriendButton.setVisible(true);
            }
            openChatButton.setVisible(true);
        }
    }

    public void showFriendListButtons(MouseEvent mouseEvent) {
        deleteFriendButton.setVisible(false);
        openFriendChatButton.setVisible(false);
        User friend = friendsTable.getSelectionModel().getSelectedItem();
        if (friend != null) {
            deleteFriendButton.setVisible(true);
            openFriendChatButton.setVisible(true);
        }
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
        deleteFriendButton.setVisible(false);
        openFriendChatButton.setVisible(false);
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

    private boolean verifyDatePickers() {
        try {
            String from = fromDate.getValue().toString();
            String to = toDate.getValue().toString();
        } catch (NullPointerException e) {
            AlertBox.showErrorMessage(null, "Please select the desired period!");
            return false;
        }
        if (fromDate.getValue().compareTo(toDate.getValue()) > 0) {
            AlertBox.showErrorMessage(null, "Please select a valid period!");
            return false;
        }
        return true;
    }

    private void writeFriendships(List<Friendship> friendships, ExportPDF exportPDF) {
        for (Friendship friendship : friendships) {
            exportPDF.addRowToTable("FRIENDSHIP",
                    (friendship.getID().getLeft().equals(LoginController.loggedUser.getID()) ?
                            userService.findOneUser(friendship.getID().getRight().toString()).toString() :
                            userService.findOneUser(friendship.getID().getLeft().toString()).toString()),
                    friendship.getDate().format(Constants.DATE_TIME_FORMATTER));
        }
    }

    private void writeMessages(List<Message> messages, ExportPDF exportPDF) {
        for (Message message : messages) {
            exportPDF.addRowToTable("MESSAGE", message.getFrom() + ": " + message.getMessageText(),
                    message.getTimestamp().format(Constants.DATE_TIME_FORMATTER));
        }
    }

    public void handleExportActivity(MouseEvent mouseEvent) {
        if (verifyDatePickers()) {
            ExportPDF exportPDF = new ExportPDF("src/main/resources/reports/report_activity.pdf");

            // Find friendships
            List<Friendship> friendships = userService.findFriendships(LoginController.loggedUser.getID())
                    .stream()
                    .filter(x -> x.getStatus().equals(FriendshipStatus.APPROVED))
                    .filter(x -> x.getDate().toLocalDate().compareTo(fromDate.getValue()) >= 0 &&
                            x.getDate().toLocalDate().compareTo(toDate.getValue()) <= 0)
                    .collect(Collectors.toList());

            // Write friendships
            writeFriendships(friendships, exportPDF);

            // Find messages
            List<Message> messages = messageService.readAllMessages(LoginController.loggedUser)
                    .stream()
                    .filter(x -> x.getTimestamp().toLocalDate().compareTo(fromDate.getValue()) >= 0 &&
                            x.getTimestamp().toLocalDate().compareTo(toDate.getValue()) <= 0)
                    .collect(Collectors.toList());

            // Write messages
            writeMessages(messages, exportPDF);

            try {
                exportPDF.exportTable();
                AlertBox.showMessage(null, Alert.AlertType.CONFIRMATION,
                        "SUCCESS", "PDF successfully exported!");
            } catch (DocumentException | FileNotFoundException e) {
                AlertBox.showErrorMessage(null, "Export ERROR!");
            }
        }
    }

    public void handleExportPrivate(MouseEvent mouseEvent) {
        User friend = friendsTable.getSelectionModel().getSelectedItem();
        if (friend == null) {
            AlertBox.showErrorMessage(null, "Please select a friend from the friends list!");
        }
        if (verifyDatePickers() && friend != null) {
            ExportPDF exportPDF = new ExportPDF("src/main/resources/reports/report_private.pdf");

            Chat chat = chatService.findPrivateChat(LoginController.loggedUser, friend);
            List<Message> messages = messageService.readAllMessagesFromChat(
                    LoginController.loggedUser, chat.getID().toString());

            writeMessages(messages, exportPDF);

            try {
                exportPDF.exportTable();
                AlertBox.showMessage(null, Alert.AlertType.CONFIRMATION,
                        "SUCCESS", "PDF successfully exported!");
            } catch (DocumentException | FileNotFoundException e) {
                AlertBox.showErrorMessage(null, "Export ERROR!");
            }
        }
    }

    // Notifications & Events tab functionalities
    public void handleNotificationsClick(MouseEvent mouseEvent) {
        Notification notification = notificationList.getSelectionModel().getSelectedItem();
        if (notification != null) {
            if (notification.getType() == NotificationType.FRIENDSHIP) {
                acceptButton.setVisible(true);
                rejectButton.setVisible(true);
            } else {
                acceptButton.setVisible(false);
                rejectButton.setVisible(false);
            }
            if (mouseEvent.getClickCount() == 2) {
                AlertBox.showMessage(null, Alert.AlertType.INFORMATION, notification.getType() + " " +
                                notification.getTimestamp().format(Constants.DATE_TIME_FORMATTER),
                        notification.getEntityText());
                if (notification.getType() != NotificationType.FRIENDSHIP) {
                    notification.setUserNotificationStatus(LoginController.loggedUser);
                    NotificationService.updateSeenNotification(notification);
                    update(null);
                }
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

    public void handleCreateEvent(MouseEvent mouseEvent) {
        int hour, minute, second;
        try {
            hour = Integer.parseInt(eventHourTextField.getText());
            minute = Integer.parseInt(eventMinuteTextField.getText());
            second = Integer.parseInt(eventSecondTextField.getText());
        } catch (NumberFormatException e) {
            AlertBox.showErrorMessage(null, "Please make sure you have inserted HH:MM:SS format!");
            return;
        }

        try {
            String date = eventDatePicker.getValue().toString();
        } catch (NullPointerException e) {
            AlertBox.showErrorMessage(null, "Please select the desired date!");
            return;
        }
        if (hour < 0 || hour > 23) {
            AlertBox.showErrorMessage(null, "Please insert a valid hour!");
            return;
        }
        if (minute < 0 || minute > 59) {
            AlertBox.showErrorMessage(null, "Please insert a valid minute!");
            return;
        }
        if (second < 0 || second > 59) {
            AlertBox.showErrorMessage(null, "Please insert a valid second!");
            return;
        }
        if (eventTitleTextField.getText() == null
                || eventTitleTextField.getText().equals("")) {
            AlertBox.showErrorMessage(null, "Please insert a valid title!");
            return;
        }
        LocalDateTime startAt = eventDatePicker.getValue().atTime(hour, minute, second);
        if (startAt.compareTo(LocalDateTime.now()) < 0) {
            AlertBox.showErrorMessage(null, "Please select a valid date!");
            return;
        }

        if (eventService.createEvent(eventTitleTextField.getText(), startAt, LoginController.loggedUser) != null) {
            AlertBox.showMessage(null, Alert.AlertType.CONFIRMATION, "Success!", "Event created!");
            eventHourTextField.setText("");
            eventMinuteTextField.setText("");
            eventSecondTextField.setText("");
            eventTitleTextField.setText("");
        }
    }

    public void handleJoinEvent(MouseEvent mouseEvent) {
        Event event = eventsListView.getSelectionModel().getSelectedItem();
        if (eventService.addUserToEvent(event, LoginController.loggedUser) != null) {
            AlertBox.showMessage(null, Alert.AlertType.CONFIRMATION,
                    "Successful!", "You have successfully joined to this event!");
            joinEventButton.setVisible(false);
        } else {
            AlertBox.showErrorMessage(null, "Something went wrong!");
        }
    }

    public void handleEventListSelection(MouseEvent mouseEvent) {
        Event event = eventsListView.getSelectionModel().getSelectedItem();
        joinEventButton.setVisible(event != null);
        for (Event userEvent : eventService.readAllUserEvents(LoginController.loggedUser)) {
            if (event != null && event.getID().equals(userEvent.getID())) {
                joinEventButton.setVisible(false);
                return;
            }
        }
    }

    public void handleEventSubscription(MouseEvent mouseEvent) {
        Event event = userEventsListView.getSelectionModel().getSelectedItem();
        if (event == null) {
            subscribeButton.setVisible(false);
            unsubscribeButton.setVisible(false);
            return;
        }
        for (Map.Entry<User, EventSubscription> entry : event.getUsers().entrySet()) {
            if (entry.getKey().getID().equals(LoginController.loggedUser.getID())) {
                subscribeButton.setVisible(entry.getValue() != EventSubscription.SUBSCRIBE);
                unsubscribeButton.setVisible(entry.getValue() != EventSubscription.UNSUBSCRIBE);
                return;
            }
        }
    }

    public void handleSubscribeButton(MouseEvent mouseEvent) {
        Event event = userEventsListView.getSelectionModel().getSelectedItem();
        if (event != null) {
            eventService.changeUserSubscription(event,
                    LoginController.loggedUser, EventSubscription.SUBSCRIBE);
            AlertBox.showMessage(null, Alert.AlertType.CONFIRMATION,
                    "Subscribe!", "You have successfully subscribed to this event!");
            subscribeButton.setVisible(false);
        }
    }

    public void handleUnsubscribeButton(MouseEvent mouseEvent) {
        Event event = userEventsListView.getSelectionModel().getSelectedItem();
        if (event != null) {
            eventService.changeUserSubscription(event,
                    LoginController.loggedUser, EventSubscription.UNSUBSCRIBE);
            AlertBox.showMessage(null, Alert.AlertType.CONFIRMATION,
                    "Unsubscribe!", "You have successfully unsubscribed to this event!");
            unsubscribeButton.setVisible(false);
        }
    }

    // Chats tab functionalities
    public void displayChatName(MouseEvent mouseEvent) {
        Chat selectedChat = chatList.getSelectionModel().getSelectedItem();
        if (selectedChat != null) {
            activeChat.setText(selectedChat.toString());
            editChatTitleButton.setVisible(selectedChat.getChatSize() > 2);
            initChatLog();
        }
    }

    public void handleEditTitle(MouseEvent mouseEvent) {
        Chat selectedChat = chatList.getSelectionModel().getSelectedItem();
        if (selectedChat != null) {
            activeChat.setDisable(false);
            editChatTitleButton.setVisible(false);
            editTitleTextField.setVisible(true);

            editTitleTextField.setText(activeChat.getText());
            activeChat.setText("");
            activeChat.setGraphic(editTitleTextField);
        }
    }

    public void handleSubmitTitleChanges(KeyEvent keyEvent) {
        Chat selectedChat = chatList.getSelectionModel().getSelectedItem();
        if (selectedChat != null && !editTitleTextField.getText().trim().equals("")
                && keyEvent.getCode().equals(KeyCode.ENTER)) {
            selectedChat.setTitle(editTitleTextField.getText().trim());
            chatService.updateChat(selectedChat);
            activeChat.setGraphic(null);

            activeChat.setDisable(true);
            editChatTitleButton.setVisible(true);
            editTitleTextField.setVisible(false);

            chatList.getSelectionModel().select(selectedChat);
            displayChatName(null);
        }
    }

    public void handleSendMessage(MouseEvent mouseEvent) {
        Chat selectedChat = chatList.getSelectionModel().getSelectedItem();
        Message selectedReply = messagesListView.getSelectionModel().getSelectedItem();
        if (!messageTextField.getText().trim().equals("")) {
            if (selectedReply == null) {
                messageService.sendMessage(LoginController.loggedUser, new HashMap<String, String>() {{
                    put("to", selectedChat.getID().toString());
                    put("message", messageTextField.getText());
                    put("reply", null);
                }});
            } else {
                messageService.sendMessage(LoginController.loggedUser, new HashMap<String, String>() {{
                    put("to", selectedChat.getID().toString());
                    put("message", messageTextField.getText());
                    put("reply", selectedReply.getMessageText());
                }});
            }
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

    public void handleMessageSelection(MouseEvent mouseEvent) {
        if (messagesListView.getSelectionModel().getSelectedItem() == lastSelectedMessage) {
            messagesListView.getSelectionModel().select(null);
        }
        lastSelectedMessage = messagesListView.getSelectionModel().getSelectedItem();
    }
}
