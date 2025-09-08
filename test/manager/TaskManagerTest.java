package manager;

import exceptions.NotFoundException;
import exceptions.TimeConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public abstract class TaskManagerTest <T extends TaskManager> {

    public T manager;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void create() throws IOException, TimeConflictException, NotFoundException {
        manager = createManager();
        task = new Task("Задача", "Описание", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.AUGUST, 20, 10, 30), Duration.ofSeconds(200));
        manager.createTask(task);
        epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.AUGUST, 21, 20, 30),
                Duration.ofSeconds(2000), epic.getId());
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
    public void canSearchTaskById() throws NotFoundException {
        assertNotNull(manager.getTaskById(task.getId()));
        assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
    public void canSearchEpicById() throws NotFoundException {
        assertNotNull(manager.getEpicById(epic.getId()));
        assertEquals(epic, manager.getEpicById(epic.getId()));
    }

    @Test
    public void canSearchSubtaskById() throws NotFoundException {
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
    public void canDeleteTaskById() throws NotFoundException {
        manager.deleteTaskById(task.getId());
        assertFalse(manager.getAllTasks().contains(task));
    }

    @Test
    public void canDeleteEpicById() throws NotFoundException {
        manager.deleteEpicById(epic.getId());
        assertFalse(manager.getAllEpics().contains(epic));
    }

    @Test
    public void canDeleteSubtaskById() throws NotFoundException {
        manager.deleteSubtaskById(subtask.getId());
        assertFalse(manager.getAllSubtask().contains(subtask));
    }

    @Test
    public void epicShouldChangeStatusDependingOnTheSubtasks() throws TimeConflictException, NotFoundException {
        manager.createSubtask(new Subtask("Имя", "Описание", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.SEPTEMBER, 12, 20, 10),
                Duration.ofHours(8), epic.getId()));
        assertEquals(TaskStatus.NEW, epic.getStatus());

        manager.deleteAllSubtasks();
        manager.createSubtask(new Subtask("Имя", "Описание", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.SEPTEMBER, 10, 13,40),
                Duration.ofHours(4), epic.getId()));
        manager.createSubtask(new Subtask("Имя", "Описание", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.SEPTEMBER, 12, 20, 34),
                Duration.ofMinutes(12), epic.getId()));
        assertEquals(TaskStatus.DONE, epic.getStatus());

        manager.createSubtask(new Subtask("Имя", "Описание", TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofMinutes(8), epic.getId()));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());

        manager.createSubtask(new Subtask("Имя", "Описание", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, Month.OCTOBER, 22, 22, 0),
                Duration.ofMinutes(40), epic.getId()));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void tasksShouldNotOverlapOfExecutionTime() throws TimeConflictException {
        Task taskWithDifferentTime = new Task("Имя", "Описание", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, Month.SEPTEMBER, 25, 12, 40), Duration.ofHours(12));
        manager.createTask(taskWithDifferentTime);
        assertEquals(2, manager.getAllTasks().size());
        assertTrue(manager.getAllTasks().contains(taskWithDifferentTime));

        Task taskWithSameTime = new Task("Имя", "Описание", TaskStatus.DONE,
                task.getStartTime(), Duration.ofHours(4));
        assertThrows(TimeConflictException.class, () -> {
                    manager.createTask(taskWithSameTime);
                });
        assertEquals(2, manager.getAllTasks().size());
        assertFalse(manager.getAllTasks().contains(taskWithSameTime));
    }
}
