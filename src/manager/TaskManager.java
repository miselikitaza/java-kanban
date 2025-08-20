package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    List<Task> getAllTasks();

    List<Task> getPrioritizedTasks();

    Task getTaskById(int id);

    Task updateTask(Task task);

    void deleteAllTasks();

    void deleteTaskById(int id);

    Epic createEpic(Epic epic);

    List<Epic> getAllEpics();

    Epic getEpicById(int id);

    Epic updateEpic(Epic epic);

    void deleteEpicById(int id);

    void deleteAllEpics();

    Subtask createSubtask(Subtask subtask);

    List<Subtask> getAllSubtask();

    Subtask getSubtaskById(int id);

    Subtask updateSubtask(Subtask subtask);

    void deleteSubtaskById(int id);

    void deleteAllSubtasks();

    List<Task> getHistory();

}
