import com.fasterxml.jackson.databind.ObjectMapper;
import data.models.Task;
import server.ClientController;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.time.LocalDate;

class ClientControllerTest {

    @Test
    void testClientHandler() throws IOException {
        String request = "ADD_TASK " + LocalDate.now() + " " + "testtask";
        BufferedReader in = new BufferedReader(new StringReader(request));
        StringWriter stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);

        ClientController handler = new ClientController(in, out);
        handler.start();

        String response = stringWriter.toString();
        System.out.println("Response: " + response);
    }
}