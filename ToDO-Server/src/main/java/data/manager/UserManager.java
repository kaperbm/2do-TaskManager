package data.manager;

import data.models.User;
import data.json.UserRepository;
import java.io.IOException;
import java.util.List;

/**
 * @version 1.0
 * @date 01-02-2026
 * Verwaltet Benutzeranmeldung, Registrierung und Passwortänderung.
 */


public class UserManager {

    private final UserRepository repo = new UserRepository();
    private List<User> users;

    /**
     * Konstruktor - Lädt alle Benutzer aus dem Repository.
     * @throws IOException wenn beim Laden der Benutzerdaten ein Fehler auftritt
     */
    public UserManager() throws IOException {
        this.users = repo.loadAll();
    }


    /**
     * Überprüft die Login-Daten eines Benutzers.
     * @param email E-Mail-Adresse des Benutzers
     * @param password Passwort des Benutzers
     * @return true wenn E-Mail und Passwort übereinstimmen, sonst false
     */
    public boolean checkLogin(String email, String password) {
        return users.stream()
                .anyMatch(u ->
                        u.getEmail().equals(email) &&
                                u.getPassword().equals(password)
                );
    }

    /**
     * Erstellt einen neuen Benutzer im System.
     * @param name Name des neuen Benutzers
     * @param email E-Mail-Adresse des neuen Benutzers
     * @param password Passwort des neuen Benutzers
     * @return true wenn Benutzer erfolgreich erstellt wurde, false wenn E-Mail bereits existiert
     */
    public boolean createNewUser(String name, String email, String password) throws IOException {
        users = repo.loadAll();

        if (users.stream().anyMatch(u -> u.getEmail().equals(email))) {
            return false;
        }

        users.add(new User(name, email, password));
        repo.saveAll(users);
        return true;
    }

    /**
     * Ändert das Passwort eines bestehenden Benutzers.
     * @param email E-Mail-Adresse des Benutzers
     * @param oldPassword Aktuelles Passwort zur Verifikation
     * @param newPassword Neues Passwort
     * @return true wenn Passwort erfolgreich geändert wurde,
     * @return false wenn Benutzer nicht gefunden oder altes Passwort falsch war
     */
    public boolean changePassword(String email, String oldPassword, String newPassword) throws IOException {
        repo.loadAll();

        for (User u : users) {
            if (u.getEmail().equals(email)) {

                if (!u.getPassword().equals(oldPassword)) {
                    return false;
                }
                u.setPassword(newPassword);
                repo.saveAll(users);
                return true;
            }
        }
        return false;
    }
}
