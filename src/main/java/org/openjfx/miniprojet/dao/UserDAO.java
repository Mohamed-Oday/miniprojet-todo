package org.openjfx.miniprojet.dao;

import org.openjfx.miniprojet.util.Database;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public ResultSet getSavedUser() throws SQLException {
        String query = "SELECT * FROM saveduser";
        return Database.getInstance().executeQuery(query);
    }

    public void saveUser(String username) throws SQLException {
        String query = "INSERT INTO SavedUser (username) VALUES (?)";
        Database.getInstance().executeUpdate(query, username);
    }

    public void deleteUser() throws SQLException {
        String query = "DELETE FROM SavedUser";
        Database.getInstance().executeUpdate(query);
    }

    public boolean registerUser(String username, String password) throws SQLException {
        String checkQuery = "SELECT username FROM Users WHERE username = ?";
        String registerQuery = "INSERT INTO Users (username, password) VALUES (?, ?)";
        ResultSet resultSet = Database.getInstance().executeQuery(checkQuery, username);
        if (resultSet.next()) {
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        Database.getInstance().executeUpdate(registerQuery, username, hashedPassword);
        return true;
    }

    public boolean isValidLogin(String username, String password) throws SQLException {
        String query = "SELECT password FROM Users WHERE username = ?";
        ResultSet resultSet = Database.getInstance().executeQuery(query, username);
        if (resultSet.next()){
            String hashedPassword = resultSet.getString("password");
            return BCrypt.checkpw(password, hashedPassword);
        }
        return false;
    }

    public int getTotalTasks(String username) throws SQLException {
        String query = "SELECT total_tasks FROM Users WHERE username = ?";
        ResultSet resultSet = Database.getInstance().executeQuery(query, username);
        if (resultSet.next()) {
            return resultSet.getInt("total_tasks");
        } else {
            throw new SQLException("No user found with username: " + username);
        }
    }

    public int getCompletedTasks(String username) throws SQLException {
        String query = "SELECT completed_tasks FROM Users WHERE username = ?";
        ResultSet resultSet = Database.getInstance().executeQuery(query, username);
        if (resultSet.next()) {
            return resultSet.getInt("completed_tasks");
        } else {
            throw new SQLException("No user found with username: " + username);
        }
    }

    public int getAbandonedTasks(String username) throws SQLException {
        String query = "SELECT abondoned_tasks FROM Users WHERE username = ?";
        ResultSet resultSet = Database.getInstance().executeQuery(query, username);
        if (resultSet.next()) {
            return resultSet.getInt("abondoned_tasks");
        } else {
            throw new SQLException("No user found with username: " + username);
        }
    }

    public int getDeletedTasks(String username) throws SQLException {
        String query = "SELECT deleted_tasks FROM Users WHERE username = ?";
        ResultSet resultSet = Database.getInstance().executeQuery(query, username);
        if (resultSet.next()) {
            return resultSet.getInt("deleted_tasks");
        } else {
            throw new SQLException("No user found with username: " + username);
        }
    }

    public double getCompletionRate(String username) throws SQLException {
        int totalTasks = getTotalTasks(username);
        int completedTasks = getCompletedTasks(username);
        if (totalTasks == 0) {
            return 0;
        }
        // returning a number like 98.5 instead of 98.543245
        return Math.round(((double) completedTasks / totalTasks) * 1000) / 10.0;
    }

    public void incrementCompletedTasks(String username) throws SQLException {
        int completedTasks = getCompletedTasks(username) + 1;
        String query = "UPDATE Users SET completed_tasks = ? WHERE username = ?";
        Database.getInstance().executeUpdate(query, completedTasks, username);
    }

    public void decrementCompletedTasks(String username) throws SQLException {
        int completedTasks = getCompletedTasks(username) - 1;
        String query = "UPDATE Users SET completed_tasks = ? WHERE username = ?";
        Database.getInstance().executeUpdate(query, completedTasks, username);
    }


    public void incrementAbandonedTasks(String username) throws SQLException {
        int abandonedTasks = getAbandonedTasks(username) + 1;
        String query = "UPDATE Users SET abondoned_tasks = ? WHERE username = ?";
        Database.getInstance().executeUpdate(query, abandonedTasks, username);
    }

    public void decrementAbandonedTasks(String username) throws SQLException {
        int abandonedTasks = getAbandonedTasks(username) - 1;
        String query = "UPDATE Users SET abondoned_tasks = ? WHERE username = ?";
        Database.getInstance().executeUpdate(query, abandonedTasks, username);
    }

    public void updateTotalTasks(String username) throws SQLException {
        int totalTasks = getTotalTasks(username) + 1;
        String query = "UPDATE Users SET total_tasks = ? WHERE username = ?";
        Database.getInstance().executeUpdate(query, totalTasks, username);
    }

    public void incrementDeletedTasks(String username) throws SQLException {
        int deletedTasks = getDeletedTasks(username) + 1;
        String query = "UPDATE Users SET deleted_tasks = ? WHERE username = ?";
        Database.getInstance().executeUpdate(query, deletedTasks, username);
    }

    public void updateCompletionRate(String username) throws SQLException {
        double completionRate = getCompletionRate(username);
        String query = "UPDATE Users SET completion_rate = ? WHERE username = ?";
        Database.getInstance().executeUpdate(query, completionRate, username);
    }
}
