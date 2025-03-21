package com.github.luckybard.timetracker.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class TimeUtils {

    public static boolean isValidDate(String date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isValidTime(String time) {

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setLenient(false);
        try {
            timeFormat.parse(time);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isEndTimeBeforeStartTime(String startTime, String endTime) {

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setLenient(false);

        try {
            Date formattedStartTime = timeFormat.parse(startTime);
            Date formattedEndTime = timeFormat.parse(endTime);
            return formattedEndTime.before(formattedStartTime);
        } catch (Exception e) {
            return false;
        }
    }

    public static String getDurationAsString(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();

        return 0 == hours ? minutes + "m" : hours + "h " + minutes + "m";
    }
}
