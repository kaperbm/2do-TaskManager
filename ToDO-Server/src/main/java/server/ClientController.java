package server;
import data.manager.TaskManager;
import data.manager.UserManager;
import data.models.Task;
import java.io.*;
import java.net.Socket;

public class ClientController extends Thread {

    private final BufferedReader in;
    private final PrintWriter out;

    private final UserManager userManager = new UserManager();

    public ClientController(Socket socket) throws IOException {
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public ClientController(BufferedReader in, PrintWriter out) throws IOException {
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            out.println("CONNECTED");

            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("Request: " + request);
                handleRequest(request);
            }

        } catch (IOException e) {
            System.out.println("Client getrennt.");
        }
    }

    private void handleRequest(String req) {
        try {
            String[] parts = req.split(" ");
            TaskManager taskManager = new TaskManager(parts[1]);

            switch (parts[0]) {
                case "LOGIN":
                    String email = parts[1];
                    String pass  = parts[2];
                    if (userManager.checkLogin(email, pass)) {
                        out.println("OK LOGIN");
                    } else {
                        out.println("ERR LOGIN");
                    }
                    break;

                case "GET_TASKS":
                    out.println(taskManager.getAllTasksString());
                    break;

                case "ADD_TASK":
                    taskManager.addTask(parts[1], parts[2], parts[3]);
                    out.println("OK ADD");
                    break;
                case "ADD_DATETASK": {
                    if (parts.length < 3) {
                        out.println("ERR INVALID_PARAMS");
                        break;
                    }

                    String date = parts[1];
                    String title = parts[2];
                    String description = parts[3];

                    taskManager.addTask(title, description, date);
                    out.println("OK EVENT_ADDED");
                    break;
                }
                case "GET_TASKCOUNT": {
                    int count = taskManager.getTaskCount(parts[1]);
                    out.println(count);
                    break;
                }
                case "CREATE_ACCOUNT":{
                    if(parts.length < 4){
                        out.println("ERR INVALID_PARAMS");
                        break;
                    }

                    userManager.createNewUser(parts[1], parts[2], parts[3]);
                    out.println("OK ACCOUNT_CREATED");
                    break;
                }
                case "GET_DATETASK":

                    StringBuilder sb = new StringBuilder();
                    for (Task t : taskManager.getTasksWithDate()) {
                        sb.append(t.getDescription()).append(";").append(t.getDate()).append("\n");
                    }
                    out.println(sb);
                    break;
                default:
                    out.println("ERR UNKNOWN_COMMAND");
            }

        } catch (Exception e) {
            out.println("ERR BAD_REQUEST");
        }
    }
}