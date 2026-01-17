package ui.controllers;

import client.NetworkClient;
import client.Session;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ui.SceneManager;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private static final boolean DEBUG_MODE = false;
    private static final String DEBUG_EMAIL = "test";
    private static final String DEBUG_PASSWORD = "test";

    @FXML
    private void initialize() {
        if (DEBUG_MODE) {
            emailField.setText(DEBUG_EMAIL);
            passwordField.setText(DEBUG_PASSWORD);
            Platform.runLater(() -> onLoginClick() );
        }
    }

    @FXML
    private void onLoginClick() {
        try {
            String email = emailField.getText();
            String pass  = passwordField.getText();

            if (Session.client == null) {
                Session.client = new NetworkClient("localhost", 5001);
            }

            String response = Session.client.send("LOGIN " + email + " " + pass);

            if (response.startsWith("OK")) {
                Session.loggedInUserEmail = email;

                Stage stage = (Stage) emailField.getScene().getWindow();
                SceneManager.switchTo(stage, "/ui/views/dashboard.fxml");

            }else {
                errorLabel.setText("Login fehlgeschlagen!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Server nicht erreichbar");
        }

    }

    @FXML
    public void onCreateAccountClick() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        SceneManager.switchTo(stage, "/ui/views/createAccount.fxml");
    }
}