package tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

class SubtaskTest {

    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void shouldBeOneSubtaskWhenIdsAreEqual() {
        Epic epic = new Epic("Праздничный стол", "организовать закуски");
        taskManager.createEpic(epic);
        Subtask subtaskOne = new Subtask("Продукты", "Купить ребрышки",
                TaskStatus.IN_PROGRESS, epic.getId());
        taskManager.createSubtask(subtaskOne);
        Subtask subtaskTwo = new Subtask(subtaskOne.getId(), "Торт", "испечь коржи",
                TaskStatus.IN_PROGRESS, epic.getId());
        taskManager.createSubtask(subtaskTwo);
        assertEquals(subtaskOne, subtaskTwo);
    }
}