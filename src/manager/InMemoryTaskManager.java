package manager;

import java.util.*;

import history.HistoryManager;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

public class InMemoryTaskManager implements TaskManager {

    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::getId));
    protected int id = 0;

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
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
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
            Task oldTask = tasks.get(task.getId());
            if (oldTask.getStartTime() != null) {
                prioritizedTasks.remove(oldTask);
            }
            tasks.put(task.getId(), task);

            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
        return task;
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            if (task.getStartTime() != null) {
                prioritizedTasks.remove(task);
            }
        }
        for (Integer key : tasks.keySet()) {
            historyManager.remove(key);
        }
        tasks.clear();
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            if (task.getStartTime() != null) {
                prioritizedTasks.remove(task);
            }
            tasks.remove(id);
            historyManager.remove(id);
        }
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
            Epic oldEpic = epics.get(epic.getId());
            if (oldEpic.getStartTime() != null) {
                prioritizedTasks.remove(oldEpic);
            }
            epics.put(epic.getId(), epic);
            if (epic.getStartTime() != null) {
                prioritizedTasks.add(epic);
            }
            updateEpicStatus(epic.getId());
        }
        return epic;
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic.getStartTime() != null) {
            prioritizedTasks.remove(epic);
        }
        List<Integer> subtasksIds = epic.getSubtasks();
        for (int subtask : new ArrayList<>(subtasksIds)) {
            Subtask subtaskForDelete = subtasks.get(subtask);
            if (subtaskForDelete != null && subtaskForDelete.getStartTime() != null) {
                prioritizedTasks.remove(subtaskForDelete);
            }
            subtasks.remove(subtask);
            historyManager.remove(subtask);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllEpics() {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
            historyManager.remove(subtask.getId());
        }

        for (Epic epic : epics.values()) {
            prioritizedTasks.remove(epic);
            historyManager.remove(epic.getId());
        }
        subtasks.clear();
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
                if (subtask.getStartTime() != null) {
                    prioritizedTasks.add(subtask);
                }
                Epic epic = epics.get(subtask.getEpicId());
                epic.addSubtasks(subtask.getId());
                updateEpicStatus(epic.getId());
                epic.updateTime(subtasks);
                prioritizedTasks.remove(epic);
                if (epic.getStartTime() != null) {
                    prioritizedTasks.add(epic);
                }
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
            Subtask oldSubtask = subtasks.get(subtask.getId());
            if (oldSubtask.getStartTime() != null) {
                prioritizedTasks.remove(oldSubtask);
            }
            subtasks.put(subtask.getId(), subtask);
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
            updateEpicStatus(subtask.getEpicId());
            Epic epic = getEpicById(subtask.getEpicId());
            epic.updateTime(subtasks);
            prioritizedTasks.remove(epic);
            if (epic.getStartTime() != null) {
                prioritizedTasks.add(epic);
            }
        }
        return subtask;
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.remove(subtask);
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtasks(id);
        updateEpicStatus(epic.getId());
        subtasks.remove(id);
        epic.updateTime(subtasks);
        prioritizedTasks.remove(epic);
        if (epic.getStartTime() != null) {
            prioritizedTasks.add(epic);
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic.getId());
            epic.updateTime(subtasks);
            prioritizedTasks.remove(epic);
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}
