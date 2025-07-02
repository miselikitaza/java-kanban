package history;

import static org.junit.jupiter.api.Assertions.*;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

class InMemoryHistoryManagerTest {

    InMemoryTaskManager historyManager;
    Task task1;
    Task task2;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    public void create() {
        historyManager = new InMemoryTaskManager();
        epic = new Epic("Эпик", "эпик");
        historyManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "подзадача", TaskStatus.NEW, epic.getId());
        historyManager.createSubtask(subtask);
        task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        historyManager.createTask(task1);
        task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW);
        historyManager.createTask(task2);

        historyManager.getTaskById(task1.getId());
        historyManager.getTaskById(task2.getId());
    }

    @Test
    public void shouldAddTheTaskToTheHistoryWhenWeGetIt() {
        assertNotNull(historyManager.getHistory());
        assertTrue(historyManager.getHistory().contains(task1));
        assertTrue(historyManager.getHistory().contains(task2));
    }

    @Test
    public void shouldRemoveTheTaskFromTheHistoryWhenWeDeleteTheTask() {
        historyManager.deleteTaskById(task1.getId());
        assertFalse(historyManager.getHistory().contains(task1));
        assertFalse(historyManager.getHistory().isEmpty());
    }

    @Test
    public void shouldSaveTheTasksInTheOrderTheyWereAdded() {
        historyManager.getEpicById(epic.getId());
        assertEquals(historyManager.getHistory().getLast(), epic);
        historyManager.getTaskById(task2.getId());
        assertEquals(historyManager.getHistory().getLast(), task2);
        assertEquals(historyManager.getHistory().getFirst(), task1);
    }

    @Test
    public void shouldDeleteThePreviousTaskIfItsViewedASecondTimeAndPutItAtTheEndOfTheHistory() {
        historyManager.getTaskById(task1.getId());
        assertEquals(historyManager.getHistory().getFirst(), task2);
        assertEquals(historyManager.getHistory().getLast(), task1);
    }


    @Test
    public void shouldRemoveTheTaskFromTheHistoryWhenWeRemoveAllTasks() {
        historyManager.getEpicById(epic.getId());
        historyManager.deleteAllTasks();
        assertFalse(historyManager.getHistory().contains(task1));
        assertFalse(historyManager.getHistory().contains(task2));
        assertTrue(historyManager.getHistory().contains(epic));
    }

    @Test
    public void shouldRemoveASubtaskFromTheHistoryIfHerEpicIsRemoved() {
        historyManager.getEpicById(epic.getId());
        historyManager.getSubtaskById(subtask.getId());
        historyManager.deleteEpicById(epic.getId());
        assertFalse(historyManager.getHistory().contains(subtask));
        assertFalse(historyManager.getHistory().contains(epic));
        assertTrue(historyManager.getHistory().contains(task1));
    }

    @Test
    public void shouldAddAllTasksToTheHistoryIfWeUsedGetAll() {
        Epic epic2 = new Epic("Эпик 2", "Описание 2");
        historyManager.createEpic(epic2);
        historyManager.getAllEpics();

        assertTrue(historyManager.getHistory().contains(epic));
        assertTrue(historyManager.getHistory().contains(epic2));
    }
}