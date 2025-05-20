package manager;

import java.util.ArrayList;
import java.util.HashMap;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

public class TaskManager {

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private static int id = 0;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private int generateId() {
        return ++id;
    }


}
