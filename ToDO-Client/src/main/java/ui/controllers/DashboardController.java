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
import ui.controllers.SceneManager;
import ui.models.Task;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.application.Platform;
import javafx.util.Pair;
import java.time.LocalDate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @date 01-02-2026
 * Steuert das Dashboard, lädt Aufgaben vom Server, zeigt sie an
 * und ermöglicht Bearbeitung, Sortierung und Speicherung.
 */

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

    @FXML private Label titleHeaderLabel;
    @FXML private Label deleteHeaderLabel;

    private boolean isAlphabeticalAscending = true;
    private boolean isDateAscending = true;
    private boolean isDateTbdAscending = true;


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
        userNameLabel.setText(Session.loggedInUserEmail.split("@")[0]);
        welcomeLabel.setText("Willkommen zurück, " +  Session.loggedInUserEmail.split("@")[0] +"!");

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
            Dialog<Pair<String, LocalDate>> dialog = new Dialog<>();
            dialog.setTitle("Neues Event");
            dialog.setHeaderText("Event hinzufügen");

            ButtonType addButtonType = new ButtonType("Hinzufügen", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField titleField = new TextField();
            titleField.setPromptText("Titel");
            DatePicker deadlinePicker = new DatePicker();
            deadlinePicker.setValue(LocalDate.now());
            deadlinePicker.setPromptText("Fälligkeitsdatum auswählen");

            grid.add(new Label("Titel:"), 0, 0);
            grid.add(titleField, 1, 0);
            grid.add(new Label("Fällig bis:"), 0, 1);
            grid.add(deadlinePicker, 1, 1);

            dialog.getDialogPane().setContent(grid);

            Platform.runLater(() -> titleField.requestFocus());

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    return new Pair<>(titleField.getText(), deadlinePicker.getValue());
                }
                return null;
            });

            var result = dialog.showAndWait();
            if (result.isEmpty()) return;

            String title = result.get().getKey().trim();
            LocalDate deadline = result.get().getValue();

            if (title.isBlank() || deadline == null) return;

            String currentDate = LocalDate.now().toString();
            String deadlineDate = deadline.toString();

            String cmd = "ADD_TASK " + Session.loggedInUserEmail + " " + currentDate + " " + title + " " + deadlineDate;

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

        Label dateLabel2 = new Label(task.getDateTbd());
        dateLabel2.setStyle("-fx-text-fill: #999999;");
        dateLabel2.setMinWidth(100);
        dateLabel2.setMaxWidth(100);

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
                dateLabel2,
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

    @FXML
    public void onAlphabeticalClicked() throws IOException {
        if (currentTaskList != null && !currentTaskList.isEmpty()) {
            if (isAlphabeticalAscending) {
                currentTaskList.sort((task1, task2) ->
                        task1.getTitle().compareToIgnoreCase(task2.getTitle())
                );
            } else {
                currentTaskList.sort((task1, task2) -> {
                    String title1 = task1.getTitle().toLowerCase();
                    String title2 = task2.getTitle().toLowerCase();

                    if (title1.isEmpty() || title2.isEmpty()) {
                        return title1.compareTo(title2);
                    }
                    char first1 = title1.charAt(0);
                    char first2 = title2.charAt(0);

                    if (first1 != first2) {
                        return Character.compare(first2, first1);
                    } else {
                        return title1.compareTo(title2);
                    }
                });
            }

            isAlphabeticalAscending = !isAlphabeticalAscending;

            taskDisplayVBox.getChildren().clear();
            for (Task task : currentTaskList) {
                HBox taskRow = createTaskRow(task);
                taskDisplayVBox.getChildren().add(taskRow);
            }

            String currentLabel = tasksInfoLabel.getText();
            String sortOrder = isAlphabeticalAscending ? "Z-A" : "A-Z";
            currentLabel = currentLabel.replaceAll(" \\(alphabetisch sortiert.*\\)", "");
            tasksInfoLabel.setText(currentLabel + " (alphabetisch sortiert " + sortOrder + ")");
        }
    }

    @FXML
    public void onDateClicked() throws IOException {
        if (currentTaskList != null && !currentTaskList.isEmpty()) {
            if (isDateAscending) {
                currentTaskList.sort((task1, task2) ->
                        LocalDate.parse(task1.getDate()).compareTo(LocalDate.parse(task2.getDate()))
                );
            } else {
                currentTaskList.sort((task1, task2) ->
                        LocalDate.parse(task2.getDate()).compareTo(LocalDate.parse(task1.getDate()))
                );
            }

            isDateAscending = !isDateAscending;
            taskDisplayVBox.getChildren().clear();
            for (Task task : currentTaskList) {
                HBox taskRow = createTaskRow(task);
                taskDisplayVBox.getChildren().add(taskRow);
            }

            String currentLabel = tasksInfoLabel.getText();
            String sortOrder = isDateAscending ? "neueste zuerst" : "älteste zuerst";
            currentLabel = currentLabel.replaceAll(" \\(.*sortiert.*\\)", "");
            tasksInfoLabel.setText(currentLabel + " (nach Erstelldatum sortiert: " + sortOrder + ")");
        }
    }

    @FXML
    public void onDateTbdClicked() throws IOException {
        if (currentTaskList != null && !currentTaskList.isEmpty()) {
            if (isDateTbdAscending) {
                currentTaskList.sort((task1, task2) -> {
                    String date1 = task1.getDateTbd();
                    String date2 = task2.getDateTbd();

                    if (date1 == null && date2 == null) return 0;
                    if (date1 == null) return 1;
                    if (date2 == null) return -1;

                    return LocalDate.parse(date1).compareTo(LocalDate.parse(date2));
                });
            } else {
                currentTaskList.sort((task1, task2) -> {
                    String date1 = task1.getDateTbd();
                    String date2 = task2.getDateTbd();

                    if (date1 == null && date2 == null) return 0;
                    if (date1 == null) return 1;
                    if (date2 == null) return -1;

                    return LocalDate.parse(date2).compareTo(LocalDate.parse(date1));
                });
            }

            isDateTbdAscending = !isDateTbdAscending;
            taskDisplayVBox.getChildren().clear();
            for (Task task : currentTaskList) {
                HBox taskRow = createTaskRow(task);
                taskDisplayVBox.getChildren().add(taskRow);
            }

            String currentLabel = tasksInfoLabel.getText();
            String sortOrder = isDateTbdAscending ? "späteste zuerst" : "früheste zuerst";
            currentLabel = currentLabel.replaceAll(" \\(.*sortiert.*\\)", "");
            tasksInfoLabel.setText(currentLabel + " (nach Fälligkeit sortiert: " + sortOrder + ")");
        }
    }

    @FXML
    public void onDeleteHeaderClicked() throws IOException {
        isAlphabeticalAscending = true;
        isDateAscending = true;
        isDateTbdAscending = true;
        onOpenClicked();
    }

}

