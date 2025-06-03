package manager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

class InMemoryTaskManagerTest {

     InMemoryTaskManager taskManager;
     Task task;
     Epic epic;
     Subtask subtask;

    @BeforeEach
    public void create() {
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

    @Test
    public void canDeleteAllTasks() {
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    public void canDeleteAllEpics() {
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    public void canDeleteAllSubtasks() {
        taskManager.deleteAllSubtasks();
        assertTrue(taskManager.getAllSubtask().isEmpty());
    }

    @Test
    public void canDeleteTaskById() {
        taskManager.deleteTaskById(task.getId());
        assertFalse(taskManager.getAllTasks().contains(task));
    }

    @Test
    public void canDeleteEpicById() {
        taskManager.deleteEpicById(epic.getId());
        assertFalse(taskManager.getAllEpics().contains(epic));
    }

    @Test
    public void canDeleteSubtaskById() {
        taskManager.deleteSubtaskById(subtask.getId());
        assertFalse(taskManager.getAllSubtask().contains(subtask));
    }

    @Test
    public void tasksWithDifferentTypesOfIdsDoNotConflict() {
        Task predefinedTask = new Task(8, "Задача с предопределенным id", "Описание",
                TaskStatus.IN_PROGRESS);
        taskManager.createTask(predefinedTask);
        assertTrue(taskManager.getAllTasks().contains(task));
        assertTrue(taskManager.getAllTasks().contains(predefinedTask));
        assertNotEquals(task, predefinedTask);
    }

    @Test
    public void taskShouldRemainTheSameAfterAddingToManager() {
        Task taskAfterAdding = taskManager.getTaskById(task.getId());
        assertEquals(task.getId(), taskAfterAdding.getId());
        assertEquals(task.getName(), taskAfterAdding.getName());
        assertEquals(task.getDescription(), taskAfterAdding.getDescription());
        assertEquals(task.getStatus(),taskAfterAdding.getStatus());
    }

    @Test
    public void taskShouldRetainThePreviousVersion() {
        taskManager.addTaskToHistory(task);
        Task taskAfterAdding = taskManager.getHistory().getFirst();
        assertEquals(task, taskAfterAdding);
        assertTrue(taskManager.getAllTasks().contains(task));
        assertTrue(taskManager.getHistory().contains(task));
    }

    @Test
    public void canGetSubtasksForEpicId() {
        Subtask subtaskByEpicId = taskManager.getSubtasksForEpicId(epic.getId()).get(0);
        assertEquals(subtask, subtaskByEpicId);
    }
}
