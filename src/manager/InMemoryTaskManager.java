package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import history.HistoryManager;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private int id = 0;

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
        if (!tasks.containsKey(task.getId())) {
            if (task.getId() == 0) {
                task.setId(generateId());
                tasks.put(id, task);
            } else {
                tasks.put(task.getId(), task);
            }
        } else {
            updateTask(task);
        }
        return task;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.add(task);
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
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
        for (Integer key : tasks.keySet()) {
            historyManager.remove(key);
        }
        tasks.clear();
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            if (epic.getId() == 0) {
                epic.setId(generateId());
                epics.put(id, epic);
            } else {
                epics.put(epic.getId(), epic);
            }
        } else {
            updateEpic(epic);
        }
        return epic;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.add(epic);
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic.getId());
        }
        return epic;
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        List<Integer> subtasksIds = epic.getSubtasks();
        for (int subtask : new ArrayList<>(subtasksIds)) {
            subtasks.remove(subtask);
            historyManager.remove(subtask);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            List<Integer> subtasksIds = epic.getSubtasks();
            for (int subtask : new ArrayList<>(subtasksIds)) {
                subtasks.remove(subtask);
                historyManager.remove(subtask);
            }
        }
        for (Integer key : epics.keySet()) {
            historyManager.remove(key);
        }
        epics.clear();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            if (epics.containsKey(subtask.getEpicId())) {
                if (subtask.getId() == 0) {
                    subtask.setId(generateId());
                    subtasks.put(id, subtask);
                } else {
                    subtasks.put(subtask.getId(), subtask);
                }
                Epic epic = epics.get(subtask.getEpicId());
                epic.addSubtasks(subtask.getId());
                updateEpicStatus(epic.getId());
            }
        }
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.add(subtask);
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
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
        historyManager.remove(id);
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer key : subtasks.keySet()) {
            historyManager.remove(key);
        }
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
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
        }
        return subtasksForEpic;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        List<Integer> subtasksIds = epic.getSubtasks();
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

}
