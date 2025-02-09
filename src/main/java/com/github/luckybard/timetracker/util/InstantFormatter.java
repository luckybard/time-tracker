package com.github.luckybard.timetracker.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class InstantFormatter {

    // Metoda statyczna do formatowania czasu
    public static String formatTime(Instant instant) {
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return zonedDateTime.format(timeFormatter);
    }

    // Metoda statyczna do formatowania daty
    public static String formatDate(Instant instant) {
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return zonedDateTime.format(dateFormatter);
    }
}
