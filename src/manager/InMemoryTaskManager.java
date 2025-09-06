package manager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import exceptions.NotFoundException;
import exceptions.TimeConflictException;
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
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));
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
    public Task createTask(Task task) throws TimeConflictException {
        if (!checkTaskOverlap(task)) {
            throw new TimeConflictException("Задача пересекается с существующими");
        }
            if (!tasks.containsKey(task.getId())) {
                task.setId(generateId());
                tasks.put(id, task);
                if (task.getStartTime() != null) {
                    prioritizedTasks.add(task);
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
    public Task getTaskById(int id) throws NotFoundException {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача с таким id не найдена");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Task updateTask(Task task) throws NotFoundException, TimeConflictException {
            if (!tasks.containsKey(task.getId())) {
                throw new NotFoundException("Задача с таким id не найдена");
                }
            if (!checkTaskOverlap(task)) {
                throw new TimeConflictException("Задача пересекается с существующими");
            }
            Task oldTask = tasks.get(task.getId());
            tasks.put(task.getId(), task);
            if (oldTask.getStartTime() != null) {
                prioritizedTasks.remove(oldTask);
            }
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
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
    public void deleteTaskById(int id) throws NotFoundException {
        if (!tasks.containsKey(id)) {
            throw new NotFoundException("Задача с таким id не найдена");
        }
        Task task = tasks.get(id);
        tasks.remove(id);
        historyManager.remove(id);
        if (task.getStartTime() != null) {
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            epic.setId(generateId());
            epics.put(id, epic);
        }
        return epic;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        epics.values().forEach(historyManager::add);
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpicById(int id) throws NotFoundException {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Эпик с таким id не найден");
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) throws NotFoundException {
        if (!epics.containsKey(epic.getId())) {
            throw new NotFoundException("Эпик с таким id не найден");
        }
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
        //epic.updateTime(subtasks);
        return epic;
    }

    @Override
    public void deleteEpicById(int id) throws NotFoundException {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Эпик с таким id не найден");
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
        subtasks.values().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        });

        epics.values().forEach(epic -> historyManager.remove(epic.getId()));
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) throws NotFoundException, TimeConflictException {
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new NotFoundException("Эпик с таким id не найден");
        }

        if (!checkTaskOverlap(subtask)) {
            throw new TimeConflictException("Подзадача пересекается с существующими");
        }
        if (!subtasks.containsKey(subtask.getId())) {
            subtask.setId(generateId());
            subtasks.put(subtask.getId(), subtask);
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtask((subtask.getId()));
            updateEpicStatus(epic.getId());
            epic.updateTime(subtasks);
        }
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        subtasks.values().forEach(historyManager::add);
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtaskById(int id) throws NotFoundException {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с таким id не обнаружена");
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) throws NotFoundException, TimeConflictException {
        if (!checkTaskOverlap(subtask)) {
            throw new TimeConflictException("Подзадача пересекается по времени с существующей задачей");
        }
            if (!subtasks.containsKey(subtask.getId())) {
                throw new NotFoundException("Подзадача с таким id не найдена");
            }
            if (!epics.containsKey(subtask.getEpicId())) {
                throw new NotFoundException("Эпик с id " + subtask.getEpicId() + " не найден");
            }

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
        return subtask;
    }

    @Override
    public void deleteSubtaskById(int id) throws NotFoundException {
        if (!subtasks.containsKey(id)) {
            throw new NotFoundException("Подзадача с id " + id + " не найдена");
        }
        Subtask subtask = subtasks.get(id);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.remove(subtask);
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtasks(id);
        updateEpicStatus(epic.getId());
        subtasks.remove(id);
        epic.updateTime(subtasks);
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
        });
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected List<Subtask> getSubtasksByEpicId(int epicId) {
        return subtasks.values().stream()
                .filter(subtask -> subtask != null && subtask.getEpicId() == epicId)
                .collect(Collectors.toList());
    }

    private boolean isOverlapping(Task task1, Task task2) {
        if (task1 == null || task2 == null || task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !start1.isAfter(end2) && !start2.isAfter(end1);
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

    private boolean checkTaskOverlap(Task newTask) {
        if (newTask == null || newTask.getStartTime() == null) {
            return true;
        }

        return prioritizedTasks.stream()
                .filter(task -> task.getId() != newTask.getId())
                .noneMatch(task -> isOverlapping(task, newTask));
    }
}