package manager;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Objects;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;

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
            Arrays.stream(lines)
                    .skip(1)
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(CSVFormatter::fromString)
                    .filter(Objects::nonNull)
                    .forEach(task -> {
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
                    });

            manager.subtasks.values().forEach(subtask -> {
                Epic epic = manager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtasks(subtask.getId());
                    manager.updateEpic(epic);
                }
            });

            manager.epics.values().forEach(epic -> {
                epic.updateTime(manager.subtasks);
            });
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при извлечении данных из файла: " + e.getMessage());
        }
        return manager;
    }

   private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSVFormatter.getHeader());
            writer.newLine();

            for (Task task : getAllTasks()) {
                writer.write(CSVFormatter.toString(task));
                writer.newLine();
            }

            for (Epic epic : getAllEpics()) {
                writer.write(CSVFormatter.toString(epic));
                writer.newLine();
            }

            for (Subtask subtask : getAllSubtask()) {
                writer.write(CSVFormatter.toString(subtask));
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
