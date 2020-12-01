package socialnetwork.ui.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.entities.User;
import socialnetwork.ui.gui.MainGUI;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginController {
    public static User loggedUser = null;

    @FXML
    private TextField emailLoginText;
    @FXML
    private PasswordField passwordLoginText;
    @FXML
    private Label loginFailedLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    @FXML
    public void initialize() {
        loginFailedLabel.setText("");
    }

    public void handleLoginButton() throws IOException {
        loginButton.setDisable(true);
        registerButton.setDisable(true);

        Map<String, String> loginMap = new HashMap<String, String>() {{
            put("email", emailLoginText.getText());
            put("password", passwordLoginText.getText());
        }};
        loggedUser = MainGUI.getUserService().verifyCredentials(loginMap);

        if (loggedUser == null) {
            loginFailedLabel.setText("Invalid login data. Please try again.");
        } else {
            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/mainLayout.fxml"));
            AnchorPane root = loader.load();

            Stage mainStage = new Stage();
            mainStage.setTitle("Main");

            Scene mainScene = new Scene(root);
            mainStage.setScene(mainScene);
            mainStage.show();
        }

        loginButton.setDisable(false);
        registerButton.setDisable(false);
    }
}
