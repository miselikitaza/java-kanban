package http;

import exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import tasks.Subtask;
import tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class HttpTaskManagerSubtasksTest extends HttpTaskServerTest {

    @Test
    public void canCreateSubtask() throws IOException, InterruptedException {
        Subtask newSubtask = new Subtask("Новая подзадача", "Описание", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.NOVEMBER, 11, 12, 5),
                Duration.ofMinutes(30), epic.getId());
        URI url = URI.create("http://localhost:8080/subtasks");
        String subtaskToJson = gson.toJson(newSubtask);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskToJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("Новая подзадача", manager.getAllSubtask().getLast().getName());
    }

    @Test
    public void shouldReturnTimeConflictWhenSubtaskHasOverlap() throws IOException,
            InterruptedException {
        Subtask subtaskWithOverlap = new Subtask("Подзадача с пересечением", "Описание", TaskStatus.NEW,
                subtask.getStartTime(), Duration.ofMinutes(12), epic.getId());
        URI url = URI.create("http://localhost:8080/subtasks");
        String subtaskToJson = gson.toJson(subtaskWithOverlap);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskToJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertTrue(response.body().contains("Подзадача пересекается с существующими"));
    }

    @Test
    public void canGetSubtaskById() throws IOException, InterruptedException {
        URI urlWithId = URI.create("http://localhost:8080/subtasks?id=" + subtask.getId());
        request = HttpRequest.newBuilder().uri(urlWithId).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Подзадача 1"));
    }

    @Test
    public void shouldReturnNotFoundWhenWeGetNonExistentSubtask() throws IOException, InterruptedException {
        URI urlNonExistentSubtask = URI.create("http://localhost:8080/subtasks?id=" + 900);
        request = HttpRequest.newBuilder().uri(urlNonExistentSubtask).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("Подзадача с таким id не обнаружена"));
    }

    @Test
    public void canGetAllTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Подзадача 1"));
    }

    @Test
    public void canUpdateSubtask() throws IOException, InterruptedException {
        Subtask subtaskForUpdate = new Subtask("Подзадача после обновления", "Описание",
                TaskStatus.NEW, LocalDateTime.of(2025, Month.SEPTEMBER, 7, 14,0),
                Duration.ofHours(1), epic.getId());
        URI urlWithId = URI.create("http://localhost:8080/subtasks?id=" + subtask.getId());
        String taskToJson = gson.toJson(subtaskForUpdate);
        request = HttpRequest.newBuilder().uri(urlWithId).POST(HttpRequest.BodyPublishers
                .ofString(taskToJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllSubtask().size());
        assertEquals("Подзадача после обновления", manager.getAllSubtask().getFirst().getName());
    }

    @Test
    public void shouldReturnNotFoundWhenWeUpdateNonExistentSubtask() throws IOException, InterruptedException,
            NotFoundException {
        Subtask subtaskForUpdate = new Subtask("Подзадача после обновления", subtask.getDescription(),
                subtask.getStatus(), LocalDateTime.of(2025, Month.SEPTEMBER, 8, 12, 10),
                Duration.ofMinutes(30), epic.getId());
        URI urlNonExistentSubtask = URI.create("http://localhost:8080/subtasks?id=" + 900);
        String taskToJson = gson.toJson(subtaskForUpdate);
        request = HttpRequest.newBuilder().uri(urlNonExistentSubtask).POST(HttpRequest.BodyPublishers
                .ofString(taskToJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue((response.body().contains("Подзадача с таким id не найдена")));
        assertEquals("Подзадача 1", manager.getSubtaskById(subtask.getId()).getName());
    }

    @Test
    public void shouldReturnNotFoundWhenWeCreateSubtaskWithNonExistentEpic() throws IOException, InterruptedException {
        Subtask subtaskWithInvalidEpicId = new Subtask("Подзадача c несуществующим эпиком", "Описание",
                TaskStatus.NEW, LocalDateTime.of(2025, Month.SEPTEMBER, 21, 18,0),
                Duration.ofHours(1), 900);
        URI url = URI.create("http://localhost:8080/subtasks");
        String subtaskToJson = gson.toJson(subtaskWithInvalidEpicId);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskToJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("Эпик с таким id не найден"));
    }
}
