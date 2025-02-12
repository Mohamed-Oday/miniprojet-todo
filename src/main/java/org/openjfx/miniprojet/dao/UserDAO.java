package org.openjfx.miniprojet.dao;

import com.opencsv.CSVWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openjfx.miniprojet.util.Database;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) for user-related operations.
 */
public class UserDAO {

    /**
     * Retrieves the saved user from the database.
     *
     * @return ResultSet containing the saved user.
     * @throws SQLException if a database access error occurs.
     */
    public ResultSet getSavedUser() throws SQLException {
        String query = "SELECT * FROM saveduser";
        return Database.getInstance().executeQuery(query);
    }

    /**
     * Saves a user to the database.
     *
     * @param username the username to save.
     * @throws SQLException if a database access error occurs.
     */
    public void saveUser(String username) throws SQLException {
        String query = "INSERT INTO SavedUser (username) VALUES (?)";
        Database.getInstance().executeUpdate(query, username);
    }

    /**
     * Exports tasks to a file in JSON format.
     *
     * @param resultSet the ResultSet containing the tasks.
     * @param file the file to write the tasks to.
     * @throws DataAccessException if an error occurs while writing to the file.
     */
    public void exportTasks(ResultSet resultSet, File file) {
        try (FileWriter fw = new FileWriter(file)) {
            JSONArray tasksArray = new JSONArray();

            while (resultSet.next()) {
                JSONObject task = new JSONObject();
                task.put("taskName", resultSet.getString("task_name"));
                task.put("description", resultSet.getString("task_description"));
                task.put("startDate", resultSet.getString("task_startDate"));
                task.put("dueDate", resultSet.getString("task_dueDate"));
                task.put("status", resultSet.getString("task_status"));
                task.put("priority", resultSet.getString("task_priority"));
                task.put("category", resultSet.getString("category_name"));
                task.put("important", resultSet.getString("is_important"));

                tasksArray.put(task);
            }

            JSONObject root = new JSONObject();
            root.put("tasks", tasksArray);

            fw.write(root.toString(2)); // Pretty print with 2-space indentation
        } catch (IOException | SQLException e) {
            throw new DataAccessException("Error writing tasks to file", e);
        }
    }

    /**
     * Deletes all saved users from the database.
     *
     * @throws SQLException if a database access error occurs.
     */
    public void deleteUser() throws SQLException {
        String query = "DELETE FROM SavedUser";
        Database.getInstance().executeUpdate(query);
    }

    /**
     * Registers a new user in the database.
     *
     * @param username the username to register.
     * @param password the password to register.
     * @return true if the user was successfully registered, false if the username already exists.
     * @throws SQLException if a database access error occurs.
     */
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

    /**
     * Validates user login credentials.
     *
     * @param username the username to validate.
     * @param password the password to validate.
     * @return true if the credentials are valid, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean isValidLogin(String username, String password) throws SQLException {
        String query = "SELECT password FROM Users WHERE username = ?";
        ResultSet resultSet = Database.getInstance().executeQuery(query, username);
        if (resultSet.next()){
            String hashedPassword = resultSet.getString("password");
            return BCrypt.checkpw(password, hashedPassword);
        }
        return false;
    }

    /**
     * Retrieves the total number of tasks for a user.
     *
     * @param username the username to query.
     * @return the total number of tasks.
     * @throws SQLException if a database access error occurs or the user is not found.
     */
    public int getTotalTasks(String username) throws SQLException {
        String query = "SELECT total_tasks FROM Users WHERE username = ?";
        ResultSet resultSet = Database.getInstance().executeQuery(query, username);
        if (resultSet.next()) {
            return resultSet.getInt("total_tasks");
        } else {
            throw new SQLException("No user found with username: " + username);
        }
    }

    /**
     * Retrieves the number of completed tasks for a user.
     *
     * @param username the username to query.
     * @return the number of completed tasks.
     * @throws SQLException if a database access error occurs or the user is not found.
     */
    public int getCompletedTasks(String username) throws SQLException {
        String query = "SELECT completed_tasks FROM Users WHERE username = ?";
        ResultSet resultSet = Database.getInstance().executeQuery(query, username);
        if (resultSet.next()) {
            return resultSet.getInt("completed_tasks");
        } else {
            throw new SQLException("No user found with username: " + username);
        }
    }

