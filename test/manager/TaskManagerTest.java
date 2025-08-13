package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public abstract class TaskManagerTest <T extends TaskManager> {

    public T manager;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void create() throws IOException {
        manager = createManager();
        task = new Task("Задача", "Описание", TaskStatus.NEW);
        manager.createTask(task);
        epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.createSubtask(subtask);
    }

    public abstract T createManager();

    @Test
    public void canCreateTask() {
        assertNotNull(manager.getAllTasks());
        assertTrue(manager.getAllTasks().contains(task));
    }

    @Test
    public void canCreateEpicWithSubtask() {
        assertNotNull(manager.getAllEpics());
        assertTrue(manager.getAllEpics().contains(epic));

        assertNotNull(manager.getAllSubtask());
        assertTrue(manager.getAllSubtask().contains(subtask));

        assertTrue(epic.getSubtasks().contains(subtask.getId()));
        assertEquals(epic.getId(), subtask.getEpicId());
    }

    @Test
    public void canSearchTaskById() {
        assertNotNull(manager.getTaskById(task.getId()));
        assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
    public void canSearchEpicById() {
        assertNotNull(manager.getEpicById(epic.getId()));
        assertEquals(epic, manager.getEpicById(epic.getId()));
    }

    @Test
    public void canSearchSubtaskById() {
        assertNotNull(manager.getSubtaskById(subtask.getId()));
        assertEquals(subtask, manager.getSubtaskById(subtask.getId()));
    }

    @Test
    public void canDeleteAllTasks() {
        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void canDeleteAllEpics() {
        manager.deleteAllEpics();
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    public void canDeleteAllSubtasks() {
        manager.deleteAllSubtasks();
        assertTrue(manager.getAllSubtask().isEmpty());
    }

    @Test
    public void canDeleteTaskById() {
        manager.deleteTaskById(task.getId());
        assertFalse(manager.getAllTasks().contains(task));
    }

    @Test
    public void canDeleteEpicById() {
        manager.deleteEpicById(epic.getId());
        assertFalse(manager.getAllEpics().contains(epic));
    }

    @Test
    public void canDeleteSubtaskById() {
        manager.deleteSubtaskById(subtask.getId());
        assertFalse(manager.getAllSubtask().contains(subtask));
    }

}
