package com.github.luckybard.timetracker.model;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Session {

    private String id;
    private String branch;
    private String name;
    private String description;
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

    public void setId(String id) {
        this.id = id;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isSentToJira() {
        return isSentToJira;
    }

    public void setSentToJira(boolean sentToJira) {
        isSentToJira = sentToJira;
    }

    public Duration getDuration() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        LocalTime start = LocalTime.parse(startTime, timeFormatter);
        LocalTime end = LocalTime.parse(endTime, timeFormatter);

        return Duration.between(start, end);
    }
}