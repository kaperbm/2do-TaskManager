package data.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import data.models.User;
import server.ServerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @version 1.0
 * @date 01-02-2026
 * Abstraktes Repository zum Laden und Speichern von Daten in einer JSON-Datei.
 */


public abstract class JsonFileRepository<T> implements SaveLoad<T> {

    protected final ObjectMapper mapper = new ObjectMapper();
    protected final File file;
    protected final Class<T[]> arrayType;

    protected JsonFileRepository(String filePath, Class<T[]> arrayType) {
        this.file = new File(filePath);
        this.arrayType = arrayType;
        this.file.getParentFile().mkdirs();
    }


    @Override
    public List<T> loadAll() throws IOException {
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        T[] data = mapper.readValue(file, arrayType);
        return new ArrayList<>(Arrays.asList(data));
    }

    @Override
    public void saveAll(List<T> data) throws IOException {

        if(data instanceof User ){
            String json = mapper.writerWithView(User.TaskFileView.class)
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(data);

            try (FileWriter fw = new FileWriter(file)) {
                fw.write(json);
                fw.flush();
                System.out.println("File written: " + file.length() + " bytes");
            }
        }else{
            String json = mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(data);

            try (FileWriter fw = new FileWriter(file)) {
                fw.write(json);
                fw.flush();
            }
        }
    }

    public List<User> checkIfUserExists() throws IOException {
        List<User> userList = new ArrayList<>();

        if (file.exists() && file.length() > 0) {
            User[] userArray = mapper.readValue(file, User[].class);
            userList = new ArrayList<>(Arrays.asList(userArray));
        }

        return userList;
    }
}
