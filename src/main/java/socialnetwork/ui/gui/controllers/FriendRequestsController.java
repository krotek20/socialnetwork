package socialnetwork.ui.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import socialnetwork.Utils.design.NotifyStatus;
import socialnetwork.Utils.design.Observer;
import socialnetwork.domain.entities.Friendship;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.enums.FriendshipStatus;
import socialnetwork.repository.RepositoryException;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.UserService;
import socialnetwork.ui.gui.MainGUI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FriendRequestsController implements Observer {

    private final ObservableList<User> requestsModel = FXCollections.observableArrayList();
    private FriendshipService friendshipService;
    private UserService userService;

    @FXML
    private ListView<User> requestsList;
    @FXML
    private Button cancelRequestButton;

    @FXML
    public void initialize() {
        cancelRequestButton.setDisable(true);

        this.friendshipService = MainGUI.getFriendshipService();
        this.userService = MainGUI.getUserService();
        friendshipService.register(this);
        userService.register(this);

        requestsList.setItems(requestsModel);
        initModel();
    }

    private void initModel() {
        List<Friendship> friendships = userService.findFriendships(LoginController.loggedUser.getID());
        List<User> pendingRequests = friendships
                .stream()
                .filter(x -> x.getStatus().equals(FriendshipStatus.PENDING) &&
                        x.getID().getRight().equals(LoginController.loggedUser.getID()))
                .map(x -> userService.findOneUser(x.getID().getLeft().toString()))
                .collect(Collectors.toList());
        requestsModel.setAll(pendingRequests);
    }

    @Override
    public void update(NotifyStatus status) {
        initModel();
    }

    public void handleCancelRequest(MouseEvent mouseEvent) {
        User user = requestsList.getSelectionModel().getSelectedItem();
        String id1 = user.getID().toString();
        String id2 = LoginController.loggedUser.getID().toString();
        Map<String, String> friendshipMap = new HashMap<>();
        friendshipMap.put("id1", id1);
        friendshipMap.put("id2", id2);
        try {
            friendshipService.deleteFriendship(friendshipMap);
            AlertBox.showMessage(null, Alert.AlertType.CONFIRMATION,
                    "Request cancelled", "Friend request has been cancelled.");
        } catch (RepositoryException e) {
            AlertBox.showErrorMessage(null, e.getMessage());
        }
        cancelRequestButton.setDisable(true);
    }

    public void handleUserSelection(MouseEvent mouseEvent) {
        cancelRequestButton.setDisable(false);
    }
}
