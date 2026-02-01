package client;

/**
 * @version 1.0
 * @date 01-02-2026
 * Speichert den aktuellen Sitzungszustand der Anwendung
 * wie eingeloggten Benutzer, Serververbindung und Dark-Mode.
 */

public class Session {
    public static String loggedInUserEmail;
    public static NetworkClient client;
    public static boolean darkMode = false;
}