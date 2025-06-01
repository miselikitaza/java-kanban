package tasks;

import static org.junit.jupiter.api.Assertions.*;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EpicTest {

    static InMemoryTaskManager taskManager;
    static Epic epic;

    @BeforeAll
    public static void create() {
        taskManager = new InMemoryTaskManager();
        epic = new Epic("Ремонт", "поклеить обои");
        taskManager.createEpic(epic);
    }

    @Test
    public void shouldBeOneEpicWhenIdsAreEqual() {
        Epic epicTwo = new Epic(epic.getId(), "Ремонт", "положить ламинат");
        taskManager.createEpic(epicTwo);
        assertEquals(epic, epicTwo);
    }

    @Test
    public void ShouldReturnFalseWhenAddEpicToItself() {
        epic.addSubtasks(epic.getId());
        assertFalse(epic.getSubtasks().contains(epic.getId()));
    }
}
