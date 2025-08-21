package history;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

class HistoryManagerTest {

    static InMemoryHistoryManager manager;
    static Task task;
    static Epic epic;

    @BeforeEach
    void create() {
        manager = new InMemoryHistoryManager();
        task = new Task(1, "Имя", "Описание", TaskStatus.DONE, LocalDateTime.now(),
                Duration.ofHours(4));
        epic = new Epic(10, "Имя", "Описание");
    }

    @Test
    void canAddAndRemoveTaskToTheHistory() {
        manager.add(task);
        assertTrue(manager.getHistory().contains(task));

        manager.remove(task.getId());
        assertFalse(manager.getHistory().contains(task));
    }

    @Test
    void canGetEmptyOrFullHistory() {
        assertTrue(manager.getHistory().isEmpty());

        manager.add(task);
        assertFalse(manager.getHistory().isEmpty());
    }

    @Test
    void shouldNotDuplicateTasks() {
        manager.add(task);
        manager.add(epic);
        assertEquals(2, manager.getHistory().size());
        assertEquals(task, manager.getHistory().getFirst());

        manager.add(task);
        assertEquals(2, manager.getHistory().size());
        assertEquals(task, manager.getHistory().getLast());
    }

    @Test
    void shouldChangeTheLocationOfTasksWhenOneOfThemIsDeleted() {
        Subtask subtask = new Subtask("Имя", "Описание", TaskStatus.IN_PROGRESS,
                LocalDateTime.now(), Duration.ofHours(2), epic.getId());
        manager.add(subtask);
        manager.add(task);
        manager.add(epic);

        assertEquals(subtask, manager.getHistory().getFirst());
        assertEquals(epic, manager.getHistory().getLast());

        manager.remove(subtask.getId());
        assertEquals(task, manager.getHistory().getFirst());
        assertEquals(epic, manager.getHistory().getLast());
    }
}
