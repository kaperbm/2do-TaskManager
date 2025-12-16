package client;

import java.io.*;
import java.net.Socket;

public class NetworkClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public NetworkClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public String send(String msg) throws IOException {
        out.println(msg);
        return in.readLine();
    }
}