    /**
     * Retrieves the number of abandoned tasks for a user.
     *
     * @param username the username to query.
     * @return the number of abandoned tasks.
     * @throws SQLException if a database access error occurs or the user is not found.
     */
    public int getAbandonedTasks(String username) throws SQLException {
        String query = "SELECT abondoned_tasks FROM Users WHERE username = ?";
        ResultSet resultSet = Database.getInstance().executeQuery(query, username);
        if (resultSet.next()) {
            return resultSet.getInt("abondoned_tasks");
        } else {
            throw new SQLException("No user found with username: " + username);
        }
    }

    /**
     * Retrieves the number of deleted tasks for a user.
     *
     * @param username the username to query.
     * @return the number of deleted tasks.
     * @throws SQLException if a database access error occurs or the user is not found.
     */
    public int getDeletedTasks(String username) throws SQLException {
        String query = "SELECT deleted_tasks FROM Users WHERE username = ?";
        ResultSet resultSet = Database.getInstance().executeQuery(query, username);
        if (resultSet.next()) {
            return resultSet.getInt("deleted_tasks");
        } else {
            throw new SQLException("No user found with username: " + username);
        }
    }

    /**
     * Calculates and retrieves the completion rate for a user.
     *
     * @param username the username to query.
     * @return the completion rate as a percentage.
     * @throws SQLException if a database access error occurs.
     */
    public double getCompletionRate(String username) throws SQLException {
        int totalTasks = getTotalTasks(username);
        int completedTasks = getCompletedTasks(username);
        if (totalTasks == 0) {
            return 0;
        }
        // returning a number like 98.5 instead of 98.543245
        return Math.round(((double) completedTasks / totalTasks) * 1000) / 10.0;
    }

    /**
     * Increments the number of completed tasks for a user.
     *
     * @param username the username to update.
     * @throws SQLException if a database access error occurs.
     */
    public void incrementCompletedTasks(String username) throws SQLException {
        int completedTasks = getCompletedTasks(username) + 1;
        String query = "UPDATE Users SET completed_tasks = ? WHERE username = ?";
        Database.getInstance().executeUpdate(query, completedTasks, username);
    }

    /**
     * Decrements the number of completed tasks for a user.
     *
     * @param username the username to update.
     * @throws SQLException if a database access error occurs.
     */
    public void decrementCompletedTasks(String username) throws SQLException {
        int completedTasks = getCompletedTasks(username) - 1;
        String query = "UPDATE Users SET completed_tasks = ? WHERE username = ?";
        Database.getInstance().executeUpdate(query, completedTasks, username);
    }

    /**
     * Increments the number of abandoned tasks for a user.
     *
     * @param username the username to update.
     * @throws SQLException if a database access error occurs.
     */
    public void incrementAbandonedTasks(String username) throws SQLException {
        int abandonedTasks = getAbandonedTasks(username) + 1;
        String query = "UPDATE Users SET abondoned_tasks = ? WHERE username = ?";
        Database.getInstance().executeUpdate(query, abandonedTasks, username);
    }

    /**
     * Decrements the number of abandoned tasks for a user.
     *
     * @param username the username to update.
     * @throws SQLException if a database access error occurs.
     */
    public void decrementAbandonedTasks(String username) throws SQLException {
        int abandonedTasks = getAbandonedTasks(username) - 1;
        String query = "UPDATE Users SET abondoned_tasks = ? WHERE username = ?";
        Database.getInstance().executeUpdate(query, abandonedTasks, username);
    }

    /**
     * Updates the total number of tasks for a user.
     *
     * @param username the username to update.
     * @throws SQLException if a database access error occurs.
     */
    public void updateTotalTasks(String username) throws SQLException {
        int totalTasks = getTotalTasks(username) + 1;
        String query = "UPDATE Users SET total_tasks = ? WHERE username = ?";
        Database.getInstance().executeUpdate(query, totalTasks, username);
    }

    /**
     * Increments the number of deleted tasks for a user.
     *
     * @param username the username to update.
     * @throws SQLException if a database access error occurs.
     */
    public void incrementDeletedTasks(String username) throws SQLException {
        int deletedTasks = getDeletedTasks(username) + 1;
        String query = "UPDATE Users SET deleted_tasks = ? WHERE username = ?";
        Database.getInstance().executeUpdate(query, deletedTasks, username);
    }

    /**
     * Updates the completion rate for a user.
     *
     * @param username the username to update.
     * @throws SQLException if a database access error occurs.
     */
    public void updateCompletionRate(String username) throws SQLException {
        double completionRate = getCompletionRate(username);
        String query = "UPDATE Users SET completion_rate = ? WHERE username = ?";
        Database.getInstance().executeUpdate(query, completionRate, username);
    }
}