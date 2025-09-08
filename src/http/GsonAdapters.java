package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class GsonAdapters {

    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(TaskStatus.class, new TaskStatusAdapter())
                .registerTypeAdapter(Epic.class, new EpicAdapter())
                .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
                .registerTypeAdapter(Task.class, new TaskAdapter())
                .create();
    }

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(formatter));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            String value = in.nextString();
            return value != null ? LocalDateTime.parse(value, formatter) : null;
        }
    }

    private static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter out, Duration value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toMinutes());
            }
        }

        @Override
        public Duration read(JsonReader in) throws IOException {
            long minutes = in.nextLong();
            return Duration.ofMinutes(minutes);
        }
    }

    private static class TaskStatusAdapter extends TypeAdapter<TaskStatus> {
        @Override
        public void write(JsonWriter out, TaskStatus value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.name());
            }
        }

        @Override
        public TaskStatus read(JsonReader in) throws IOException {
            String value = in.nextString();
            try {
                return TaskStatus.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    private static class TaskAdapter extends TypeAdapter<Task> {
        @Override
        public Task read(JsonReader in) throws IOException {
            String name = null;
            String description = null;
            TaskStatus taskStatus = TaskStatus.NEW;
            LocalDateTime startTime = null;
            Duration duration = null;
            int id = 0;

            in.beginObject();
            while (in.hasNext()) {
                String fieldName = in.nextName();
                switch (fieldName) {
                    case "id":
                        id = in.nextInt();
                        break;
                    case "name":
                        name = in.nextString();
                        break;
                    case "description":
                        description = in.nextString();
                        break;
                    case "taskStatus":
                        try {
                            String statusStr = in.nextString();
                            taskStatus = TaskStatus.valueOf(statusStr.toUpperCase());
                        } catch (Exception e) {
                            taskStatus = TaskStatus.NEW;
                        }
                        break;
                    case "startTime":
                        try {
                            String startTimeStr = in.nextString();
                            if (startTimeStr != null && !startTimeStr.equals("null") && !startTimeStr.isEmpty()) {
                                startTime = LocalDateTime.parse(startTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                            }
                        } catch (Exception e) {
                            startTime = null;
                        }
                        break;
                    case "duration":
                        try {
                            long durationMinutes = in.nextLong();
                            duration = Duration.ofMinutes(durationMinutes);
                        } catch (Exception e) {
                            duration = Duration.ZERO;
                        }
                        break;
                    default:
                        in.skipValue();
                        break;
                }
            }
            in.endObject();

            if (name == null) {
                throw new IOException("Укажите имя для задачи");
            }

            Task task = new Task(name, description, taskStatus, startTime, duration);
            task.setId(id);
            return task;
        }

        @Override
        public void write(JsonWriter out, Task value) throws IOException {
            out.beginObject();
            out.name("id").value(value.getId());
            out.name("name").value(value.getName());
            out.name("description").value(value.getDescription());
            out.name("taskStatus").value(value.getStatus().toString());

            if (value.getStartTime() != null) {
                out.name("startTime").value(value.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } else {
                out.name("startTime").nullValue();
            }

            out.name("duration").value(value.getDuration() != null ? value.getDuration().toMinutes() : 0);

            if (value.getEndTime() != null) {
                out.name("endTime").value(value.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } else {
                out.name("endTime").nullValue();
            }

            out.endObject();
        }
    }

    private static class SubtaskAdapter extends TypeAdapter<Subtask> {
        @Override
        public Subtask read(JsonReader in) throws IOException {
            String name = null;
            String description = null;
            TaskStatus taskStatus = TaskStatus.NEW;
            LocalDateTime startTime = null;
            Duration duration = null;
            Integer epicId = null;
            int id = 0;

            in.beginObject();
            while (in.hasNext()) {
                String fieldName = in.nextName();
                switch (fieldName) {
                    case "id":
                        id = in.nextInt();
                        break;
                    case "name":
                        name = in.nextString();
                        break;
                    case "description":
                        description = in.nextString();
                        break;
                    case "taskStatus":
                        String statusStr = in.nextString();
                        try {
                            taskStatus = TaskStatus.valueOf(statusStr.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            taskStatus = TaskStatus.NEW;
                        }
                        break;
                    case "startTime":
                        String startTimeStr = in.nextString();
                        if (startTimeStr != null && !startTimeStr.equals("null")) {
                            try {
                                startTime = LocalDateTime.parse(startTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                            } catch (DateTimeParseException e) {
                                startTime = null;
                            }
                        }
                        break;
                    case "duration":
                        try {
                            long durationMinutes = in.nextLong();
                            duration = Duration.ofMinutes(durationMinutes);
                        } catch (NumberFormatException e) {
                            duration = Duration.ZERO;
                        }
                        break;
                    case "epicId":
                        try {
                            epicId = in.nextInt();
                        } catch (NumberFormatException e) {
                            throw new IOException("id эпика должен быть числом");
                        }
                        break;
                    default:
                        in.skipValue();
                        break;
                }
            }
            in.endObject();

            if (epicId == null) {
                throw new IOException("Укажите id эпика для подзадачи");
            }
            if (name == null) {
                throw new IOException("Укажите имя для подзадачи");
            }

            Subtask subtask = new Subtask(name, description, taskStatus, startTime, duration, epicId);
            subtask.setId(id);
            return subtask;
        }

        @Override
        public void write(JsonWriter out, Subtask value) throws IOException {
            out.beginObject();
            out.name("id").value(value.getId());
            out.name("name").value(value.getName());
            out.name("description").value(value.getDescription());
            out.name("taskStatus").value(value.getStatus().toString());

            if (value.getStartTime() != null) {
                out.name("startTime").value(value.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } else {
                out.name("startTime").nullValue();
            }

            out.name("duration").value(value.getDuration().toMinutes());

            if (value.getEndTime() != null) {
                out.name("endTime").value(value.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } else {
                out.name("endTime").nullValue();
            }

            out.name("epicId").value(value.getEpicId());
            out.endObject();
        }
    }

    private static class EpicAdapter extends TypeAdapter<Epic> {
        @Override
        public Epic read(JsonReader in) throws IOException {
            String name = null;
            String description = null;
            TaskStatus taskStatus = TaskStatus.NEW;

            in.beginObject();
            while (in.hasNext()) {
                String fieldName = in.nextName();
                switch (fieldName) {
                    case "name":
                        name = in.nextString();
                        break;
                    case "description":
                        description = in.nextString();
                        break;
                    case "taskStatus":
                        try {
                            String statusStr = in.nextString();
                            taskStatus = TaskStatus.valueOf(statusStr.toUpperCase());
                        } catch (Exception e) {
                            taskStatus = TaskStatus.NEW;
                        }
                        break;
                    default:
                        in.skipValue();
                        break;
                }
            }
            in.endObject();

            if (name == null) {
                throw new IOException("Укажите имя для эпика");
            }
            if (description == null) {
                description = "";
            }

            Epic epic = new Epic(name, description);
            epic.setStatus(taskStatus);
            return epic;
        }

        @Override
        public void write(JsonWriter out, Epic value) throws IOException {
            out.beginObject();
            out.name("id").value(value.getId());
            out.name("name").value(value.getName());
            out.name("description").value(value.getDescription());
            out.name("taskStatus").value(value.getStatus().toString());

            if (value.getStartTime() != null) {
                out.name("startTime").value(value.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } else {
                out.name("startTime").nullValue();
            }

            out.name("duration").value(value.getDuration() != null ? value.getDuration().toMinutes() : 0);

            if (value.getEndTime() != null) {
                out.name("endTime").value(value.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } else {
                out.name("endTime").nullValue();
            }

            out.name("subtasks");
            out.beginArray();
            for (Integer subtaskId : value.getSubtasks()) {
                out.value(subtaskId);
            }
            out.endArray();

            out.endObject();
        }
    }
}
