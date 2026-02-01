package ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @version 1.0
 * @date 01-02-2026
 * Wechselt die aktuelle JavaFX-Szene und übernimmt Fenster- und Style-Einstellungen.
 */
public class SceneManager {

    public static void switchTo(Stage stage, String fxmlPath) {
        try {
            boolean wasFullScreen = stage.isFullScreen();
            double width = stage.getWidth();
            double height = stage.getHeight();

            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource(fxmlPath)
            );

            Scene scene = new Scene(loader.load());


            var css = SceneManager.class.getResource("/ui/views/styles.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            } else {
                System.err.println("styles.css nicht gefunden!");
            }

            stage.setScene(scene);

            stage.setWidth(width);
            stage.setHeight(height);
            stage.setFullScreen(wasFullScreen);


            scene.getRoot().getStyleClass().remove("dark");

            if (client.Session.darkMode) {
                scene.getRoot().getStyleClass().add("dark");
            }

        } catch (Exception e) {
            System.err.println("SceneManager Fehler beim Laden von: " + fxmlPath);
            e.printStackTrace();
        }
    }
}