package history;

import static org.junit.jupiter.api.Assertions.*;

import exceptions.NotFoundException;
import exceptions.TimeConflictException;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

class InMemoryHistoryManagerTest {

    InMemoryTaskManager historyManager;
    Task task1;
    Task task2;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    public void create() throws NotFoundException, TimeConflictException {
        historyManager = new InMemoryTaskManager();
        epic = new Epic("Эпик", "эпик");
        historyManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "подзадача", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.AUGUST, 21, 15, 0), Duration.ofHours(24),
                epic.getId());
        historyManager.createSubtask(subtask);
        task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW,
                LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0), Duration.ofMinutes(45));
        historyManager.createTask(task1);
        task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.DECEMBER, 31, 10, 10), Duration.ofMinutes(50));
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
    public void shouldRemoveTheTaskFromTheHistoryWhenWeDeleteTheTask() throws NotFoundException {
        historyManager.deleteTaskById(task1.getId());
        assertFalse(historyManager.getHistory().contains(task1));
        assertFalse(historyManager.getHistory().isEmpty());
    }

    @Test
    public void shouldSaveTheTasksInTheOrderTheyWereAdded() throws NotFoundException {
        historyManager.getEpicById(epic.getId());
        assertEquals(historyManager.getHistory().getLast(), epic);
        historyManager.getTaskById(task2.getId());
        assertEquals(historyManager.getHistory().getLast(), task2);
        assertEquals(historyManager.getHistory().getFirst(), task1);
    }

    @Test
    public void shouldDeleteThePreviousTaskIfItsViewedASecondTimeAndPutItAtTheEndOfTheHistory() throws NotFoundException {
        historyManager.getTaskById(task1.getId());
        assertEquals(historyManager.getHistory().getFirst(), task2);
        assertEquals(historyManager.getHistory().getLast(), task1);
    }


    @Test
    public void shouldRemoveTheTaskFromTheHistoryWhenWeRemoveAllTasks() throws NotFoundException {
        historyManager.getEpicById(epic.getId());
        historyManager.deleteAllTasks();
        assertFalse(historyManager.getHistory().contains(task1));
        assertFalse(historyManager.getHistory().contains(task2));
        assertTrue(historyManager.getHistory().contains(epic));
    }

    @Test
    public void shouldRemoveASubtaskFromTheHistoryIfHerEpicIsRemoved() throws NotFoundException {
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