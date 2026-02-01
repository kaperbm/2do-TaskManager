package ui.controllers;

import client.Session;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import ui.controllers.SceneManager;

/**
 * @version 1.0
 * @date 01-02-2026
 * Steuert die Einstellungen-Ansicht inklusive Benutzeranzeige,
 * Passwortänderung, Dark-Mode und Navigation.
 */

public class SettingsController {

    @FXML private Label userAvatarLabel;
    @FXML private Label userNameLabel;
    @FXML private CheckBox darkModeCheckBox;

    @FXML
    public void initialize() {

        String email = Session.loggedInUserEmail != null ? Session.loggedInUserEmail : "admin@example.com";

        String avatar = email.length() >= 2 ? email.substring(0, 2).toUpperCase() : "US";
        userAvatarLabel.setText(avatar);
        userNameLabel.setText(Session.loggedInUserEmail.split("@")[0]);
        darkModeCheckBox.setSelected(Session.darkMode);
    }

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label passwordStatusLabel;

    @FXML
    private void onChangePasswordClick() {
        try {
            String email = Session.loggedInUserEmail;
            String oldPw = oldPasswordField.getText();
            String newPw = newPasswordField.getText();

            if (oldPw.isBlank() || newPw.isBlank()) {
                passwordStatusLabel.setText("Bitte beide Felder ausfüllen.");
                return;
            }

            String resp = Session.client.send("CHANGE_PASSWORD " + email + " " + oldPw + " " + newPw);

            if (resp.startsWith("OK")) {
                passwordStatusLabel.setText("Passwort geändert ✅");
                oldPasswordField.clear();
                newPasswordField.clear();
            } else {
                passwordStatusLabel.setText("Altes Passwort falsch ❌");
            }

        } catch (Exception e) {
            e.printStackTrace();
            passwordStatusLabel.setText("Serverfehler ❌");
        }
    }



    @FXML
    private void onToggleDarkMode() {
        Session.darkMode = darkModeCheckBox.isSelected();
        Stage stage = (Stage) darkModeCheckBox.getScene().getWindow();
        stage.getScene().getRoot().getStyleClass().remove("dark");
        if (Session.darkMode) {
            stage.getScene().getRoot().getStyleClass().add("dark");
        }
    }

    @FXML
    private void openDashboard() {
        Stage stage = (Stage) darkModeCheckBox.getScene().getWindow();
        SceneManager.switchTo(stage, "/ui/views/dashboard.fxml");
    }

    @FXML
    private void openCalendar() {
        Stage stage = (Stage) darkModeCheckBox.getScene().getWindow();
        SceneManager.switchTo(stage, "/ui/views/calendar.fxml");
    }

    @FXML
    private void onLogoutClick() {
        Stage stage = (Stage) darkModeCheckBox.getScene().getWindow();
        Session.loggedInUserEmail = null;
        SceneManager.switchTo(stage, "/ui/views/login.fxml");
    }
}
