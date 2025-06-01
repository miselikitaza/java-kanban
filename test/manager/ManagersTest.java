package manager;

import history.HistoryManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    public void shouldReturnNotEmptyTaskManager() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);
    }

    @Test
    public void shouldReturnNotEmptyHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }

}