package socialnetwork.ui.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import socialnetwork.Utils.Events.UsersChangeEvent;
import socialnetwork.Utils.Observer.Observer;
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
import java.util.stream.StreamSupport;

public class MainController implements Observer<UsersChangeEvent> {
    UserService userService;
    FriendshipService friendshipService;
    ObservableList<User> usersModel = FXCollections.observableArrayList();
    ObservableList<User> friendsModel = FXCollections.observableArrayList();


    @FXML
    private Tab accountTab;

    @FXML
    private Tab notificationsTab;

    @FXML
    private Tab chatsTab;

    @FXML
    TextField filterUsersTextField;

    @FXML
    TextField filterFriendsTextField;

    @FXML
    TableView<User> usersTable;

    @FXML
    TableColumn<User,String> tableColumnName;

    @FXML
    TableColumn<User,String> tableColumnSurname;

    @FXML
    TableView<User> friendsTable;

    @FXML
    TableColumn<User,String> friendsTableColumnName;

    @FXML
    TableColumn<User,String> friendsTableColumnSurname;

    @FXML
    private Button addFriendButton;

    public void setServices(){
        this.userService = MainGUI.getUserService();
        this.friendshipService = MainGUI.getFriendshipService();
        userService.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize(){
        tableColumnName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableColumnSurname.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        usersTable.setItems(usersModel);
        friendsTableColumnName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        friendsTableColumnSurname.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        friendsTable.setItems(friendsModel);
    }

    private void initModel() {

        Iterable<User> users = userService.findAllUsers();
        List<User> userList = StreamSupport.stream(users.spliterator(), false)
                .collect(Collectors.toList());


        List<Friendship> friendships = userService.findFriendships(LoginController.loggedUser.getID());
        List<Friendship> accetpedFriendships = friendships
                .stream()
                .filter(x -> x.getStatus().equals(FriendshipStatus.APPROVED))
                .collect(Collectors.toList());
        List<User> friendList = accetpedFriendships
                .stream()
                .map(x -> {
                    if(x.getID().getLeft().equals(LoginController.loggedUser.getID()))
                        return userService.findOneUser(x.getID().getRight().toString());
                    else
                        return userService.findOneUser(x.getID().getLeft().toString());
                })
                .collect(Collectors.toList());

        userList.removeIf(friendList::contains);

        usersModel.setAll(userList);
        friendsModel.setAll(friendList);
    }

    @Override
    public void update(UsersChangeEvent usersChangeEvent) {
        initModel();
    }

    public void handleAddFriend(MouseEvent mouseEvent) {
        addFriendButton.setDisable(true);

        User user = usersTable.getSelectionModel().getSelectedItem();

        if(user == null){
            AlertBox.showErrorMessage(null, "No selected user!");
        }
        else{
            String id1 = user.getID().toString();
            String id2 = LoginController.loggedUser.getID().toString();
            Map<String,String> friendshipMap = new HashMap<String, String>();
            friendshipMap.put("id1",id1);
            friendshipMap.put("id2",id2);
            try {
                friendshipService.requestFriendship(friendshipMap);
                AlertBox.showMessage(null, Alert.AlertType.CONFIRMATION, "Request sent!", "Your friend request has been sent.");
            }
            catch(RepositoryException e){
                AlertBox.showErrorMessage(null, e.getMessage());
            }
        }

        addFriendButton.setDisable(false);
    }


    public void filterUsers(KeyEvent keyEvent) {
        String filter = filterUsersTextField.getText();
        Iterable<User> users = userService.findAllUsers();
        List<User> userList = StreamSupport.stream(users.spliterator(), false)
                .filter(x -> (x.getFirstName() + " " + x.getLastName()).toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList());
        userList.removeIf(friendsModel::contains);
        usersModel.setAll(userList);
    }
    public void filterFriends(KeyEvent keyEvent) {
        String filter = filterFriendsTextField.getText();

        List<Friendship> friendships = userService.findFriendships(LoginController.loggedUser.getID());
        List<Friendship> accetpedFriendships = friendships
                .stream()
                .filter(x -> x.getStatus().equals(FriendshipStatus.APPROVED))
                .collect(Collectors.toList());
        List<User> friendList = accetpedFriendships
                .stream()
                .map(x -> {
                    if(x.getID().getLeft().equals(LoginController.loggedUser.getID()))
                        return userService.findOneUser(x.getID().getRight().toString());
                    else
                        return userService.findOneUser(x.getID().getLeft().toString());
                })
                .filter(x -> (x.getFirstName() + " " + x.getLastName()).toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList());
        friendsModel.setAll(friendList);
    }
}
