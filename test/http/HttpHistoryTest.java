package http;

import exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpHistoryTest extends HttpTaskServerTest {

    @Test
    public void canGetHistory() throws NotFoundException, IOException, InterruptedException {
        manager.getTaskById(task.getId());
        manager.getSubtaskById(subtask.getId());
        URI url = URI.create("http://localhost:8080/history");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Задача 1"));
        assertTrue(response.body().contains("Подзадача 1"));
    }
}
