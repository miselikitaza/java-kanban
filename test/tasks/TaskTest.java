package tasks;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {

    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void shouldBeOneTaskWhenIdsAreEqual() {
        Task taskOne = new Task("Спорт", "сделать пробежку", TaskStatus.IN_PROGRESS);
        taskManager.createTask(taskOne);
        Task taskTwo = new Task(taskOne.getId(), "Зал", "попрыгать на скакалке", TaskStatus.NEW);
        taskManager.createTask(taskTwo);
        Assertions.assertEquals(taskOne, taskTwo);
    }
}