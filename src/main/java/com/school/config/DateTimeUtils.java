package com.school.config;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtils {
    public static final ZoneId APP_ZONE = ZoneId.of("America/Guatemala");

    public static LocalDateTime now() {
        return LocalDateTime.now(APP_ZONE);
    }

    public static boolean isBeforeNow(LocalDateTime dateTime) {
        return dateTime.isBefore(now());
    }
}