package manager;

import exceptions.NotFoundException;
import exceptions.TimeConflictException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    Task createTask(Task task) throws TimeConflictException;

    List<Task> getAllTasks();

    Task getTaskById(int id) throws NotFoundException;

    Task updateTask(Task task) throws NotFoundException, TimeConflictException;

    void deleteAllTasks();

    void deleteTaskById(int id) throws NotFoundException;

    Epic createEpic(Epic epic);

    List<Epic> getAllEpics();

    Epic getEpicById(int id) throws NotFoundException;

    Epic updateEpic(Epic epic) throws NotFoundException;

    void deleteEpicById(int id) throws NotFoundException;

    void deleteAllEpics();

    Subtask createSubtask(Subtask subtask) throws TimeConflictException, NotFoundException;

    List<Subtask> getAllSubtask();

    Subtask getSubtaskById(int id) throws NotFoundException;

    Subtask updateSubtask(Subtask subtask) throws NotFoundException, TimeConflictException;

    void deleteSubtaskById(int id) throws NotFoundException;

    void deleteAllSubtasks();

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
