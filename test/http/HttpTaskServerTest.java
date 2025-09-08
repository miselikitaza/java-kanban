package http;

import com.google.gson.Gson;
import exceptions.NotFoundException;
import exceptions.TimeConflictException;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class HttpTaskServerTest {

    TaskManager manager;
    Task task;
    Epic epic;
    Subtask subtask;
    Gson gson;
    HttpClient client;
    HttpTaskServer server;

    protected static HttpRequest request;
    protected static HttpResponse<String> response;

    @BeforeEach
    public void create() throws TimeConflictException, IOException, NotFoundException {
        manager = new InMemoryTaskManager();
        gson = GsonAdapters.createGson();
        client = HttpClient.newHttpClient();
        server = new HttpTaskServer(manager);
        task = new Task("Задача 1", "Описание 1", TaskStatus.NEW, LocalDateTime.of(2025,
                Month.SEPTEMBER, 17, 17, 30), Duration.ofHours(3));
        manager.createTask(task);
        epic = new Epic("Эпик 1", "Описание 1");
        manager.createEpic(epic);
        subtask = new Subtask("Подзадача 1", "Описание 1", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, Month.OCTOBER, 1, 10,15),
                Duration.ofHours(2), epic.getId());
        manager.createSubtask(subtask);
        server.start();
    }

    @AfterEach
    public void stop() {
        server.stop();
    }
}