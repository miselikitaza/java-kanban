package manager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        if (checkTaskOverlap(task)) {
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
        }
        return task;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        tasks.values().forEach(historyManager::add);
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
        if (checkTaskOverlap(task)) {
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
        }
        return task;
    }

    @Override
    public void deleteAllTasks() {
        tasks.values().stream()
                .filter(task -> task.getStartTime() != null)
                .forEach(prioritizedTasks::remove);

        tasks.keySet().forEach(historyManager::remove);
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
        if (checkTaskOverlap(epic)) {
            if (!epics.containsKey(epic.getId())) {
                if (epic.getId() == 0) {
                    epic.setId(generateId());
                    epics.put(id, epic);
                } else {
                    epics.put(epic.getId(), epic);
                }
            }
        } else {
            updateEpic(epic);
        }
        return epic;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        epics.values().forEach(historyManager::add);
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
        if (checkTaskOverlap(epic)) {
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
        subtasks.values().stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .forEach(prioritizedTasks::remove);

        subtasks.values().stream()
                .map(Subtask::getId)
                .forEach(historyManager::remove);

        epics.values().stream()
                .filter(epic -> epic.getStartTime() != null)
                .forEach(prioritizedTasks::remove);

        epics.values().stream()
                .map(Epic::getId)
                .forEach(historyManager::remove);

        subtasks.clear();
        epics.clear();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (checkTaskOverlap(subtask)) {
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
        }
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        subtasks.values().forEach(historyManager::add);
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
        if (checkTaskOverlap(subtask)) {
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
        subtasks.values().stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .forEach(prioritizedTasks::remove);

        subtasks.values().stream()
                .map(Subtask::getId)
                .forEach(historyManager::remove);

        subtasks.clear();

        epics.values().forEach(epic -> {
            epic.getSubtasks().clear();
            updateEpicStatus(epic.getId());
            epic.updateTime(subtasks);
            prioritizedTasks.remove(epic);
        });
    }

    public ArrayList<Subtask> getSubtasksForEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return epic.getSubtasks().stream()
                    .map(subtasks::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return new ArrayList<>();
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

        boolean isAllDone = subtasksIds.stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);

        boolean isAllNew = subtasksIds.stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .allMatch(subtask -> subtask.getStatus() == TaskStatus.NEW);

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

    public boolean isOverlapping(Task task1, Task task2) {
        if (task1 == null || task2 == null || task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    private boolean checkTaskOverlap(Task newTask) {
        if (newTask == null || newTask.getStartTime() == null) {
            return true;
        }

        return prioritizedTasks.stream()
                .filter(task -> task.getId() != newTask.getId())
                .noneMatch(task -> isOverlapping(task, newTask));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}