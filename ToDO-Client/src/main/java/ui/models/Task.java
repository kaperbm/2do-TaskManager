package ui.models;
import java.util.UUID;

public class Task {
    private String id;
    private String date;
    private String title;
    private String description;
    private boolean completed;
    private boolean tbd;

    public Task() {}

    public Task(String id, String date, String title, String description, boolean completed, boolean tbd) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.description = "";
        this.completed = false;
        this.tbd = false;
    }

    public Task(String date, String title, String description) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.title = title;
        this.description = description;
        this.completed = false;
        this.tbd = false;
    }

    public String getId() { return id; }
    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
    public boolean isTbd() { return tbd; }

    public void setId(String id) { this.id = id; }
    public void setDate(String date) { this.date = date; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setTbd(boolean tbd) { this.tbd = tbd; }
}