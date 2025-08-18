package tasks;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

class TaskTest {

    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void shouldBeOneTaskWhenIdsAreEqual() {
        Task taskOne = new Task("Спорт", "сделать пробежку", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, Month.AUGUST, 18, 20, 30), Duration.ofMinutes(12));
        taskManager.createTask(taskOne);
        Task taskTwo = new Task(taskOne.getId(), "Зал", "попрыгать на скакалке", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.AUGUST, 21, 13, 20), Duration.ofMinutes(8));
        taskManager.createTask(taskTwo);
        Assertions.assertEquals(taskOne, taskTwo);
    }
}