package manager;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }

    @Test
    public void tasksWithDifferentTypesOfIdsDoNotConflict() {
        Task predefinedTask = new Task(8, "Задача с предопределенным id", "Описание",
                TaskStatus.IN_PROGRESS, LocalDateTime.of(2025, Month.SEPTEMBER, 12, 13, 45),
                Duration.ofMinutes(12));
        manager.createTask(predefinedTask);
        assertTrue(manager.getAllTasks().contains(task));
        assertTrue(manager.getAllTasks().contains(predefinedTask));
        assertNotEquals(task, predefinedTask);
    }

    @Test
    public void taskShouldRemainTheSameAfterAddingToManager() {
        Task taskAfterAdding = manager.getTaskById(task.getId());
        assertEquals(task.getId(), taskAfterAdding.getId());
        assertEquals(task.getName(), taskAfterAdding.getName());
        assertEquals(task.getDescription(), taskAfterAdding.getDescription());
        assertEquals(task.getStatus(),taskAfterAdding.getStatus());
    }

    @Test
    public void taskShouldRetainThePreviousVersion() {
        manager.getTaskById(task.getId());
        Task taskAfterAdding = manager.getHistory().getFirst();
        assertEquals(task, taskAfterAdding);
        assertTrue(manager.getAllTasks().contains(task));
        assertTrue(manager.getHistory().contains(task));
    }

    @Test
    public void canGetSubtasksForEpicId() {
        Subtask subtaskByEpicId = manager.getSubtasksForEpicId(epic.getId()).getFirst();
        assertEquals(subtask, subtaskByEpicId);
    }

    @Test
    public void epicShouldNotKeepADeletedSubtask() {
        Subtask subtaskForDelete = new Subtask("Задача для удаления", "описание",
                TaskStatus.NEW, LocalDateTime.of(2025, Month.SEPTEMBER, 24, 23, 15),
                Duration.ofMinutes(10), epic.getId());
        manager.createSubtask(subtaskForDelete);

        assertTrue(epic.getSubtasks().contains(subtask.getId()));
        assertTrue(epic.getSubtasks().contains(subtaskForDelete.getId()));

        manager.deleteSubtaskById(subtaskForDelete.getId());
        assertFalse(epic.getSubtasks().contains(subtaskForDelete.getId()));
        assertTrue(epic.getSubtasks().contains(subtask.getId()));
    }
}
