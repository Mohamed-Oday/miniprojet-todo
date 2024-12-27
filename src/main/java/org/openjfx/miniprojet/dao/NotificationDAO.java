package org.openjfx.miniprojet.dao;

import org.openjfx.miniprojet.model.Notification;
import org.openjfx.miniprojet.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class for managing notifications in the database.
 */
public class NotificationDAO {
    public static final String INSERT_NOTIFICATION = "INSERT INTO notifications (user_id, title, message) VALUES (?, ?, ?)";
    public static final String GET_NOTIFICATIONS = "SELECT * FROM notifications WHERE user_id= ? AND is_read = 0 ORDER BY time DESC";
    public static final String MARK_AS_READ = "UPDATE notifications SET is_read = 1 WHERE id = ?";

    /**
     * Gets a connection to the database.
     *
     * @return a Connection object to the database.
     * @throws SQLException if a database access error occurs.
     */
    private Connection getConnection() throws SQLException {
        return Database.getInstance().getConnection();
    }

    /**
     * Inserts a new notification into the database.
     *
     * @param userID the ID of the user to whom the notification belongs.
     * @param title the title of the notification.
     * @param message the message content of the notification.
     */
    public void insertNotification(String userID, String title, String message) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NOTIFICATION)) {
            preparedStatement.setString(1, userID);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, message);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves unread notifications for a specific user from the database.
     *
     * @param userID the ID of the user whose notifications are to be retrieved.
     * @return a list of Notification objects representing the unread notifications.
     */
    public List<Notification> getNotificationsByUser(String userID) {
        List<Notification> notifications = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_NOTIFICATIONS)) {
            preparedStatement.setString(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Notification notification = new Notification(
                        resultSet.getString("user_id"),
                        resultSet.getString("title"),
                        resultSet.getString("message"),
                        resultSet.getTimestamp("time"),
                        resultSet.getBoolean("is_read")
                );
                notification.setId(resultSet.getInt("id"));
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * Marks a notification as read in the database.
     *
     * @param id the ID of the notification to be marked as read.
     */
    public void markAsRead(int id) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(MARK_AS_READ)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}