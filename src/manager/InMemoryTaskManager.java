package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

public class InMemoryTaskManager implements TaskManager {

    private HistoryManager historyManager = Managers.getDefaultHistory();
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private static int id = 0;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }
    private int generateId() {
        return ++id;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(id, task);
        return task;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        addTaskToHistory(task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
        return task;
    }


    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public Task deleteTaskById(int id) {
        return tasks.remove(id);
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(id, epic);
        return epic;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        addTaskToHistory(epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic.getId());
        } return epic;
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        ArrayList<Integer> subtasksIds = epic.getSubtasks();
        for (int subtask : new ArrayList<>(subtasksIds)) {
            subtasks.remove(subtask);
        }
            epics.remove(id);
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            ArrayList<Integer> subtasksIds = epic.getSubtasks();
            for (int subtask : new ArrayList<>(subtasksIds)) {
                subtasks.remove(subtask);
            }
        }
        epics.clear();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(generateId());
            subtasks.put(id, subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtasks(id);
            updateEpicStatus(epic.getId());
        }
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        addTaskToHistory(subtask);
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
        return subtask;
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtasks(id);
        updateEpicStatus(epic.getId());
        subtasks.remove(id);
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            updateEpicStatus(epic.getId());
        }
    }

    public ArrayList<Subtask> getSubtasksForEpicId(int epicId) {
        ArrayList<Subtask> subtasksForEpic = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasks()) {
                Subtask subtask = subtasks.get(subtaskId);
                subtasksForEpic.add(subtask);
            }
        } return subtasksForEpic;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        ArrayList<Integer> subtasksIds = epic.getSubtasks();
        if (subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean isAllDone = true;
        boolean isAllNew = true;

        for (Integer sub : subtasksIds) {
            Subtask subtask = subtasks.get(sub);
            if (subtask.getStatus() != TaskStatus.DONE) {
                isAllDone = false;
            }
            if (subtask.getStatus() != TaskStatus.NEW) {
                isAllNew = false;
            }
        }

        if (isAllDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (isAllNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

     private void addTaskToHistory(Task task) {
       historyManager.add(task);
    }
}
