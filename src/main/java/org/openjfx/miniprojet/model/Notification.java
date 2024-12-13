package org.openjfx.miniprojet.model;

import java.sql.Timestamp;

public class Notification {
    private int id;
    private String userID;
    private String title;
    private String message;
    private Timestamp time;
    private boolean isRead;

    public Notification(String userID, String title, String message, Timestamp time, boolean isRead) {
        this.userID = userID;
        this.title = title;
        this.message = message;
        this.time = time;
        this.isRead = isRead;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userID='" + userID + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", time=" + time +
                ", isRead=" + isRead +
                '}';
    }
}
