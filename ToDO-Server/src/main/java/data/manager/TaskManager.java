package data.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import data.models.Task;
import server.ServerConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kacper Bohaczyk
 * Die Klasse managed der Speicher und erstellen der Tasks, sowie das laden.
 */

public class TaskManager {

    private List<Task> tasks = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public TaskManager() {
        try{
            load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addTask(String date, String title) throws IOException {
        saveTask(new Task(date, title));
    }

    public void addTask(String date, String title, String description) throws IOException {
        saveTask(new Task(title, description, date));
    }

    public int getTaskCount() {
        return tasks.size();
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    public List<Task> getTasksWithDate() {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getDate() != null) {
                result.add(t);
            }
        }
        return result;
    }

    public String getAllTasksString() {
        StringBuilder sb = new StringBuilder();
        for (Task t : tasks) {
            String date = "";
            if (t.getDescription() != null) {
                date = t.getDescription();
            }
            sb.append(t.getId()).append(";")
                    .append(t.getDate()).append(";")
                    .append(t.getTitle()).append(";")
                    .append(date)
                    .append("\n");
        }
        return sb.toString();
    }


    private List<Task> load() throws IOException {
        File file = new File(ServerConfig.TASK_FILE);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        tasks = List.of(mapper.readValue(file, Task[].class));

        return tasks;
    }

    private void saveTask(Task task) throws IOException {
        File file = new File(ServerConfig.TASK_FILE);
        file.getParentFile().mkdirs();

        List<Task> tasks = load();

        tasks.add(task);

        mapper.writerWithDefaultPrettyPrinter().writeValue(file, tasks);
    }
}
