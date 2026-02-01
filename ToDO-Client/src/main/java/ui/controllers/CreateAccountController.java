package ui.controllers;

import client.NetworkClient;
import client.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ui.controllers.SceneManager;

/**
 * @version 1.0
 * @date 01-02-2026
 * Steuert die Registrierung neuer Benutzer und die Kommunikation mit dem Server.
 */


public class CreateAccountController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void createNewAccountClick() {
        try {
            String email = emailField.getText();
            String name  = nameField.getText();
            String pass  = passwordField.getText();

            if (Session.client == null) {
                Session.client = new NetworkClient("localhost", 5001);
            }

            String response = Session.client.send("CREATE_ACCOUNT " + name + " " + email + " " + pass);

            if(response.startsWith("OK")) {
                errorLabel.setText("Account erfolgreich erstellt!");
            } else if (response.startsWith("ERR"))
                errorLabel.setText("Account existiert bereits!"); {
            }
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Server nicht erreichbar");
            }

    }

    @FXML
    public void onBackToLoginClick() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        SceneManager.switchTo(stage, "/ui/views/login.fxml");
    }
}