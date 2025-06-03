package manager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

class InMemoryTaskManagerTest {

    static InMemoryTaskManager taskManager;
    static Task task;
    static Epic epic;
    static Subtask subtask;

    @BeforeAll
    public static void create() {
        taskManager = new InMemoryTaskManager();
        task = new Task("Задача", "Описание", TaskStatus.NEW);
        taskManager.createTask(task);
        epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);
    }

    @Test
    public void canCreateTask() {
        assertNotNull(taskManager.getAllTasks());
        assertTrue(taskManager.getAllTasks().contains(task));
    }

    @Test
    public void canCreateEpicWithSubtask() {
        assertNotNull(taskManager.getAllEpics());
        assertTrue(taskManager.getAllEpics().contains(epic));

        assertNotNull(taskManager.getAllSubtask());
        assertTrue(taskManager.getAllSubtask().contains(subtask));

        assertTrue(epic.getSubtasks().contains(subtask.getId()));
        assertEquals(epic.getId(), subtask.getEpicId());
    }

    @Test
    public void canSearchTaskById() {
        assertNotNull(taskManager.getTaskById(task.getId()));
        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    public void canSearchEpicById() {
        assertNotNull(taskManager.getEpicById(epic.getId()));
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    public void canSearchSubtaskById() {
        assertNotNull(taskManager.getSubtaskById(subtask.getId()));
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }
}
