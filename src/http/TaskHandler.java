package http;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TimeConflictException;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {

    private final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            String query = exchange.getRequestURI().getQuery();

            int id = -1;
            if (query != null && query.contains("id=")) {
                id = getFromQuery(query);
            }

            switch (path) {
                case "/tasks":
                    handleTaskOperations(exchange, method, id);
                    break;
                case "/subtasks":
                    handleSubtaskOperations(exchange, method, id);
                    break;
                case "/epics":
                    handleEpicOperations(exchange, method, id);
                    break;
                default:
                    sendText(exchange, "Неверный путь" + path, 404);
            }
        } catch (NotFoundException e) {
            notFound(exchange, "Объект не существует: " + e.getMessage());
        } catch (TimeConflictException e) {
            sendText(exchange, "Обнаружено пересечение: " + e.getMessage(), 406);
        }
    }

    private Integer getFromQuery(String query) {
        if (query == null || query.isEmpty()) {
            return -1;
        }
        return Integer.parseInt(query.substring(3));
    }

    private void handleTaskOperations(HttpExchange exchange, String method, Integer id) throws NotFoundException,
            IOException, TimeConflictException {

        switch (method) {
            case "GET":
                if (id != -1) {
                    Task task = manager.getTaskById(id);
                    sendText(exchange, gson.toJson(task), 200);
                } else {
                    List<Task> tasks = manager.getAllTasks();
                    sendText(exchange, gson.toJson(tasks), 200);
                }
                break;

            case "POST":
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(body, Task.class);

                if (id != -1) {
                    task.setId(id);
                    manager.updateTask(task);
                    sendText(exchange, "Задача обновлена", 201);
                } else {
                    manager.createTask(task);
                    sendText(exchange, "Задача создана", 201);
                }
                break;

            case "DELETE":
                if (id != -1) {
                    manager.deleteTaskById(id);
                    sendText(exchange, "Задача удалена", 200);
                } else {
                    manager.deleteAllTasks();
                    sendText(exchange, "Все задачи удалены", 200);
                }
                break;

            default:
                sendText(exchange, "Указан неверный метод", 404);
        }
    }


    private void handleSubtaskOperations(HttpExchange exchange, String method, Integer id) throws IOException, NotFoundException, TimeConflictException {
try {
    switch (method) {
        case "GET":
            if (id != -1) {
                Subtask subtask = manager.getSubtaskById(id);
                sendText(exchange, gson.toJson(subtask), 200);
            } else {
                List<Subtask> subtasks = manager.getAllSubtask();
                sendText(exchange, gson.toJson(subtasks), 200);
            }
            break;

        case "POST":
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(body, Subtask.class);

            if (id != -1) {
                subtask.setId(id);
                manager.updateSubtask(subtask);
                sendText(exchange, "Подзадача обновлена", 201);
            } else {
                manager.createSubtask(subtask);
                sendText(exchange, "Подзадача создана", 201);
            }
            break;

        case "DELETE":
            if (id != -1) {
                manager.deleteSubtaskById(id);
                sendText(exchange, "Подзадача удалена", 200);
            } else {
                manager.deleteAllSubtasks();
                sendText(exchange, "Все подзадачи удалены", 200);
            }
            break;

        default:
            sendText(exchange, "Такого метода нет", 404);
    }
} catch (NotFoundException e) {
    sendText(exchange, e.getMessage(), 404);
} catch (TimeConflictException e) {
    sendText(exchange, e.getMessage(), 406);
} catch (Exception e) {
    sendText(exchange, "Внутренняя ошибка сервера: " + e.getMessage(), 500);
}
    }


    private void handleEpicOperations(HttpExchange exchange, String method, Integer id) throws NotFoundException,
            IOException {

        switch (method) {
            case "GET":
                if (id != -1) {
                    Epic epic = manager.getEpicById(id);
                    sendText(exchange, gson.toJson(epic), 200);
                } else {
                    List<Epic> epics = manager.getAllEpics();
                    sendText(exchange, gson.toJson(epics), 200);
                }
                break;

            case "POST":
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(body, Epic.class);
                if (id != -1) {
                    epic.setId(id);
                    manager.updateEpic(epic);
                    sendText(exchange, "Эпик обновлен", 201);
                } else {
                    manager.createEpic(epic);
                    sendText(exchange, "Эпик создан", 201);
                }
                break;

            case "DELETE":
                if (id != -1) {
                    manager.deleteEpicById(id);
                    sendText(exchange, "Эпик удален", 200);
                } else {
                    manager.deleteAllEpics();
                    sendText(exchange, "Все эпики удалены", 200);
                }
                break;
        }
    }
}
