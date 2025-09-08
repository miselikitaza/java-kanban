package tasks;

import static org.junit.jupiter.api.Assertions.*;

import exceptions.NotFoundException;
import exceptions.TimeConflictException;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

class SubtaskTest {

    static InMemoryTaskManager taskManager;
    static Epic epic;
    static Subtask subtask;

    @BeforeAll
    public static void create() throws NotFoundException, TimeConflictException {
        taskManager = new InMemoryTaskManager();
        epic = new Epic("Музыкальная школа", "Подготовиться к новому учебному году");
        taskManager.createEpic(epic);
        subtask = new Subtask("Одежда для танцев", "купить спортивный купальник",
                TaskStatus.IN_PROGRESS, LocalDateTime.of(2025, Month.AUGUST, 25, 16, 40),
                Duration.ofMinutes(15), epic.getId());
        taskManager.createSubtask(subtask);
    }

    @Test
    public void shouldBeOneSubtaskWhenIdsAreEqual() throws NotFoundException, TimeConflictException {
        Subtask subtaskTwo = new Subtask(subtask.getId(), "Торт", "испечь коржи",
                TaskStatus.IN_PROGRESS, LocalDateTime.of(2025, Month.AUGUST, 26, 11, 0),
                Duration.ofMinutes(3),  epic.getId());
        taskManager.createSubtask(subtaskTwo);
        assertEquals(subtask, subtaskTwo);
        assertEquals(1, taskManager.getAllSubtask().size());
    }

    @Test
    public void subtaskCannotReferenceItselfAsEpic() {
        Subtask uncorrectSubtask = new Subtask(subtask.getId(), subtask.getName(), subtask.getDescription(),
        subtask.getStatus(), subtask.getStartTime().plusHours(12), subtask.getDuration(), subtask.getId());
        assertThrows(NotFoundException.class, () -> {
            taskManager.createSubtask(uncorrectSubtask);
        });
        assertNotEquals(subtask.getEpicId(), uncorrectSubtask.getId());
        assertEquals(1, taskManager.getAllSubtask().size());
        assertTrue(taskManager.getAllSubtask().contains(subtask));
    }
}