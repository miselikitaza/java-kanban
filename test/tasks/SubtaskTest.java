package tasks;

import static org.junit.jupiter.api.Assertions.*;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SubtaskTest {

    static InMemoryTaskManager taskManager;
    static Epic epic;
    static Subtask subtask;

    @BeforeAll
    public static void create() {
        taskManager = new InMemoryTaskManager();
        epic = new Epic("Музыкальная школа", "Подготовиться к новому учебному году");
        taskManager.createEpic(epic);
        subtask = new Subtask("Одежда для танцев", "купить спортивный купальник",
                TaskStatus.IN_PROGRESS, epic.getId());
        taskManager.createSubtask(subtask);
    }

    @Test
    public void shouldBeOneSubtaskWhenIdsAreEqual() {
        Subtask subtaskTwo = new Subtask(subtask.getId(), "Торт", "испечь коржи",
                TaskStatus.IN_PROGRESS, epic.getId());
        taskManager.createSubtask(subtaskTwo);
        assertEquals(subtask, subtaskTwo);
    }

    @Test
    public void subtaskCannotReferenceItselfAsEpic() {
        Subtask uncorrectSubtask = new Subtask(subtask.getId(), subtask.getName(), subtask.getDescription(),
                subtask.getStatus(), subtask.getId());
        taskManager.createSubtask(uncorrectSubtask);
        assertNotEquals(subtask.getEpicId(), uncorrectSubtask.getId());
        assertEquals(1, taskManager.getAllSubtask().size());
        assertTrue(taskManager.getAllSubtask().contains(subtask));
    }
}