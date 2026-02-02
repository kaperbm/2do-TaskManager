package client;

import java.io.*;
import java.net.Socket;

/**
 * @version 1.0
 * @date 01-02-2026
 * Verwaltet die Netzwerkkommunikation mit dem Server
 * über eine persistente Socket-Verbindung.
 */

public class NetworkClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public NetworkClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        String greeting = in.readLine();
        System.out.println("Server Greeting: " + greeting);
    }

    /**
     * Sendet einen Befehl an den Server und wartet auf die Antwort.
     * @param command Befehl-String, der an den Server gesendet wird
     * @return Antwort-String vom Server
     */
    public synchronized String send(String command) throws IOException {
        System.out.println("Sende an Server: '" + command + "'");
        out.println(command);

        String response = in.readLine();
        System.out.println("Server Response: " + response);

        return response;
    }
}