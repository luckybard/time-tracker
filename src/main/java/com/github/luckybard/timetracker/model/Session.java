package com.github.luckybard.timetracker.model;

import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class Session implements Serializable {

    private String id;
    private String branch;
    /*** YYYY-MM-DD ***/
    private String date;
    /*** HH:mm:ss ***/
    private String startTime;
    /*** HH:mm:ss ***/
    private String endTime;
    private boolean isSentToJira;

    public String getId() {
        return id;
    }

    public Session setId(String id) {
        this.id = id;
        return this;
    }

    public String getBranch() {
        return branch;
    }

    public Session setBranch(String branch) {
        this.branch = branch;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Session setDate(String date) {
        this.date = date;
        return this;
    }

    public String getStartTime() {
        return startTime;
    }

    public Session setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public String getEndTime() {
        return endTime;
    }

    public Session setEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }

    public boolean isSentToJira() {
        return isSentToJira;
    }

    public Session setSentToJira(boolean sentToJira) {
        this.isSentToJira = sentToJira;
        return this;
    }

    public Duration getDuration() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        LocalTime start = LocalTime.parse(startTime, timeFormatter);
        LocalTime end = LocalTime.parse(endTime, timeFormatter);

        return Duration.between(start, end);
    }

    public String getDurationAsString() {
        Duration duration = getDuration();
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();

        return 0 == hours ? minutes + "m" : hours + "h " + minutes + "m";
    }
}