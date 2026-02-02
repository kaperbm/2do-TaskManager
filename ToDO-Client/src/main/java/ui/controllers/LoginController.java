package ui.controllers;

import client.NetworkClient;
import client.Session;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ui.controllers.SceneManager;

/**
 * @version 1.0
 * @date 01-02-2026
 * Steuert den Login, sowie die Anmeldung beim Server.
 */


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

    /**
     * Initialisiert den Login-Controller beim Laden der View.
     * Im Debug-Modus werden automatisch Test-Credentials eingetragen und der Login ausgeführt.
     */
    @FXML
    private void initialize() {
        if (DEBUG_MODE) {
            emailField.setText(DEBUG_EMAIL);
            passwordField.setText(DEBUG_PASSWORD);
            Platform.runLater(() -> onLoginClick() );
        }
    }

    /**
     * Verarbeitet den Login-Versuch des Benutzers.
     * Stellt bei Bedarf eine Verbindung zum Server her, sendet die Login-Daten
     * und wechselt bei Erfolg zur Dashboard-Ansicht oder zeigt eine Fehlermeldung an.
     */
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
    /**
     * Wechselt zur Registrierungs-Ansicht (Create Account).
     */
    @FXML
    public void onCreateAccountClick() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        SceneManager.switchTo(stage, "/ui/views/createAccount.fxml");
    }
}