package manager;

import org.junit.jupiter.api.*;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File tempFile;
    FileBackedTaskManager manager;

    @Override
    @BeforeEach
    public void create() throws IOException{
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
}