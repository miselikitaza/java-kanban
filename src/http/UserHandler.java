package http;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;
import java.io.IOException;
import java.util.List;

public class UserHandler extends BaseHttpHandler {

    private static TaskManager manager;

    public UserHandler(TaskManager manager) {
        UserHandler.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (!method.equals("GET")) {
            throw new IOException("Неверный метод");
        }

        switch (path) {
            case "/history":
                List<Task> history = manager.getHistory();
                sendText(exchange, gson.toJson(history), 200);
                break;
            case "/prioritized":
                List<Task> prioritizedTasks = manager.getPrioritizedTasks();
                sendText(exchange, gson.toJson(prioritizedTasks), 200);
                break;
            default:
                sendText(exchange, "Неверный путь" + path, 404);
        }

    }
}
