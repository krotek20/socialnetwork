package socialnetwork.ui.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import socialnetwork.Utils.design.NotifyStatus;
import socialnetwork.Utils.design.Observer;
import socialnetwork.domain.entities.User;
import socialnetwork.service.ChatService;
import socialnetwork.service.UserService;
import socialnetwork.ui.gui.MainGUI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GroupChatController implements Observer {

    private final ObservableList<User> usersModel = FXCollections.observableArrayList();
    private final ObservableList<User> groupUsersModel = FXCollections.observableArrayList();

    private UserService userService;
    private ChatService chatService;

    @FXML
    private Button createGroupChatButton;
    @FXML
    private Button addToGroupChatButton;
    @FXML
    private Button removeFromGroupChatButton;
    @FXML
    private ListView<User> groupChatUsersList;
    @FXML
    private TextField filterUsersTextField;
    @FXML
    private TextField groupNameTextField;
    @FXML
    private ListView<User> addedUsersList;

    @FXML
    public void initialize() {
        addToGroupChatButton.setDisable(true);
        createGroupChatButton.setDisable(true);
        removeFromGroupChatButton.setDisable(true);

        groupChatUsersList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.userService = MainGUI.getUserService();
        this.chatService = MainGUI.getChatService();
        userService.register(this);
        chatService.register(this);

        groupChatUsersList.setItems(usersModel);
        addedUsersList.setItems(groupUsersModel);
        initModel();
    }

    @Override
    public void update(NotifyStatus status) {
        initModel();
    }

    private void initModel() {
        List<User> userList = StreamSupport
                .stream(userService.findAllUsers().spliterator(), false)
                .filter(x -> !x.getID().equals(LoginController.loggedUser.getID()))
                .filter(x -> {
                    for (User user : groupUsersModel) {
                        if (x.getID().equals(user.getID()))
                            return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        usersModel.setAll(userList);
    }


    public void handleCreateGroupChat(MouseEvent mouseEvent) {
        ObservableList<User> usersList = addedUsersList.getItems();

        Map<String, String> usersMap = new HashMap<>();
        for (User user : usersList) {
            usersMap.put(user.getLastName(), user.getID().toString());
        }

        chatService.createChat(groupNameTextField.getText(), usersMap, LoginController.loggedUser);

        AlertBox.showMessage(null, Alert.AlertType.CONFIRMATION,
                "Group Chat Created!", "Your group chat has been created.");
        Stage groupChatStage = (Stage) createGroupChatButton.getScene().getWindow();
        groupChatStage.close();
    }

    public void filterUserList(KeyEvent keyEvent) {
        String filter = filterUsersTextField.getText();

        List<User> userList = StreamSupport
                .stream(userService.findAllUsers().spliterator(), false)
                .filter(x -> (x.getFirstName() + " " + x.getLastName()).toLowerCase().contains(filter.toLowerCase()))
                .filter(x -> !x.getID().equals(LoginController.loggedUser.getID()))
                .collect(Collectors.toList());

        usersModel.setAll(userList);
    }

    public void addToGroupList(MouseEvent mouseEvent) {
        ObservableList<User> usersList = groupChatUsersList.getSelectionModel().getSelectedItems();
        groupUsersModel.addAll(usersList);
        createGroupChatButton.setDisable(addedUsersList.getItems().size() <= 1);
        initModel();
        addToGroupChatButton.setDisable(true);
    }

    public void removeFromGroupList(MouseEvent mouseEvent) {
        User user = addedUsersList.getSelectionModel().getSelectedItem();
        usersModel.add(user);
        groupUsersModel.remove(user);
        createGroupChatButton.setDisable(addedUsersList.getItems().size() <= 1);
        initModel();
        removeFromGroupChatButton.setDisable(true);
    }

    public void handleGroupUserSelection(MouseEvent mouseEvent) {
        removeFromGroupChatButton.setDisable(false);
    }

    public void handleUserSelection(MouseEvent mouseEvent) {
        addToGroupChatButton.setDisable(false);
    }
}
