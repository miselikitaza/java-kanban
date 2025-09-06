package http;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final TaskManager manager;
    private final HttpServer server;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        setupHandlers();
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer taskServer = new HttpTaskServer();
        taskServer.start();
    }

    private void setupHandlers() {
        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/epics", new TaskHandler(manager));
        server.createContext("/subtasks", new TaskHandler(manager));
        server.createContext("/prioritized", new UserHandler(manager));
        server.createContext("/history", new UserHandler(manager));
    }

    public TaskManager getManager() {
        return manager;
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        server.stop(0);
    }
}
