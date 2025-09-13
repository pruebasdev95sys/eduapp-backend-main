package com.school.config;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeUtils {
    public static final ZoneId APP_ZONE = ZoneId.of("America/Guatemala");

    public static LocalDateTime now() {
        return LocalDateTime.now(APP_ZONE);
    }

    public static ZonedDateTime nowZoned() {
        return ZonedDateTime.now(APP_ZONE);
    }

    public static boolean isBeforeNow(LocalDateTime dateTime) {
        return dateTime.isBefore(now());
    }

    public static LocalDateTime convertToAppZone(LocalDateTime dateTime, ZoneId fromZone) {
        ZonedDateTime zonedDateTime = dateTime.atZone(fromZone);
        return zonedDateTime.withZoneSameInstant(APP_ZONE).toLocalDateTime();
    }

    public static LocalDateTime fromUTC(LocalDateTime utcDateTime) {
        return convertToAppZone(utcDateTime, ZoneId.of("UTC"));
    }
}