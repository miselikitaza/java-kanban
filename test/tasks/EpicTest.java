package tasks;

import static org.junit.jupiter.api.Assertions.*;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

class EpicTest {

    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void shouldBeOneEpicWhenIdsAreEqual() {
        Epic epicOne = new Epic("Ремонт", "поклеить обои");
        taskManager.createEpic(epicOne);
        Epic epicTwo = new Epic(epicOne.getId(), "Ремонт", "положить ламинат");
        taskManager.createEpic(epicTwo);
        assertEquals(epicOne, epicTwo);
    }
}
