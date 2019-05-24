package com.extrace.ui.entity;

public class Trace {
    /** 时间 */
    private String time;
    /** 描述 */
    private String description;

    public Trace() {
    }

    public Trace(String time, String description) {
        this.time = time;
        this.description = description;
    }

    public String getAcceptTime() {
        return time;
    }

    public void setAcceptTime(String time) {
        this.time = time;
    }

    public String getAcceptStation() {
        return description;
    }

    public void setAcceptStation(String description) {
        this.description = description;
    }
}