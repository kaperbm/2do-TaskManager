package data.json;

import data.models.User;
import server.ServerConfig;


/**
 * @version 1.0
 * @date 01-02-2026
 * Repository zum Laden und Speichern von Aufgaben pro Benutzer aus der JSON-Datei.
 */


public class TaskRepository extends JsonFileRepository<User> {

    public TaskRepository() {
        super(ServerConfig.TASK_FILE, User[].class);
    }
}
