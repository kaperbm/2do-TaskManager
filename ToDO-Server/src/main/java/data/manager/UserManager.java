package data.manager;

import data.models.User;
import data.json.UserRepository;
import java.io.IOException;
import java.util.List;

public class UserManager {

    private final UserRepository repo = new UserRepository();
    private List<User> users;

    public UserManager() throws IOException {
        this.users = repo.loadAll();
    }

    public boolean checkLogin(String email, String password) {
        return users.stream()
                .anyMatch(u ->
                        u.getEmail().equals(email) &&
                                u.getPassword().equals(password)
                );
    }

    public boolean createNewUser(String name, String email, String password) throws IOException {
        users = repo.loadAll();

        if (users.stream().anyMatch(u -> u.getEmail().equals(email))) {
            return false;
        }

        users.add(new User(name, email, password));
        repo.saveAll(users);
        return true;
    }

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
