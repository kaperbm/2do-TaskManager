package data.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import data.models.User;
import server.ServerConfig;

import java.io.*;
import java.util.*;

public class UserManager {

    private List<User> users = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public UserManager() throws IOException {
        load();
    }

    public boolean checkLogin(String email, String password) {
        System.out.println("checkLogin(): email=" + email + ", pass=" + password);
        System.out.println(users.size() + " User loadet");
        return users.stream().anyMatch(u -> u.getEmail().equals(email) && u.getPassword().equals(password));
    }

    public void load() throws IOException {
        users.clear();
        File file = new File(ServerConfig.USER_FILE);
        file.getParentFile().mkdirs();

        if (!file.exists() || file.length() == 0) {
           users = new ArrayList<>();
           return;
        }

        User[] userArray = mapper.readValue(file, User[].class);
        users = new ArrayList<>(Arrays.asList(userArray));
    }

    public String createNewUser(String name, String email, String password) throws IOException {
        load();
        File file = new File(ServerConfig.USER_FILE);
        file.getParentFile().mkdirs();

        if(users.isEmpty()){
            users.add(new User(name, email, password));
            String json = mapper.writerWithView(User.UserFileView.class).withDefaultPrettyPrinter().writeValueAsString(users);

            try (FileWriter fw = new FileWriter(file)) {
                fw.write(json);
                fw.flush();
            }
            return "OK NEWUSER";
        }

        for (User u : users) {
            if (u.getEmail().equals(email)) {
                return "THIS USER ALREADY EXISTS";
            }
        }

        users.add(new User(name, email, password));

        String json = mapper.writerWithView(User.TaskFileView.class).withDefaultPrettyPrinter().writeValueAsString(users);

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(json);
            fw.flush();
        }

        System.out.println("File written: " + file.length() + " bytes");

        return "OK NEWUSER";
    }
}
