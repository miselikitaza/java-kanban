package http;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import tasks.Epic;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpTaskManagerEpicsTest extends HttpTaskServerTest {

    @Test
    public void canCreateEpic() throws IOException, InterruptedException {
        Epic newEpic = new Epic("Новый эпик", "Описание");
        URI url = URI.create("http://localhost:8080/epics");
        String epicToJson = gson.toJson(newEpic);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicToJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("Новый эпик", manager.getAllEpics().getLast().getName());
    }

    @Test
    public void canGetEpicById() throws IOException, InterruptedException {
        URI urlWithId = URI.create("http://localhost:8080/epics?id=" + epic.getId());
        request = HttpRequest.newBuilder().uri(urlWithId).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Эпик 1"));
    }

    @Test
    public void shouldReturnNotFoundWhenWeGetNonExistentEpic() throws IOException, InterruptedException {
        URI urlNonExistentEpic = URI.create("http://localhost:8080/epics?id=" + 990);
        request = HttpRequest.newBuilder().uri(urlNonExistentEpic).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("Эпик с таким id не найден"));
    }

    @Test
    public void canGetAllEpics() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Эпик 1"));
    }

    @Test
    public void canUpdateEpic() throws IOException, InterruptedException {
        Epic epicForUpdate = new Epic("Эпик после обновления", epic.getDescription());
        String epicToJson = gson.toJson(epicForUpdate);
        URI urlWithId = URI.create("http://localhost:8080/epics?id=" + epic.getId());
        request = HttpRequest.newBuilder().uri(urlWithId).POST(HttpRequest.BodyPublishers.ofString(epicToJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllEpics().size());
        assertEquals("Эпик после обновления", manager.getAllEpics().getFirst().getName());
    }

    @Test
    public void shouldReturnNotFoundWhenWeUpdateNonExistentEpic() throws IOException, InterruptedException {
        Epic epicForUpdate = new Epic("Эпик после обновления", epic.getDescription());
        String epicToJson = gson.toJson(epicForUpdate);
        URI urlNonExistentEpic = URI.create("http://localhost:8080/epics?id=" + 900);
        request = HttpRequest.newBuilder().uri(urlNonExistentEpic).POST(HttpRequest.BodyPublishers.ofString(epicToJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("Эпик с таким id не найден"));
        assertEquals("Эпик 1", manager.getAllEpics().getFirst().getName());
    }

    @Test
    public void canDeleteEpic() throws IOException, InterruptedException {
        URI urlWithId = URI.create("http://localhost:8080/epics?id=" + epic.getId());
        request = HttpRequest.newBuilder().uri(urlWithId).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertFalse(manager.getAllEpics().contains(epic));
    }

    @Test
    public void shouldReturnNotFoundWhenWeDeleteNonExistentEpic() throws IOException, InterruptedException {
        URI urlNonExistentEpic = URI.create("http://localhost:8080/epics?id=" + 990);
        request = HttpRequest.newBuilder().uri(urlNonExistentEpic).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("Эпик с таким id не найден"));
    }
}
