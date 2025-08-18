package tasks;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class DataTimeFormatter {

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");

    public static String formatDuration(Duration duration) {
        return String.format("%d:%02d:%02d",
                duration.toHours(),
                duration.toMinutesPart(),
                duration.toSecondsPart());
    }

    public static Duration fromStringToDuration(String string) {
        String[] parts = string.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Некорректный формат: " + string);
        }
        try {
            long hours = Long.parseLong(parts[0]);
            long minutes = Long.parseLong(parts[1]);
            long seconds = Long.parseLong(parts[2]);
            return Duration.ofHours(hours)
                    .plusMinutes(minutes)
                    .plusSeconds(seconds);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректный формат: " + string);
        }
    }
}
