package data.json;

import data.models.User;

import java.io.IOException;
import java.util.List;

public interface SaveLoad<T> {

    List<T> loadAll() throws IOException;

    void saveAll(List<T> data) throws IOException;

}
