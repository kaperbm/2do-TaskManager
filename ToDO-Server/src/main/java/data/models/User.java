package data.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;
import java.util.List;

public class User {
    public static class UserFileView {}
    public static class TaskFileView {}

    @JsonView({UserFileView.class, TaskFileView.class})
    private String id;

    @JsonView({UserFileView.class})
    private String name;

    @JsonView({UserFileView.class, TaskFileView.class})
    private String email;

    @JsonView(UserFileView.class)
    private String password;

    @JsonView(TaskFileView.class)
    private List<Task> taskList;

    public void setEmail(String email) {
        this.email = email;
    }

    public User(){}

    public User(String name, String email, String password) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.password = password;
        taskList = new ArrayList<>();
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public List<Task> getTaskList() {
        return taskList;
    }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}