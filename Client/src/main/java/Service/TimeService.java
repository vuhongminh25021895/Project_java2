package Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeService {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String getRemainingTime(
            LocalDateTime endTime
    ) {

        Duration duration =
                Duration.between(
                        LocalDateTime.now(),
                        endTime
                );

        if (duration.isNegative()) {
            return "Đã kết thúc";
        }

        long totalSeconds = duration.getSeconds();

        long days = totalSeconds / 86400;
        totalSeconds %= 86400;

        long hours = totalSeconds / 3600;
        totalSeconds %= 3600;

        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        if (days > 0) {
            return String.format(
                    "%d ngày %02d:%02d:%02d",
                    days,
                    hours,
                    minutes,
                    seconds
            );
        }

        return String.format(
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds
        );
    }

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(FORMATTER);
    }
}
