package manager;

import history.HistoryManager;
import history.InMemoryHistoryManager;

import java.io.File;

public class Managers {

    private Managers() {

    }

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("resources/task_manager.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
