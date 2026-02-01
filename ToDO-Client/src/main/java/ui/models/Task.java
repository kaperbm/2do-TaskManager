package ui.models;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @version 1.0
 * @date 01-02-2026
 * UI-Modell zur Darstellung und Bearbeitung einer Aufgabe.
 */


public class Task {
    private String id;
    private LocalDate date;
    private String title;
    private String description;
    private LocalDate dateTbd;
    private boolean completed;
    private boolean tbd;

    public Task() {}

    public Task(String id, String date, String title, String description, boolean completed, boolean tbd) {
        this.id = id;
        this.date = LocalDate.parse(date);
        this.title = title;
        this.description = description;
        this.dateTbd = null;
        this.completed = completed;
        this.tbd = tbd;
    }

    public Task(String date, String title, String dateTbd) {
        this.id = UUID.randomUUID().toString();
        this.date = LocalDate.parse(date);
        this.title = title;
        this.description = "";
        this.dateTbd = LocalDate.parse(dateTbd);
        this.completed = false;
        this.tbd = false;
    }

    public Task(String date, String title,String dateTbd, String description ) {
        this.id = UUID.randomUUID().toString();
        this.date = LocalDate.parse(date);
        this.title = title;
        this.description = description;
        this.dateTbd = (dateTbd != null && !dateTbd.isBlank()) ? LocalDate.parse(dateTbd) : null;
        this.completed = false;
        this.tbd = false;
    }

    public String getId() { return id; }
    public String getDate() { return date.toString(); }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDateTbd() {
        if (dateTbd != null) {
            return dateTbd.toString();
        }
        return null;
    }
    public boolean isCompleted() { return completed; }
    public boolean isTbd() { return tbd; }

    public void setId(String id) { this.id = id; }
    public void setDate(String date) { this.date = LocalDate.parse(date); }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDateTbd(String dateTbd) {
        if (dateTbd != null && !dateTbd.isBlank()) {
            this.dateTbd = LocalDate.parse(dateTbd);
        } else {
            this.dateTbd = null;
        }
    }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setTbd(boolean tbd) { this.tbd = tbd; }
}