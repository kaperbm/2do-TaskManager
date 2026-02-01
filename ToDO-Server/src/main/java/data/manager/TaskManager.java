package data.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import data.json.TaskRepository;
import data.models.Task;
import data.models.User;
import server.ServerConfig;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @version 1.0
 * @date 01-02-2026
 * Verwaltet Aufgaben eines Benutzers inklusive Laden, Speichern, Filtern und Zählung.
 */


public class TaskManager{

    private List<User> userWithTasks = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();
    public record TaskCounts(int open, int tbd, int done) {}
    private final TaskRepository repo = new TaskRepository();

    public TaskManager(String email) {
        try{
            loadUserTask(email);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void addTask(String email, String date, String title, String dateTbd) throws IOException {
        Task task;
        if (dateTbd.equals(date)) {
            task = new Task(date, title);
        }
        else {
            task = new Task(date, title, dateTbd);
        }

        saveTask(email, task);

    }

    public String saveTask(String email, String json) throws IOException {

        List<Task> tasks = Arrays.asList(mapper.readValue(json, Task[].class));
        saveTasks(email, tasks);

        return "OK SAVE_TASKS";
    }

    public String getTaskCounts(String email) throws IOException {
        List<Task> tasks = loadUserTask(email);
        int open = 0;
        int tbd = 0;
        int done = 0;

        for (Task t : tasks) {
            if(t.getCompleted()){
                done++;
            }
            else if (t.getTbd()){
                tbd++;
            }
            else{
                open++;
            }
        }

        return mapper.writeValueAsString(new TaskCounts(open, tbd, done));
    }

    public String getUserTasks(String email) throws IOException {

        return mapper.writerWithView(User.TaskFileView.class).writeValueAsString(loadUserTask(email));
    }

    public String getUserTasks(String email, int i) throws IOException {

        List<Task> userWithTasks = new ArrayList<>();
        if(i == 1){
            for(Task t : loadUserTask(email)){
                if(t.getCompleted() == false && t.getTbd() == false){
                    userWithTasks.add(t);
                }
            }
        }
        if(i == 2){
            for(Task t : loadUserTask(email)){
                if(t.getTbd() == true){
                    userWithTasks.add(t);
                }
            }
        }
        if(i == 3){
            for(Task t : loadUserTask(email)){
                if(t.getCompleted() == true){
                    userWithTasks.add(t);
                }
            }
        }
        return mapper.writerWithView(User.TaskFileView.class).writeValueAsString(userWithTasks);
    }



    private List<Task> loadUserTask(String email) throws IOException {
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

        return new ArrayList<>();
    }

    private void saveTask(String email, Task task) throws IOException {

        List<User> userList = repo.checkIfUserExists();

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
        repo.saveAll(userList);
    }

    private void saveTasks(String email, List<Task> tasks) throws IOException {

        List<User> userList = repo.checkIfUserExists();
        boolean userFound = false;
        for (User u : userList) {
            if (u.getEmail().equals(email)) {
                u.setTaskList(tasks);
                userFound = true;
                break;
            }
        }

        if (!userFound) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setTaskList(tasks);
            userList.add(newUser);
        }

        repo.saveAll(userList);

        System.out.println("Tasks saved for " + email + ": " + tasks.size() + " tasks");
    }
}
