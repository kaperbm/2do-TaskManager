package server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * @version 1.0
 * @date 01-02-2026
 * Die Klasse startet den Server auf dem konfigurierten Port und wartet auf Client-Verbindungen.
 * Für jeden verbundenen Client wird ein eigener Thread zur Verarbeitung gestartet.
 */

public class ServerMain {

    public static void main(String[] args) {
        int port = ServerConfig.PORT;

        System.out.println("=== TaskManager Server startet auf Port " + port + " ===");

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client verbunden: " + client.getInetAddress());

                ClientController handler = new ClientController(client);
                handler.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}