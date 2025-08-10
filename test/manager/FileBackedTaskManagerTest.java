package manager;

import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void create() throws IOException {
        tempFile = File.createTempFile("test_tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void clean() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void canSaveEmptyManager() {
        manager.save();
        assertTrue(tempFile.exists());
    }

    @Test
    void canLoadEmptyFile() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertNotNull(loadedManager);
        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtask().isEmpty());
    }

    @Test
    void canSaveAndLoadTasksEpicsSubtasks() {
        Task task = new Task("Задача для файла", "Описание задачи", TaskStatus.NEW);
        Epic epic = new Epic("Эпик для файла", "Описание эпика");
        Subtask subtask = new Subtask(3,"Подзадача для файла", "Описание подзадачи", TaskStatus.NEW,
                2);

        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTasks().contains(task));
        assertEquals(1, loadedManager.getAllTasks().size());

        assertTrue(loadedManager.getAllEpics().contains(epic));
        assertEquals(1, loadedManager.getAllEpics().size());

        assertTrue(loadedManager.getAllSubtask().contains(subtask));
        assertEquals(1, loadedManager.getAllSubtask().size());
    }

    @Test
    void loadFromFileShouldRestoreEpicSubtasksRelationship() {
        Epic epic = new Epic(1,"Эпик", "Описание");
        Subtask subtask1 = new Subtask(2,"Подзадача1", "Описание1", TaskStatus.NEW, 1);
        Subtask subtask2 = new Subtask(3,"Подзадача2", "Описание2", TaskStatus.DONE, 1);

        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getEpicById(1).getSubtasks().size());
        assertTrue(loadedManager.getEpicById(1).getSubtasks().contains(subtask1.getId()));
        assertTrue(loadedManager.getEpicById(1).getSubtasks().contains(subtask2.getId()));
        assertEquals(TaskStatus.IN_PROGRESS, loadedManager.getEpicById(1).getStatus());
    }
}