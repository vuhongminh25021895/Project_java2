package Server.Service;

import java.time.LocalDateTime;

public class TimeService {
    public static String getTime(
            LocalDateTime time
    ) {
        long totalSeconds = time.getSecond();

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
}
