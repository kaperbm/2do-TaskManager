package data.manager;

import data.models.User;

import java.io.*;
import java.util.*;

public class UserManager {

    private List<User> users = new ArrayList<>();

    public UserManager() {
        load();
    }

    public boolean checkLogin(String email, String password) {
        System.out.println("checkLogin(): email=" + email + ", pass=" + password);
        System.out.println(users.size() + " User geladen");
        return users.stream()
                .anyMatch(u -> u.getEmail().equals(email) && u.getPassword().equals(password));
    }

    public void load() {
        users.clear();
        File f = new File("resources/main/users.csv");
        System.out.println("USER_FILE = " + f.getPath());
        System.out.println("Absoluter Pfad = " + f.getAbsolutePath());
        System.out.println("Existiert Datei? " + f.exists());

        if (!f.exists()) {
            System.out.println("users.csv existiert NICHT -> keine User geladen.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(";");
                if (p.length < 3) continue;
                users.add(new User(p[0], p[1], p[2]));
            }
            System.out.println("load(): " + users.size() + "User aus Datei geladen.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
