package data.json;

import java.io.IOException;
import java.util.List;

/**
 * @version 1.0
 * @date 01-02-2026
 * Definiert Methoden zum Laden und Speichern von Daten.
 */


public interface SaveLoad<T> {

    List<T> loadAll() throws IOException;

    void saveAll(List<T> data) throws IOException;

}
