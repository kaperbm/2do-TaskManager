package data.json;

import data.models.User;
import server.ServerConfig;

/**
 * @version 1.0
 * @date 01-02-2026
 * Repository zum Laden und Speichern von Benutzerdaten aus der JSON-Datei.
 */


public class UserRepository extends JsonFileRepository<User> {

    public UserRepository() {
        super(ServerConfig.USER_FILE, User[].class);
    }
}
