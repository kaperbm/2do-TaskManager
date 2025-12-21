package ui.controllers;

import client.NetworkClient;
import client.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ui.SceneManager;

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
            System.out.println("Server Antwort: " + response);

//            if (response.startsWith("OK")) {
//                Session.loggedInUserEmail = email;
//
//                Stage stage = (Stage) emailField.getScene().getWindow();
//                SceneManager.switchTo(stage, "/ui/views/dashboard.fxml");
//
//            }else {
//                errorLabel.setText(response);
//            }

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