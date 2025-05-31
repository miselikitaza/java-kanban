package manager;

import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {

    Task createTask(Task task);

    ArrayList<Task> getAllTasks();

    Task getTaskById(int id);

    Task updateTask(Task task);

    void deleteAllTasks();

    Task deleteTaskById(int id);

}
