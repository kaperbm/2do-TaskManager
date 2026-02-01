package server;
import data.manager.TaskManager;
import data.manager.UserManager;
import java.io.*;
import java.net.Socket;

/**
 * @version 1.0
 * @date 01-02-2026
 * Verarbeitet die Kommunikation mit einem einzelnen Client.
 * Liest Client-Anfragen, wertet Befehle aus und sendet passende Antworten zurück.
 */


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

    /**
     * Thread-Hauptmethode - Verarbeitet Client-Anfragen in einer Schleife.
     * Sendet Verbindungsbestätigung und liest kontinuierlich Anfragen vom Client.
     */
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


    /**
     * Verarbeitet eine einzelne Client-Anfrage und sendet passende Antwort.
     * Unterstützt Befehle: LOGIN, CREATE_ACCOUNT, ADD_TASK, GET_TASKS, GET_TASKCOUNT, SAVE_TASKS, CHANGE_PASSWORD
     * @param req Anfrage-String vom Client
     */

    private void handleRequest(String req) {
        try {
            String[] parts = req.split(" ");
            TaskManager taskManager = new TaskManager(parts[1]);

            switch (parts[0]) {
                case "LOGIN": {
                    String email = parts[1];
                    String pass = parts[2];
                    if (userManager.checkLogin(email, pass)) {
                        out.println("OK LOGIN");
                    } else {
                        out.println("ERR LOGIN");
                    }
                    break;
                }

                case "CREATE_ACCOUNT":{
                    if(parts.length < 4){
                        out.println("ERR INVALID_PARAMS");
                        break;
                    }

                    if(userManager.createNewUser(parts[1], parts[2], parts[3]) == true){
                        out.println("OK ACCOUNT CREATED");
                    } else {
                        out.println("ERR ACCOUNT EXISTS");
                    }
                    break;
                }

                case "ADD_TASK":{

                    taskManager.addTask(parts[1], parts[2], parts[3],parts[4]);
                    out.println("OK ADD");
                    break;
                }

                case "GET_TASKS":{
                    if(parts.length < 3){
                        out.println(taskManager.getUserTasks(parts[1]));
                        break;
                    }
                    out.println(taskManager.getUserTasks(parts[1], Integer.parseInt(parts[2])));
                    break;
                }

                case "GET_TASKCOUNT": {
                    out.println(taskManager.getTaskCounts(parts[1]));
                    break;
                }

                case "SAVE_TASKS": {
                    taskManager.saveTask(parts[1], parts[2]);
                    out.println("OK");
                    break;
                }
                case "CHANGE_PASSWORD": {
                    if (parts.length < 4) {
                        out.println("ERR INVALID_PARAMS");
                        break;
                    }

                    String email = parts[1];
                    String oldPw = parts[2];
                    String newPw = parts[3];

                    boolean ok = userManager.changePassword(email, oldPw, newPw);
                    out.println(ok ? "OK PASSWORD_CHANGED" : "ERR PASSWORD_CHANGE");
                    break;
                }
            }

        } catch (Exception e) {
            out.println("ERR BAD_REQUEST");
        }
    }
}