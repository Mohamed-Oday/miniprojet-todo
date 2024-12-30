package org.openjfx.miniprojet.model;

import java.sql.Timestamp;

/**
 * Represents a notification for a user.
 */
public class Notification {
    private int id;
    private String userID;
    private String title;
    private String message;
    private Timestamp time;
    private boolean isRead;

    /**
     * Constructs a new Notification.
     *
     * @param userID the ID of the user.
     * @param title the title of the notification.
     * @param message the message of the notification.
     * @param time the timestamp of the notification.
     * @param isRead whether the notification has been read.
     */
    public Notification(String userID, String title, String message, Timestamp time, boolean isRead) {
        this.userID = userID;
        this.title = title;
        this.message = message;
        this.time = time;
        this.isRead = isRead;
    }

    /**
     * Gets the ID of the notification.
     *
     * @return the ID of the notification.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the notification.
     *
     * @param id the new ID of the notification.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the user ID associated with the notification.
     *
     * @return the user ID.
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Sets the user ID associated with the notification.
     *
     * @param userID the new user ID.
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Gets the title of the notification.
     *
     * @return the title of the notification.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the notification.
     *
     * @param title the new title of the notification.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the message of the notification.
     *
     * @return the message of the notification.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message of the notification.
     *
     * @param message the new message of the notification.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the timestamp of the notification.
     *
     * @return the timestamp of the notification.
     */
    public Timestamp getTime() {
        return time;
    }

    /**
     * Sets the timestamp of the notification.
     *
     * @param time the new timestamp of the notification.
     */
    public void setTime(Timestamp time) {
        this.time = time;
    }

    /**
     * Checks if the notification has been read.
     *
     * @return true if the notification has been read, false otherwise.
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Sets the read status of the notification.
     *
     * @param read the new read status of the notification.
     */
    public void setRead(boolean read) {
        isRead = read;
    }

    /**
     * Returns a string representation of the notification.
     *
     * @return a string representation of the notification.
     */
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