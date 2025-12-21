package data.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import data.models.Task;
import data.models.User;
import server.ServerConfig;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kacper Bohaczyk
 * Die Klasse managed der Speicher und erstellen der Tasks, sowie das laden.
 */

public class TaskManager {

    private List<User> userWithTasks = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public TaskManager(String email) {
        try{
            load(email);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addTask(String email, String date, String title) throws IOException {
        Task task = new Task(date, title);
        saveTask(email, task);

    }

    public int getTaskCount(String email) throws IOException {
        return load(email).size();
    }

    public List<Task> getTasksWithDate() {
        List<Task> result = new ArrayList<>();
        for( User u : userWithTasks) {
            for (Task t : u.getTaskList()) {
                if (t.getDate() != null) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    public String getAllTasksString() {
        StringBuilder sb = new StringBuilder();
        for (User u : userWithTasks) {
            String date = "";
            for( Task t : u.getTaskList()) {
                if (t.getDescription() != null) {
                    date = t.getDescription();
                }
                sb.append(t.getId()).append(";")
                        .append(t.getDate()).append(";")
                        .append(t.getTitle()).append(";")
                        .append(date)
                        .append("\n");
            }
            }
        return sb.toString();
    }

    private List<Task> load(String email) throws IOException {
        File file = new File(ServerConfig.TASK_FILE);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        User[] taskArray = mapper.readValue(file, User[].class);
        userWithTasks = new ArrayList<>(Arrays.asList(taskArray));

        for(User u : userWithTasks){
            if(u.getEmail().equals(email)){
                return u.getTaskList();
            }
        }

        return null;
    }

    private void saveTask(String email, Task task) throws IOException {
        File file = new File(ServerConfig.TASK_FILE);
        file.getParentFile().mkdirs();

        List<User> userList = new ArrayList<>();

        if (file.exists() && file.length() > 0) {
            User[] userArray = mapper.readValue(file, User[].class);
            userList = new ArrayList<>(Arrays.asList(userArray));
        }

        boolean userFound = false;
        for (User u : userList) {
            if (u.getEmail().equals(email)) {
                if (u.getTaskList() == null) {
                    u.setTaskList(new ArrayList<>());
                }
                u.getTaskList().add(task);
                userFound = true;
                break;
            }
        }

        if (!userFound) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setTaskList(new ArrayList<>());
            newUser.getTaskList().add(task);
            userList.add(newUser);
        }

        String json = mapper.writerWithView(User.TaskFileView.class).withDefaultPrettyPrinter().writeValueAsString(userList);

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(json);
            fw.flush();
        }

        System.out.println("File written: " + file.length() + " bytes");
    }
}
