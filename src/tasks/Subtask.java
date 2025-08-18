package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String name, String description, TaskStatus taskStatus, LocalDateTime starTime,
                   Duration duration, int epicId) {
        super(name, description, taskStatus, starTime, duration);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, TaskStatus taskStatus, LocalDateTime startTime,
                   Duration duration, int epicId) {
        super(id, name, description, taskStatus, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() + '\'' +
                ", startTime=" + getStartTime().format(DataTimeFormatter.formatter) + '\'' +
                ", duration=" + DataTimeFormatter.formatDuration(getDuration()) + '\'' +
                ", endTime=" + getEndTime().format(DataTimeFormatter.formatter) + '\'' +
                ", epicId=" + epicId +
                '}';
    }
}

