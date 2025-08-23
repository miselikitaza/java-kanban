package manager;

import org.junit.jupiter.api.*;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File tempFile;

    @Override
    @BeforeEach
    public void create() throws IOException {
        tempFile = File.createTempFile("test_tasks", ".csv");
        super.create();
    }

    @Override
    public FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void clean() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void canLoadEmptyFile() throws IOException {
        Files.write(tempFile.toPath(), new byte[0]);
        assertTrue(tempFile.exists(), "Файл должен существовать");
        assertEquals(0, tempFile.length(), "Файл должен быть пустым");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertNotNull(loadedManager);
        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtask().isEmpty());
    }

    @Test
    void canSaveAndLoadTasksEpicsSubtasks() {
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
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getEpicById(epic.getId()).getSubtasks().size());
        assertTrue(loadedManager.getEpicById(epic.getId()).getSubtasks().contains(subtask.getId()));
        assertEquals(TaskStatus.NEW, loadedManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void saveCorruptedFileShouldThrowManagerSaveException() throws IOException {
        File fileForException = new File("/file/path/that/does/not/exist/test.csv");
        FileBackedTaskManager corruptedManager = new FileBackedTaskManager(fileForException);
        assertThrows(ManagerSaveException.class, () -> {
            corruptedManager.createTask(new tasks.Task("Имя", "Описание", TaskStatus.IN_PROGRESS,
                    LocalDateTime.now(), Duration.ofHours(3)));
        });
        fileForException.delete();
    }

    @Test
    void loadFromCorruptedFileShouldThrowManagerSaveException() throws IOException {
        File fileForException = new File("/file/path/that/does/not/exist/test.csv");
        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(fileForException);
        });
    }

    @Test
    void shouldSaveAndUploadPrioritizedTasks() {
        assertTrue(manager.getPrioritizedTasks().contains(task));
        assertTrue(manager.getPrioritizedTasks().contains(subtask));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getPrioritizedTasks().contains(task));
        assertTrue(loadedManager.getPrioritizedTasks().contains(subtask));
    }
}