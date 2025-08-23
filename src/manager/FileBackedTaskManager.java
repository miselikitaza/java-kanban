package manager;

import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            if (!file.exists()) {
                throw new ManagerSaveException("Файл не существует: " + file.getAbsolutePath());
            }

            if (file.length() == 0) {
                return manager;
            }

            String content = Files.readString(file.toPath());
            String[] lines = content.split(System.lineSeparator());
            boolean inPrioritySection = false;
            String priorityLine = null;

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                if (line.equals(CSVFormatter.PRIORITY_HEADER)) {
                    inPrioritySection = true;
                    continue;
                }

                if (inPrioritySection) {
                    priorityLine = line;
                    break;
                } else if (i > 0) {
                    Task task = CSVFormatter.fromString(line);
                    if (task == null) continue;

                    if (task instanceof Epic) {
                        manager.epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        manager.subtasks.put(task.getId(), (Subtask) task);
                    } else {
                        manager.tasks.put(task.getId(), task);
                    }

                    if (task.getId() > manager.id) {
                        manager.id = task.getId();
                    }
                }
            }

            for (Subtask subtask : manager.subtasks.values()) {
                Epic epic = manager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtasks(subtask.getId());
                }
            }

            manager.epics.values().forEach(epic -> epic.updateTime(manager.subtasks));

            if (priorityLine != null && !priorityLine.trim().isEmpty()) {
                String[] priorityIds = priorityLine.split(",");
                for (String id : priorityIds) {
                    try {
                        int taskId = Integer.parseInt(id.trim());
                        Task task = manager.tasks.get(taskId);
                        if (task == null) {
                            task = manager.subtasks.get(taskId);
                        }
                        if (task != null && task.getStartTime() != null) {
                            manager.prioritizedTasks.add(task);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Некорректный ID приоритетной задачи: " + id);
                    }
                }
            }
} catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при извлечении данных из файла: " + e.getMessage());
        }
        return manager;
    }

   private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSVFormatter.getHeader());
            writer.newLine();

            Stream.concat(Stream.concat(getAllTasks().stream(), getAllEpics().stream()),
                            getAllSubtask().stream())
                    .map(CSVFormatter::toString)
                    .forEach(line -> {
                        try {
                            writer.write(line);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка при записи задачи");
                        }
                    });

            List<Task> prioritized = getPrioritizedTasks();
            if (!prioritized.isEmpty()) {
                writer.write(CSVFormatter.PRIORITY_HEADER);
                writer.newLine();

                String priorityIds = prioritized.stream()
                        .map(task -> String.valueOf(task.getId()))
                        .collect(Collectors.joining(","));

                writer.write(priorityIds);
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Произошла ошибка при сохранении данных в файл.");
        }
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updateTask = super.updateTask(task);
        save();
        return updateTask;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }
}
