package manager;

import tasks.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class CSVFormatter {

    public static final String PRIORITY_HEADER = "#PRIORITY_TASKS";

    public static String getHeader() {
        return "id,type,name,status,description,startTime,duration,epic";
    }

    public static String toString(Task task) {
        TaskType type;
        String epicId = "";

        if (task instanceof Epic) {
            type = TaskType.EPIC;
        } else if (task instanceof Subtask) {
            type = TaskType.SUBTASK;
            epicId = String.valueOf(((Subtask) task).getEpicId());
        } else {
            type = TaskType.TASK;
        }

        String durationStr = "null";
        if (task.getDuration() != null) {
            durationStr = DataTimeFormatter.formatDuration(task.getDuration());
        }

        String startTimeStr = "null";
        if (task.getStartTime() != null) {
            startTimeStr = task.getStartTime().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        return String.join(",",
                String.valueOf(task.getId()),
                type.toString(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                startTimeStr,
                durationStr,
                epicId);
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",", -1);
        int id = Integer.parseInt(parts[0].trim());
        TaskType type = TaskType.valueOf(parts[1].trim());
        String name = parts[2].trim();
        TaskStatus status = TaskStatus.valueOf(parts[3].trim());
        String description = parts[4].trim();

        LocalDateTime startTime = null;
        if (!parts[5].trim().equals("null")) {
            startTime = LocalDateTime.parse(parts[5].trim());
        }

        Duration duration = null;
        if (!parts[6].trim().equals("null")) {
            duration = DataTimeFormatter.fromStringToDuration(parts[6].trim());
        }

        switch (type) {
            case TASK:
                return new Task(id, name, description, status, startTime, duration);
            case EPIC:
                return new Epic(id, name, description);
            case SUBTASK:
                int epicId = parts[7].trim().isEmpty() ? 0 : Integer.parseInt(parts[7].trim());
                return new Subtask(id, name, description, status, startTime, duration, epicId);
            default:
                return null;
        }
    }
}
