package socialnetwork.ui.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import socialnetwork.domain.enums.Gender;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.ui.gui.MainGUI;

import java.io.IOException;
import java.util.*;

public class RegisterController {

    @FXML
    private Button signUpButton;
    @FXML
    private TextField firstNameText;
    @FXML
    private TextField lastNameText;
    @FXML
    private TextField emailText;
    @FXML
    private PasswordField passwordText;
    @FXML
    private PasswordField confirmPasswordText;
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private Label firstNameEmptyLabel;
    @FXML
    private Label lastNameEmptyLabel;
    @FXML
    private Label emailEmptyLabel;
    @FXML
    private Label passwordEmptyLabel;
    @FXML
    private Label confirmEmptyLabel;
    @FXML
    private Label birthDateEmptyLabel;
    @FXML
    private ComboBox<String> genderComboBox;


    @FXML
    public void initialize() {
        firstNameEmptyLabel.setText("");
        lastNameEmptyLabel.setText("");
        emailEmptyLabel.setText("");
        passwordEmptyLabel.setText("");
        confirmEmptyLabel.setText("");
        birthDateEmptyLabel.setText("");

        ObservableList<String> options = FXCollections.observableArrayList();
        Gender[] genders = Gender.values();
        for (Gender gender : genders) {
            options.add(gender.toString());
        }
        genderComboBox.setItems(options);
    }

    public void handleSignUpButton(MouseEvent mouseEvent) throws IOException {
        String errors = "";
        String firstName = firstNameText.getText();
        if (firstName.equals("")) {
            handleEmptyField(firstNameText, firstNameEmptyLabel);
            errors += "error";
        }
        String lastName = lastNameText.getText();
        if (lastName.equals("")) {
            handleEmptyField(lastNameText, lastNameEmptyLabel);
            errors += "error";
        }
        String password = passwordText.getText();
        if (password.equals("")) {
            handleEmptyField(passwordText, passwordEmptyLabel);
            errors += "error";
        }
        String email = emailText.getText();
        if (email.equals("")) {
            handleEmptyField(emailText, emailEmptyLabel);
            errors += "error";
        }
        String gender = genderComboBox.getValue();
        try {
            String birthDate = birthDatePicker.getValue().toString();
        } catch (NullPointerException e) {
            birthDateEmptyLabel.setText("This field is required.");
            birthDatePicker.setStyle("-fx-text-box-border: #FF0000; -fx-focus-color: #FF0000");
            errors += "error";
        }
        if (errors.length() == 0) {
            Map<String, String> registerMap = new HashMap<String, String>() {{
                put("email", email);
                put("password", password);
                put("firstname", firstName);
                put("lastname", lastName);
                put("gender", gender);
                put("birthdate", birthDatePicker.getValue().toString());
            }};
            try {
                MainGUI.getUserService().createUser(registerMap);
                AlertBox.showMessage(null, Alert.AlertType.CONFIRMATION, "Account Created!",
                        "Account successfully created! You can now login into the app.");
                openLoginTab(null);
            } catch (ValidationException e) {
                AlertBox.showErrorMessage(null, "Invalid data!");
            }
        }
    }

    private void handleEmptyField(TextField textField, Label label) {
        label.setText("This field is required.");
        textField.setStyle("-fx-text-box-border: #FF0000; -fx-focus-color: #FF0000");
    }

    public void lastNameInput(KeyEvent keyEvent) {
        if (!firstNameText.getText().matches("^[A-Z][a-z]+([ -][A-Z][a-z]+)*$")) {
            firstNameEmptyLabel.setText("Invalid name.");
        } else {
            lastNameEmptyLabel.setText("");
            lastNameText.setStyle("-fx-text-box-border: #000000; -fx-focus-color: #000000");
        }
    }

    public void firstNameInput(KeyEvent keyEvent) {
        if (!firstNameText.getText().matches("^[A-Z][a-z]+([ -][A-Z][a-z]+)*$")) {
            firstNameEmptyLabel.setText("Invalid name.");
        } else {
            firstNameEmptyLabel.setText("");
            firstNameText.setStyle("-fx-text-box-border: #000000; -fx-focus-color: #000000");
        }
    }

    public void emailInput(KeyEvent keyEvent) {
        if (!emailText.getText().matches("^\\w{1,32}([.-]\\w+)*@\\w+([-]\\w+)*([.]\\w+)+$")) {
            emailEmptyLabel.setText("Invalid email address.");
        } else {
            emailEmptyLabel.setText("");
            emailText.setStyle("-fx-text-box-border: #000000; -fx-focus-color: #000000");
        }

    }

    public void passwordInput(KeyEvent keyEvent) {
        if (!(passwordText.getText().matches("^.{8,}$") &&
                passwordText.getText().matches("^.*[A-Z]+.*$") &&
                passwordText.getText().matches("^.*[0-9]+.*$")
        )) {
            passwordEmptyLabel.setText("Invalid password.");
        } else {
            passwordEmptyLabel.setText("");
            passwordText.setStyle("-fx-text-box-border: #000000; -fx-focus-color: #000000");
        }
    }

    public void confirmInput(KeyEvent keyEvent) {
        if (!passwordText.getText().equals(confirmPasswordText.getText())) {
            confirmEmptyLabel.setText("Passwords do not match.");
        } else {
            confirmEmptyLabel.setText("");
            confirmPasswordText.setStyle("-fx-text-box-border: #000000; -fx-focus-color: #000000");
        }
    }

    public void birthDateInput(ActionEvent actionEvent) {
        if (!passwordText.getText().equals("")) {
            birthDateEmptyLabel.setText("");
            birthDatePicker.setStyle("-fx-text-box-border: #000000; -fx-focus-color: #000000");
        }
    }

    public void openLoginTab(MouseEvent mouseEvent) throws IOException {
        Stage registerStage = (Stage) signUpButton.getScene().getWindow();
        registerStage.close();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/loginLayout.fxml"));
        BorderPane root = loader.load();

        Stage loginStage = new Stage();
        loginStage.setTitle("Main");


        Scene mainScene = new Scene(root);
        loginStage.setScene(mainScene);
        loginStage.show();
    }

}
