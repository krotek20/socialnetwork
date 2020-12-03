package socialnetwork.ui.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import socialnetwork.Utils.Constants;
import socialnetwork.Utils.design.Observable;
import socialnetwork.Utils.design.Observer;
import socialnetwork.domain.entities.Friendship;
import socialnetwork.domain.entities.Notification;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.enums.FriendshipStatus;
import socialnetwork.repository.RepositoryException;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.NotificationService;
import socialnetwork.service.UserService;
import socialnetwork.ui.gui.MainGUI;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainController implements Observer {
    private UserService userService;
    private FriendshipService friendshipService;
    private NotificationService notificationService;

    private final ObservableList<User> usersModel = FXCollections.observableArrayList();
    private final ObservableList<User> friendsModel = FXCollections.observableArrayList();
    private final ObservableList<Notification> notificationsModel = FXCollections.observableArrayList();

    @FXML
    private TextField filterUsersTextField;
    @FXML
    private TextField filterFriendsTextField;
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
    private ListView<Notification> notificationList;

    public void setServices() {
        this.userService = MainGUI.getUserService();
        this.friendshipService = MainGUI.getFriendshipService();
        this.notificationService = MainGUI.getNotificationService();

        userService.register(this);
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

        userTableColumnName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        userTableColumnSurname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        friendsTableColumnName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        friendsTableColumnSurname.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        usersTable.setItems(usersModel);
        friendsTable.setItems(friendsModel);
        notificationList.setItems(notificationsModel);
    }

    private void initModel() {
        List<User> userList = StreamSupport
                .stream(userService.findAllUsers().spliterator(), false)
                .collect(Collectors.toList());

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
                .collect(Collectors.toList());

        userList.removeIf(friendList::contains);

        List<Notification> notificationList = notificationService.readAllNotifications(LoginController.loggedUser);

        usersModel.setAll(userList);
        friendsModel.setAll(friendList);
        notificationsModel.setAll(notificationList);
    }

    @Override
    public void update(Observable observable) {
        initModel();
    }

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
            } catch (RepositoryException e) {
                AlertBox.showErrorMessage(null, e.getMessage());
            }
        }
        addFriendButton.setVisible(false);
    }

    public void filterUsers(KeyEvent keyEvent) {
        String filter = filterUsersTextField.getText();
        List<User> userList = StreamSupport.stream(userService.findAllUsers().spliterator(), false)
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
        initModel();
        deleteFriendButton.setVisible(false);
    }

    public void handleNotificationsClick(MouseEvent mouseEvent) {
        acceptButton.setVisible(true);
        rejectButton.setVisible(true);
        Notification notification = notificationList.getSelectionModel().getSelectedItem();
        if (mouseEvent.getClickCount() == 2) {
            AlertBox.showMessage(null, Alert.AlertType.INFORMATION, notification.getType() + " " +
                            notification.getTimestamp().format(Constants.DATE_TIME_FORMATTER),
                    notification.getEntityText());
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

    public void showAddButton(MouseEvent mouseEvent) {
        addFriendButton.setVisible(true);
    }

    public void showRemoveButton(MouseEvent mouseEvent) {
        deleteFriendButton.setVisible(true);
    }
}
