package client;

import java.io.*;
import java.net.Socket;

public class NetworkClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String send(String message) throws IOException {
        out.println(message);
        String line;
        do {
            line = in.readLine();
        } while (line != null && line.equals("CONNECTED"));

        return line;
    }

}