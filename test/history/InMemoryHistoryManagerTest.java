package history;

import static org.junit.jupiter.api.Assertions.*;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

class InMemoryHistoryManagerTest {

    InMemoryTaskManager historyManager;
    Task task1;
    Task task2;

    @BeforeEach
    public void create() {
        historyManager = new InMemoryTaskManager();
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
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void shouldSaveTheTasksInTheOrderTheyWereAdded() {
        Task taskForTail = new Task("Задача 3", "Описание 3", TaskStatus.NEW);
        historyManager.createTask(taskForTail);
        historyManager.getTaskById(taskForTail.getId());

        assertEquals(historyManager.getHistory().getLast(), taskForTail);
        assertEquals(historyManager.getHistory().getFirst(), task1);
    }

    @Test
    public void shouldDeleteThePreviousTaskIfItsViewedASecondTimeAndPutItAtTheEndOfTheHistory() {
        Task task3 = new Task("Задача 3", "Описание 3", TaskStatus.NEW);
        historyManager.createTask(task3);
        historyManager.getTaskById(task3.getId());

        assertEquals(historyManager.getHistory().getFirst(), task1);
        assertEquals(historyManager.getHistory().getLast(), task3);

        historyManager.getTaskById(task1.getId());
        assertEquals(historyManager.getHistory().getFirst(), task2);
        assertEquals(historyManager.getHistory().getLast(), task1);
    }
}