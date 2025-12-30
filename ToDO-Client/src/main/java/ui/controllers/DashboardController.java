package ui.controllers;

import client.Session;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.SceneManager;
import ui.models.Task;


import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class DashboardController {

    @FXML public Button saveButton;
    @FXML private Label userAvatarLabel;
    @FXML private Label userNameLabel;
    @FXML private Label welcomeLabel;

    @FXML private Label openCountLabel;
    @FXML private Label inProgressCountLabel;
    @FXML private Label doneCountLabel;

    @FXML private Label tasksInfoLabel;

    @FXML private VBox taskDisplayVBox;

    private List<Task> currentTaskList;
    private List<Task> taskList;
    private List<Task> tasksToDelete = new ArrayList<>();


    @FXML
    public void initialize() throws IOException {
        String email = Session.loggedInUserEmail != null
                ? Session.loggedInUserEmail
                : "admin@example.com";

        String avatar = "AD";
        if (email.length() >= 2) {
            avatar = email.substring(0, 2).toUpperCase();
        }

        userAvatarLabel.setText(avatar);
        userNameLabel.setText("Admin User");
        welcomeLabel.setText("Willkommen zurück, Admin User!");

        openCountLabel.setText("0");
        inProgressCountLabel.setText("0");
        doneCountLabel.setText("0");

        tasksInfoLabel.setText("Keine offenen Aufgaben");


        refreshDashboard();
        onOpenClicked();

    }
    @FXML
    private void onLogoutClick() {
        Stage stage = (Stage) userAvatarLabel.getScene().getWindow();
        SceneManager.switchTo(stage, "/ui/views/login.fxml");
    }
    @FXML
    private void openCalendar() {
        Stage stage = (Stage) userAvatarLabel.getScene().getWindow();
        SceneManager.switchTo(stage, "/ui/views/calendar.fxml");
    }
    @FXML
    private void openSettings() {
        Stage stage = (Stage) userAvatarLabel.getScene().getWindow();
        SceneManager.switchTo(stage, "/ui/views/settings.fxml");
    }
    @FXML
    public void onAddEventClick() {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Neues Event");
            dialog.setHeaderText("Event hinzufügen");
            dialog.setContentText("Titel eingeben:");

            var result = dialog.showAndWait();
            if (result.isEmpty()) return;

            String title = result.get().trim();
            if (title.isBlank()) return;

            String date = LocalDate.now().toString();

            String cmd = "ADD_TASK " + Session.loggedInUserEmail + " " + date + " " + title;

            Session.client.send(cmd);
            onOpenClicked();
            refreshDashboard();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onOpenClicked() throws IOException {
        taskDisplayVBox.getChildren().clear();
        getTasksFromServer(1);
        for (Task task : currentTaskList) {
            HBox taskRow = createTaskRow(task);
            taskDisplayVBox.getChildren().add(taskRow);
        }
        tasksInfoLabel.setText("Offene Aufgaben");
    }

    @FXML
    public void onInProgressClicked() throws IOException {
        taskDisplayVBox.getChildren().clear();
        getTasksFromServer(2);
        for (Task task : currentTaskList) {
            HBox taskRow = createTaskRow(task);
            taskDisplayVBox.getChildren().add(taskRow);
        }
        tasksInfoLabel.setText("Aufgaben in Bearbeitung");
    }

    @FXML
    public void onDoneClicked() throws IOException {
        taskDisplayVBox.getChildren().clear();
        getTasksFromServer(3);
        for (Task task : currentTaskList) {
            HBox taskRow = createTaskRow(task);
            taskDisplayVBox.getChildren().add(taskRow);
        }
        tasksInfoLabel.setText("Abgeschlossene Aufgaben");
    }

    private void getTasksFromServer(int i) throws IOException {
        String response = Session.client.send("GET_TASKS " + Session.loggedInUserEmail + " " + i);
        String allTasksJson = Session.client.send("GET_TASKS " + Session.loggedInUserEmail);

        if (response.isEmpty()) {
            Label emptyLabel = new Label("Keine Tasks vorhanden");
            emptyLabel.setStyle("-fx-text-fill: gray; -fx-padding: 20;");
            taskDisplayVBox.getChildren().add(emptyLabel);
            currentTaskList = new ArrayList<>();
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        taskList = mapper.readValue(allTasksJson, new TypeReference<>() {});
        currentTaskList = mapper.readValue(response, new TypeReference<>() {});
    }

    private HBox createTaskRow(Task task) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 10, 10, 10));
        row.setMaxWidth(Double.MAX_VALUE);

        Label nameLabel = new Label(task.getTitle());
        nameLabel.setStyle("-fx-font-weight: normal; -fx-font-size: 14px;");
        nameLabel.setMinWidth(150);
        nameLabel.setMaxWidth(150);

        TextField descField = new TextField(task.getDescription());
        descField.setMinWidth(150);
        descField.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(descField, Priority.ALWAYS);
        descField.textProperty().addListener((observable, oldValue, newValue) -> {
            task.setDescription(newValue);
        });

        HBox tbdBox = new HBox();
        tbdBox.setAlignment(Pos.CENTER);
        tbdBox.setMinWidth(120);
        tbdBox.setMaxWidth(120);
        CheckBox toBeDoneCheckbox = new CheckBox();
        tbdBox.getChildren().add(toBeDoneCheckbox);

        HBox doneBox = new HBox();
        doneBox.setAlignment(Pos.CENTER);
        doneBox.setMinWidth(80);
        doneBox.setMaxWidth(80);
        CheckBox doneCheckBox = new CheckBox();
        doneBox.getChildren().add(doneCheckBox);

        toBeDoneCheckbox.setSelected(task.isTbd());
        toBeDoneCheckbox.setOnAction(e -> {
            task.setTbd(toBeDoneCheckbox.isSelected());
            if (toBeDoneCheckbox.isSelected()) {
                task.setTbd(true);
                task.setCompleted(false);
                doneCheckBox.setSelected(false);
            }
        });

        doneCheckBox.setSelected(task.isCompleted());
        doneCheckBox.setOnAction(e -> {
            task.setCompleted(doneCheckBox.isSelected());
            if (doneCheckBox.isSelected()) {
                task.setCompleted(true);
                task.setTbd(false);
                toBeDoneCheckbox.setSelected(false);
            }
        });

        Label dateLabel = new Label(task.getDate());
        dateLabel.setStyle("-fx-text-fill: #999999;");
        dateLabel.setMinWidth(100);
        dateLabel.setMaxWidth(100);

        HBox deleteBox = new HBox();
        deleteBox.setAlignment(Pos.CENTER);
        deleteBox.setMinWidth(60);
        deleteBox.setMaxWidth(60);
        Button deleteBtn = new Button("🗑");
        deleteBox.getChildren().add(deleteBtn);
        deleteBtn.setOnAction(e -> {
            try {
                deleteTask(task);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        row.getChildren().addAll(
                nameLabel,
                descField,
                tbdBox,
                doneBox,
                dateLabel,
                deleteBox
        );

        return row;
    }

    private void deleteTask(Task task) throws IOException {
        if (tasksToDelete.contains(task)) {
            tasksToDelete.remove(task);
        } else {
            tasksToDelete.add(task);
        }

        taskDisplayVBox.getChildren().clear();
        for (Task t : currentTaskList) {
            HBox taskRow = createTaskRow(t);

            if (tasksToDelete.contains(t)) {
                taskRow.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-background-color: #ffcccc;");
            }

            taskDisplayVBox.getChildren().add(taskRow);
        }
    }

    private void refreshDashboard() {
        try {

            record TaskCounts(int open, int tbd, int done) {}
            String getTaskCount = Session.client.send("GET_TASKCOUNT " + Session.loggedInUserEmail);

            ObjectMapper mapper = new ObjectMapper();
            TaskCounts counts = mapper.readValue(getTaskCount, TaskCounts.class);

            openCountLabel.setText(String.valueOf(counts.open));
            inProgressCountLabel.setText(String.valueOf(counts.tbd));
            doneCountLabel.setText(String.valueOf(counts.done));

        } catch (Exception e) {
            e.printStackTrace();

            tasksInfoLabel.setText("Fehler beim Laden der Aufgaben");
        }
    }


    @FXML
    public void onSaveClick() throws IOException {

        for (Task task : tasksToDelete) {
            taskList.removeIf(t -> t.getId().equals(task.getId()));
            currentTaskList.removeIf(t -> t.getId().equals(task.getId()));
        }

        tasksToDelete.clear();

        taskDisplayVBox.getChildren().clear();
        for (Task t : currentTaskList) {
            HBox taskRow = createTaskRow(t);
            taskDisplayVBox.getChildren().add(taskRow);
        }

        int index = 0;
        for (Task task : taskList) {
            for (Task currentTask : currentTaskList) {
                if (task.getId().equals(currentTask.getId())) {
                    if (currentTask.isCompleted() != task.isCompleted() || currentTask.isTbd() != task.isTbd() ||
                            !currentTask.getDescription().equals(task.getDescription())) {

                        taskList.set(index, currentTask);
                    }
                }
            }
            index++;
        }

        ObjectMapper mapper = new ObjectMapper();
        String jsonTasks = mapper.writeValueAsString(taskList);


        String cmd = "SAVE_TASKS " + Session.loggedInUserEmail + " " + jsonTasks;
        Session.client.send(cmd);

        refreshDashboard();

        if (tasksInfoLabel.getText().contains("Offene")) {
            onOpenClicked();
        }
        if (tasksInfoLabel.getText().contains("Bearbeitung")) {
            onInProgressClicked();
        }
        if (tasksInfoLabel.getText().contains("Abgeschlossene")) {
            onDoneClicked();
        }
    }

}

