package http;

import exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest extends HttpTaskServerTest {

    @Test
    public void canCreateTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        Task newTask = new Task("Новая задача", "Описание", TaskStatus.NEW, LocalDateTime.of(2025,
                Month.SEPTEMBER, 18, 17, 30), Duration.ofHours(3));
        String taskToJson = gson.toJson(newTask);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskToJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("Новая задача", manager.getAllTasks().getLast().getName());
    }

    @Test
    public void shouldReturnTimeConflictWhenTaskHasOverlap() throws IOException,
            InterruptedException {
        Task taskWithOverlap = new Task("Задача с пересечением", "Описание",
                TaskStatus.IN_PROGRESS, task.getStartTime(), Duration.ofMinutes(12));
        URI url = URI.create("http://localhost:8080/tasks");
        String taskToJson = gson.toJson(taskWithOverlap);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskToJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertTrue(response.body().contains("Задача пересекается с существующими"));
    }

    @Test
    public void canGetTaskById() throws IOException, InterruptedException {
        URI urlWithId = URI.create("http://localhost:8080/tasks?id=" + task.getId());
        request = HttpRequest.newBuilder().uri(urlWithId).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Задача 1"));
    }

    @Test
    public void shouldReturnNotFoundWhenWeGetNonExistentTask() throws IOException, InterruptedException {
        URI urlNonExistentTask = URI.create("http://localhost:8080/tasks?id=" + 900);
        request = HttpRequest.newBuilder().uri(urlNonExistentTask).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("Задача с таким id не найдена"));
    }

    @Test
    public void canGetAllTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Задача 1"));
    }

    @Test
    public void shouldUpdateTask() throws IOException, InterruptedException {
        Task taskForUpdate = new Task("Задача после обновления", task.getDescription(),
                task.getStatus(), LocalDateTime.of(2025, Month.SEPTEMBER, 8, 12, 0),
                Duration.ofMinutes(30));
        URI urlWithId = URI.create("http://localhost:8080/tasks?id=" + task.getId());
        String taskToJson = gson.toJson(taskForUpdate);
        request = HttpRequest.newBuilder().uri(urlWithId).POST(HttpRequest.BodyPublishers
                .ofString(taskToJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllTasks().size());
        assertEquals("Задача после обновления", manager.getAllTasks().getFirst().getName());
    }

    @Test
    public void shouldReturnNotFoundWhenWeUpdateNonExistentTask() throws IOException, InterruptedException,
            NotFoundException {
        Task taskForUpdate = new Task(task.getId(), "Задача после обновления", task.getDescription(),
                task.getStatus(), LocalDateTime.of(2025, Month.SEPTEMBER, 9, 12, 0),
                Duration.ofMinutes(30));
        URI urlNonExistentTask = URI.create("http://localhost:8080/tasks?id=" + 900);
        String taskToJson = gson.toJson(taskForUpdate);
        request = HttpRequest.newBuilder().uri(urlNonExistentTask).POST(HttpRequest.BodyPublishers
                .ofString(taskToJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());


        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("Задача с таким id не найдена"));
        assertEquals("Задача 1", manager.getTaskById(task.getId()).getName());
    }

    @Test
    public void canDeleteTask() throws IOException, InterruptedException {
        URI urlWithId = URI.create("http://localhost:8080/tasks?id=" + task.getId());
        request = HttpRequest.newBuilder().uri(urlWithId).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertFalse(manager.getAllTasks().contains(task));
    }

    @Test
    public void shouldReturnNotFoundWhenDeleteNonExistentTask() throws IOException, InterruptedException {
        URI urlNonExistentTask = URI.create("http://localhost:8080/tasks?id=" + 900);
        request = HttpRequest.newBuilder().uri(urlNonExistentTask).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("Задача с таким id не найдена"));
        assertFalse(manager.getAllTasks().isEmpty());
    }
}
