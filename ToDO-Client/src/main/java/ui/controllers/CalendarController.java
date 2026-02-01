package ui.controllers;



import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ui.controllers.SceneManager;
import ui.models.Task;
import client.Session;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @version 1.0
 * @date 01-02-2026
 * Steuert die Kalenderansicht der Anwendung.
 * Zeigt einen Monatskalender an, lädt die Aufgaben des eingeloggten Benutzers
 * vom Server und ordnet sie den jeweiligen Tagen zu.
 * Ermöglicht das Wechseln zwischen Monaten sowie Navigation zu anderen Views.
 */


public class CalendarController implements Initializable {

    ZonedDateTime dateFocus;
    ZonedDateTime today;

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private Text year;

    @FXML
    private Text month;

    @FXML
    private GridPane calendar;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userAvatarLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private HBox dayHeaderBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();

        if (Session.loggedInUserEmail != null) {
            userNameLabel.setText(Session.loggedInUserEmail.split("@")[0]);
            String initials = Session.loggedInUserEmail.substring(0, 2).toUpperCase();
            userAvatarLabel.setText(initials);
        }

        drawCalendar();
    }

    @FXML
    void backOneMonth(ActionEvent event) {
        dateFocus = dateFocus.minusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }

    @FXML
    void forwardOneMonth(ActionEvent event) {
        dateFocus = dateFocus.plusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }

    @FXML
    private void openDashboard() {
        Stage stage = (Stage) year.getScene().getWindow();
        SceneManager.switchTo(stage, "/ui/views/dashboard.fxml");
    }

    @FXML
    private void openSettings() {
        Stage stage = (Stage) year.getScene().getWindow();
        SceneManager.switchTo(stage, "/ui/views/settings.fxml");
    }

    @FXML
    private void onLogoutClick() {
        Session.loggedInUserEmail = null;
        Session.client = null;
        Stage stage = (Stage) year.getScene().getWindow();
        SceneManager.switchTo(stage, "/ui/views/login.fxml");
    }

    private void drawCalendar(){
        year.setText(String.valueOf(dateFocus.getYear()));
        month.setText(String.valueOf(dateFocus.getMonth()));

        Map<Integer, List<Task>> taskMap = getTasksForMonth(dateFocus);

        int monthMaxDate = dateFocus.toLocalDate().lengthOfMonth();
        int dateOffset = ZonedDateTime.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1,0,0,0,0,dateFocus.getZone()).getDayOfWeek().getValue();

        int row = 0;
        int col = 0;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane stackPane = new StackPane();
                stackPane.setMinSize(120, 80);
                stackPane.setPrefSize(150, 100);
                stackPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                Rectangle rectangle = new Rectangle();
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(1);
                rectangle.widthProperty().bind(stackPane.widthProperty());
                rectangle.heightProperty().bind(stackPane.heightProperty());
                stackPane.getChildren().add(rectangle);

                int calculatedDate = (j+1)+(7*i);
                if(calculatedDate > dateOffset){
                    int currentDate = calculatedDate - dateOffset;
                    if(currentDate <= monthMaxDate){
                        Text date = new Text(String.valueOf(currentDate));
                        date.setTranslateY(-30);
                        stackPane.getChildren().add(date);

                        List<Task> tasks = taskMap.get(currentDate);
                        if(tasks != null){
                            createTaskDisplay(tasks, stackPane.getPrefHeight(), stackPane.getPrefWidth(), stackPane);
                        }
                    }
                    if(today.getYear() == dateFocus.getYear() && today.getMonth() == dateFocus.getMonth() && today.getDayOfMonth() == currentDate){
                        rectangle.setStroke(Color.BLUE);
                    }
                }

                calendar.add(stackPane, j, i);
            }
        }
    }

    private void createTaskDisplay(List<Task> tasks, double rectangleHeight, double rectangleWidth, StackPane stackPane) {
        VBox taskBox = new VBox();
        taskBox.setSpacing(2);

        for (int k = 0; k < tasks.size(); k++) {
            if(k >= 2) {
                Text moreTasks = new Text("...");
                taskBox.getChildren().add(moreTasks);
                break;
            }

            Task task = tasks.get(k);
            String displayText = task.getTitle();

            if(task.isCompleted()){
                displayText = "✓ " + displayText;
            } else if(task.isTbd()){
                displayText = "? " + displayText;
            }

            Text text = new Text(displayText);
            text.setWrappingWidth(rectangleWidth * 0.75);


            if(task.isCompleted()) {
                text.setStrikethrough(true);
                text.setFill(Color.GRAY);
            } else if(task.isTbd()) {
                text.setFill(Color.ORANGE);
            }

            taskBox.getChildren().add(text);

        }

        taskBox.setTranslateY((rectangleHeight / 2) * 0.20);
        taskBox.setMaxWidth(rectangleWidth * 0.8);
        taskBox.setMaxHeight(rectangleHeight * 0.65);
        taskBox.setStyle("-fx-background-color:LIGHTGRAY; -fx-padding: 2;");
        stackPane.getChildren().add(taskBox);
    }

    private Map<Integer, List<Task>> createTaskMap(List<Task> tasks, int year, int month) {
        Map<Integer, List<Task>> taskMap = new HashMap<>();

        for (Task task : tasks) {
            if(task.getDateTbd() == null || task.getDateTbd().isEmpty()) {
                continue;
            }

            LocalDate taskDate = LocalDate.parse(task.getDateTbd());

            if(taskDate.getYear() == year && taskDate.getMonthValue() == month) {
                int dayOfMonth = taskDate.getDayOfMonth();

                if(!taskMap.containsKey(dayOfMonth)){
                    taskMap.put(dayOfMonth, new ArrayList<>(List.of(task)));
                } else {
                    taskMap.get(dayOfMonth).add(task);
                }
            }
        }
        return taskMap;
    }

    private Map<Integer, List<Task>> getTasksForMonth(ZonedDateTime dateFocus) {
        if(Session.loggedInUserEmail == null || Session.client == null) {
            System.err.println("Warning: User not logged in or client not initialized");
            return new HashMap<>();
        }

        try {

            String command = "GET_TASKS " + Session.loggedInUserEmail;
            String response = Session.client.send(command);

            Task[] taskArray = mapper.readValue(response, Task[].class);
            List<Task> allTasks = Arrays.asList(taskArray);

            return createTaskMap(allTasks, dateFocus.getYear(), dateFocus.getMonthValue());

        } catch (IOException e) {
            System.err.println("Error loading tasks from server: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public void refresh() {
        calendar.getChildren().clear();
        drawCalendar();
    }
}