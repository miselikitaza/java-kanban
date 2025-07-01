package history;

import static org.junit.jupiter.api.Assertions.*;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

class InMemoryHistoryManagerTest {

    static InMemoryTaskManager historyManager;
    Task task1;
    Task task2;
    static Epic epic;
    static Subtask subtask;

    @BeforeAll
    public static void createEpicWithSubtasks() {
        historyManager = new InMemoryTaskManager();
        epic = new Epic("Эпик", "эпик");
        historyManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "подзадача", TaskStatus.NEW, epic.getId());
        historyManager.createSubtask(subtask);
    }

    @BeforeEach
    public void create() {
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
        Task task3 = new Task("Задача 3", "Описание 3", TaskStatus.NEW);
        historyManager.createTask(task3);
        Task task4 = new Task("Задача 4", "Описание 4", TaskStatus.NEW);
        historyManager.createTask(task4);

        historyManager.getAllTasks();
        assertTrue(historyManager.getHistory().contains(task3));
        assertTrue(historyManager.getHistory().contains(task4));
    }
}