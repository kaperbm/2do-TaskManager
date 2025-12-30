package ui.controllers;

import client.Session;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ui.SceneManager;

public class SettingsController {

    @FXML private Label userAvatarLabel;
    @FXML private Label userNameLabel;

    @FXML private CheckBox darkModeCheckBox;

    @FXML
    public void initialize() {

        String email = Session.loggedInUserEmail != null ? Session.loggedInUserEmail : "admin@example.com";

        String avatar = email.length() >= 2 ? email.substring(0, 2).toUpperCase() : "US";
        userAvatarLabel.setText(avatar);
        userNameLabel.setText("Admin User");
        darkModeCheckBox.setSelected(Session.darkMode);
    }

    @FXML
    private void onToggleDarkMode() {
        Session.darkMode = darkModeCheckBox.isSelected();

        // sofort anwenden auf aktueller Scene
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
