package com.github.luckybard.timetracker.model;

import java.io.Serializable;

public class Session implements Serializable {

    private Long id;
    private String branch;
    private String date;      // "YYYY-MM-DD"
    private String startTime; // "HH:mm:ss"
    private String endTime; // "HH:mm:ss"

    public Long getId() {
        return id;
    }

    public Session setId(Long id) {
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
}