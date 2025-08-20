package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Epic extends Task {

    private final List<Integer> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW, null, Duration.ZERO);
        this.subtasks = new ArrayList<>();
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW, null, Duration.ZERO);
        this.subtasks = new ArrayList<>();
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }


    public void addSubtasks(int subtask) {
        if (subtask != this.getId()) {
            subtasks.add(subtask);
        }
    }

    public void removeSubtasks(int subtask) {
        this.subtasks.remove(Integer.valueOf(subtask));
    }

    public void updateTime(Map<Integer, Subtask> allSubtasks) {
        if (allSubtasks == null || subtasks.isEmpty()) {
            this.startTime = null;
            this.duration = Duration.ZERO;
            endTime = null;
            return;
        }

        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;
        Duration totalDuration = Duration.ZERO;
        boolean hasTime = false;

        for (int subtaskId : subtasks) {
            Subtask subtask = allSubtasks.get(subtaskId);

            if (subtask == null) {
                continue;
            }

            if (subtask.getStartTime() != null) {
                hasTime = true;
                if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                    earliestStart = subtask.getStartTime();
                }

                LocalDateTime subtaskEnd = subtask.getEndTime();
                if (subtaskEnd != null) {
                    if (latestEnd == null || subtaskEnd.isAfter(latestEnd)) {
                        latestEnd = subtaskEnd;
                    }
                }
                if (subtask.getDuration() != null) {
                    totalDuration = totalDuration.plus(subtask.getDuration());
                }
            }
        }

        if (hasTime) {
            this.startTime = earliestStart;
            this.duration = totalDuration;
            this.endTime = latestEnd;
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        String startTimeString;
        if (startTime == null) {
            startTimeString = "null";
        } else {
            startTimeString = startTime.format(DataTimeFormatter.formatter);
        }

        String endTimeString;
        if (endTime == null) {
            endTimeString = "null";
        } else {
            endTimeString = endTime.format(DataTimeFormatter.formatter);
        }
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + startTimeString + '\'' +
                ", duration=" + DataTimeFormatter.formatDuration(getDuration()) + '\'' +
                ", endTime=" + endTimeString + '\'' +
                ", subtasks=" + subtasks +
                '}';
    }
}