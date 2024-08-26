package com.example.d4;

public class AttendanceRecord {
    private String type;
    private String timestamp;

    public AttendanceRecord(String type, String timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
