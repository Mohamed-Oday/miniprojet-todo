package org.openjfx.miniprojet.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openjfx.miniprojet.controller.AppController;
import org.openjfx.miniprojet.model.Status;
import org.openjfx.miniprojet.model.TaskImpl;
import org.openjfx.miniprojet.model.TaskListImpl;
import org.openjfx.miniprojet.util.Database;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

public class TaskDAO {

    private final UserDAO userDAO = new UserDAO();
    private AppController appController;

    public void deleteTask(TaskImpl task){
        String deleteQuery = "DELETE FROM tasks WHERE task_id = ?";
        try {
            Database.getInstance().executeUpdate(deleteQuery, task.getId());
        }catch (SQLException e){
            throw new DataAccessException("Error deleting task", e);
        }
    }

    public void exportTasks(File file, String userID){
        String exportQuery =
                "SELECT task_name, task_description, task_startDate, task_dueDate, task_status, task_priority, categories.category_name, is_important " +
                "FROM tasks " +
                "LEFT JOIN categories ON tasks.task_categoryID = categories.category_id " +
                "WHERE tasks.user_id = ?";
        try (ResultSet resultSet = Database.getInstance().executeQuery(exportQuery, userID)){
            userDAO.exportTasks(resultSet, file);
        } catch (SQLException e){
            throw new DataAccessException("Error exporting tasks", e);
        }
    }

    public void importTasks(File file, String userID) {
        try (FileReader reader = new FileReader(file)){
            JSONObject root = new JSONObject(new JSONTokener(reader));
            JSONArray tasks = root.getJSONArray("tasks");

            for (int i = 0 ; i < tasks.length() ; i++){
                JSONObject task = tasks.getJSONObject(i);
                String category = task.getString("category");

                Integer categoryID = getCategoryID(category, userID);

                String insertQuery = "INSERT INTO tasks (task_name, task_description, task_startDate, task_dueDate, " +
                                "task_status, task_priority, task_categoryID, is_important, user_id) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                Database.getInstance().executeUpdate(insertQuery,
                        task.getString("taskName"),
                        task.getString("description"),
                        task.getString("startDate"),
                        task.getString("dueDate"),
                        task.getString("status"),
                        task.getString("priority"),
                        categoryID,
                        task.getString("important"),
                        userID
                );
            }

        } catch (IOException | SQLException e) {
            throw new DataAccessException("Error importing tasks", e);
        }
    }


    public Map<String, Integer> getLastSevenDaysData(String userID) {
        Map<String, Integer> data = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");

        try {
            // Get connection from your Database class
            Connection conn = Database.getInstance().getConnection();

            for (int i = 6; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                String formattedDate = date.format(formatter);

                // Create SQL query based on the type
                String sql = "SELECT COUNT(*) FROM tasks " +
                        "WHERE user_id = ? " +
                        "AND DATE(completion_date) = ? " +  // Use completion_date instead of date_recorded
                        "AND task_status = 'Completed'";        // Add explicit status check

                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, userID); // userID is the current logged-in user
                statement.setDate(2, java.sql.Date.valueOf(date));

                ResultSet rs = statement.executeQuery();

                // If we have data for this date, use it; otherwise use 0
                int value = rs.next() ? rs.getInt(1) : 0;
                data.put(formattedDate, value);

                rs.close();
                statement.close();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching chart data", e);
        }
        return data;
    }

    public void setCompletionDate(TaskImpl task){
        String updateQuery = "UPDATE tasks SET completion_date = CURDATE() WHERE task_id = ?";
        try {
            Database.getInstance().executeUpdate(updateQuery, task.getId());
        } catch (SQLException e){
            throw new DataAccessException("Error setting completion date", e);
        }
    }

    public void updateTask(TaskImpl task, String userID, boolean isOverRidden) throws DataAccessException, SQLException {
        String updateQuery = getString(isOverRidden);
        int categoryID = getCategoryID(task.getCategory(), userID);
        Object[] params = {
                task.getName(),
                task.getDescription(),
                task.getStartDate(),
                task.getDueDate(),
                task.getStatus().toString(),
                task.getPriority(),
                categoryID,
                task.isImportant(),
                task.getReminder(),
                task.getId(),
        };
        try {
            Database.getInstance().executeUpdate(updateQuery, params);
        } catch (SQLException e){
            throw new DataAccessException("Error updating task", e);
        }
    }

    private static String getString(boolean isOverRidden) {
        String updateQuery;
        if (isOverRidden){
            updateQuery = "UPDATE tasks SET task_name = ?, task_description = ?, task_startDate = ?, task_dueDate = ?, task_status = ?, task_priority = ?, task_categoryID = ?, is_important = ?, reminder = ?, status_overriden = 1 WHERE task_id = ?";
        }else{
            updateQuery = "UPDATE tasks SET task_name = ?, task_description = ?, task_startDate = ?, task_dueDate = ?, task_status = ?, task_priority = ?, task_categoryID = ?, is_important = ?, reminder = ? WHERE task_id = ?";
        }
        return updateQuery;
    }

    public Integer getCategoryID(String categoryName, String userID) throws SQLException {
        String selectQuery = "SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?";
        try (ResultSet resultSet = Database.getInstance().executeQuery(selectQuery, categoryName, userID)) {
            if (resultSet.next()) {
                return resultSet.getInt("category_id");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String insertQuery = "INSERT INTO categories (category_name, user_id) VALUES (?, ?)";
        return Database.getInstance().executeInsert(insertQuery, categoryName, userID);
    }

    public ObservableList<TaskImpl> loadTasks(String userID, String currentPage, String pageTitle) throws DataAccessException{
        updateTasksStatus(userID);
        // T1, Category, General
        String query = getTasksQuery(currentPage);
        Object[] params = getQueryParams(userID, currentPage, pageTitle);
        // T1, General, T1
        TaskListImpl tasks = new TaskListImpl(FXCollections.observableArrayList());

        try (ResultSet resultSet  = Database.getInstance().executeQuery(query, params)){
            if (currentPage.equals("Category")){
                return loadTasksByCategory(pageTitle, userID); // General -> T1
            }
            while (resultSet.next()){
                int taskID = resultSet.getInt("task_id");
                String taskName = resultSet.getString("task_name");
                String taskDescription = resultSet.getString("task_description");
                LocalDate taskStartDate = resultSet.getDate("task_startDate").toLocalDate();
                LocalDate taskDueDate = resultSet.getDate("task_dueDate").toLocalDate();
                Status taskStatus = Status.valueOf(resultSet.getString("task_status"));
                String taskPriority = resultSet.getString("task_priority");
                String taskCategory = resultSet.getString("category_name");
                boolean isImportant = resultSet.getBoolean("is_important");
                int reminder = resultSet.getInt("reminder");
                if (taskCategory == null){
                    taskCategory = "General";
                }
                TaskImpl task = new TaskImpl(taskName, taskDescription, taskDueDate, taskStatus, taskPriority, taskCategory, taskStartDate, isImportant, userID, reminder);
                task.setId(taskID);
                loadTaskComments(task);
                tasks.addTask(task);
            }
            return tasks.getTasks();
        } catch (SQLException e){
            throw new DataAccessException("Error loading tasks", e);
        }
    }

    public void resetToAutoStatus(TaskImpl task){
        String updateQuery = "UPDATE tasks SET status_overriden = 0 WHERE task_id = ?";
        try{
            Database.getInstance().executeUpdate(updateQuery, task.getId());
        } catch (SQLException e){
            throw new DataAccessException("Error resetting status", e);
        }
    }

    public void updateTasksStatus(String userID){
        String updateQueryAbandoned = "UPDATE tasks SET task_status = 'Abandoned' WHERE task_dueDate < CURDATE() AND task_status = 'Started' AND user_id = ? AND status_overriden = 0";
        String updateQueryStarted = "UPDATE tasks SET task_status = 'Started' WHERE task_startDate <= CURDATE() AND task_dueDate >= CURDATE() AND (task_status = 'Pending' OR task_status = 'Abandoned') AND user_id = ? AND status_overriden = 0";
        String updateQueryPending = "UPDATE tasks SET task_status = 'Pending' WHERE task_startDate > CURDATE() AND user_id = ? AND status_overriden = 0";
        try{
            Database.getInstance().executeUpdate(updateQueryAbandoned, userID);
            Database.getInstance().executeUpdate(updateQueryStarted, userID);
            Database.getInstance().executeUpdate(updateQueryPending, userID);
        } catch (SQLException e){
            throw new DataAccessException("Error updating tasks status", e);
        }
    }

    public void loadTaskComments(TaskImpl task){
        String loadCommentsQuery = "SELECT comment FROM comments WHERE task_id = ?";
        try (ResultSet resultSet = Database.getInstance().executeQuery(loadCommentsQuery, task.getId())){
            System.out.println("Loading comments for: " +task.getName());
            while (resultSet.next()){
                task.addComment(resultSet.getString("comment"));
            }
        } catch (SQLException e){
            throw new DataAccessException("Error loading comments", e);
        }
    }

    public ObservableList<TaskImpl> loadTasksByCategory(String categoryName, String userID){
        String loadTasksQuery = "SELECT tasks.task_id, tasks.task_name, tasks.task_description, tasks.task_dueDate, tasks.task_status, tasks.task_priority, tasks.task_startDate, tasks.is_important, tasks.reminder "
                + "FROM tasks JOIN categories ON tasks.task_categoryID = categories.category_id "
                + "WHERE tasks.user_id = ? AND categories.category_name = ? AND tasks.task_status NOT IN ('Completed', 'Abandoned')";
        TaskListImpl tasks = new TaskListImpl(FXCollections.observableArrayList());
        try (ResultSet resultSet = Database.getInstance().executeQuery(loadTasksQuery, userID, categoryName)){
            while (resultSet.next()){
                int taskID = resultSet.getInt("task_id");
                String taskName = resultSet.getString("task_name");
                String taskDescription = resultSet.getString("task_description");
                LocalDate taskStartDate = resultSet.getDate("task_startDate").toLocalDate();
                LocalDate taskDueDate = resultSet.getDate("task_dueDate").toLocalDate();
                Status taskStatus = Status.valueOf(resultSet.getString("task_status"));
                String taskPriority = resultSet.getString("task_priority");
                boolean isImportant = resultSet.getBoolean("is_important");
                int reminder = resultSet.getInt("reminder");
                TaskImpl task = new TaskImpl(taskName, taskDescription, taskDueDate, taskStatus, taskPriority, categoryName, taskStartDate, isImportant, userID, reminder);
                task.setId(taskID);
                loadTaskComments(task);
                tasks.addTask(task);
            }
            return tasks.getTasks();
        } catch (SQLException e){
            throw new DataAccessException("Error loading tasks", e);
        }
    }

    public void createTasks(String taskName, String description, LocalDate dueDate, LocalDate startDate, Status status, String categoryName, String priority, String userID, boolean isImportant, int reminder) throws SQLException {
        String insertQuery = "INSERT INTO tasks (user_id, task_name, task_description, task_dueDate, task_status, is_important, task_startDate, task_categoryID, task_priority, reminder) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int categoryID = getCategoryID(categoryName, userID);
        try{
            Database.getInstance().executeUpdate(
                    insertQuery,
                    userID,
                    taskName,
                    description,
                    Date.valueOf(dueDate),
                    status.toString(),
                    isImportant ? "1" : "0",
                    Date.valueOf(startDate),
                    categoryID,
                    priority,
                    reminder
            );
            userDAO.updateTotalTasks(userID);
        } catch (SQLException e){
            throw new DataAccessException("Error creating task", e);
        }
    }

    public ObservableList<TaskImpl> loadCollabTasksByCategory(String userID, String categoryName, String owner) throws SQLException {
        String checkQuery = "SELECT * from category_collaboration WHERE collaborated_user = ? AND category_name = ? AND owner_name = ? AND is_accepted = 1";
        ResultSet resultSet = Database.getInstance().executeQuery(checkQuery, userID, categoryName, owner);
        ObservableList<TaskImpl> tasks = FXCollections.observableArrayList();

        if (resultSet.next()) {
            tasks.addAll(loadTasks(owner, "Category", categoryName));
        }

        return tasks;
    }

    public void updateCollaborativeTask(TaskImpl task, String owner, boolean isOverriden){
        try{
            if (!isCollaborativeTask(task, owner)){
                throw new DataAccessException("Collaborative task does not exist", null);
            }
            String updateQuery = getString(isOverriden);
            int categoryID = getCategoryID(task.getCategory(), owner);
            Object[] params = {
                    task.getName(),
                    task.getDescription(),
                    task.getStartDate(),
                    task.getDueDate(),
                    task.getStatus().toString(),
                    task.getPriority(),
                    categoryID,
                    task.isImportant(),
                    task.getId(),
            };
            Database.getInstance().executeUpdate(updateQuery, params);
        } catch (SQLException e){
            throw new DataAccessException("Error updating collaborative task", e);
        }
    }

    public void deleteCollaborativeTask(TaskImpl task, String owner) {
        if (!isCollaborativeTask(task, owner)){
            throw new DataAccessException("Collaborative task does not exist", null);
        }
        deleteTask(task);
    }

    public boolean isCollaborativeTask(TaskImpl task, String ownerID) {
        String query = "SELECT * FROM category_collaboration WHERE category_name = ? AND owner_name = ?";
        try {
            ResultSet resultSet = Database.getInstance().executeQuery(query,
                    task.getCategory(),
                    ownerID);
            return resultSet.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public void addComment(String comment, TaskImpl task, String userID) {
        String insertQuery = "INSERT INTO comments (comment, task_id, user_id, creation_date) VALUES (?, ?, ?, ?)";
        try{
            Database.getInstance().executeUpdate(insertQuery, comment, task.getId(), userID, Date.valueOf(LocalDate.now()));
            task.addComment(comment);
        } catch (SQLException e){
            throw new DataAccessException("Error adding comment", e);
        }
    }

    private Object[] getQueryParams(String userID, String currentPage, String categoryName){
        if (currentPage.equals("Category")){
            return new Object[]{userID ,categoryName, userID};
        }
        return new Object[]{userID};
    }

    private String getTasksQuery(String currentPage){
        String loadTasksQuery = "SELECT tasks.task_id, tasks.task_name, tasks.task_description, tasks.task_dueDate, tasks.task_status, tasks.task_priority, categories.category_name, tasks.task_startDate, tasks.is_important, tasks.reminder "
                + "FROM tasks LEFT JOIN categories ON tasks.task_categoryID = categories.category_id"
                + " WHERE tasks.user_id = ?";

        switch (currentPage) {
            case "My Day" ->
                    loadTasksQuery += " AND task_startDate = CURDATE() AND tasks.task_status NOT IN ('Completed', 'Abandoned')";
            case "Important" ->
                    loadTasksQuery += " AND is_important = 1 AND tasks.task_status NOT IN ('Completed', 'Abandoned')";
            case "Category" ->
                    loadTasksQuery += " AND tasks.task_categoryID = (SELECT categories.category_id FROM categories WHERE categories.category_name = ? AND categories.user_id = ?)";
        }
        return loadTasksQuery;
    }

    public void checkUpcomingTasks(String userID, AppController controller) {
        System.out.println("Checking upcoming tasks");
        String query = "SELECT * FROM tasks WHERE user_id = ? AND task_status = 'Pending' AND task_startDate > CURDATE()";
        appController = controller;
        try (ResultSet resultSet = Database.getInstance().executeQuery(query, userID)) {
            while (resultSet.next()) {
                LocalDate startDate = resultSet.getDate("task_startDate").toLocalDate();
                // Checking for 1 day, 3 days, 1 week
                long days = ChronoUnit.DAYS.between(LocalDate.now(), startDate);
                System.out.println("Task: " + resultSet.getString("task_name") + " is starting in " + days + " days");

                if (days == 1) {
                    appController.alertUpComingTasks(resultSet.getString("task_name"), "Task is starting tomorrow");
                } else if (days == 3) {
                    appController.alertUpComingTasks(resultSet.getString("task_name"), "Task is starting in 3 days");
                } else if (days == 7) {
                    appController.alertUpComingTasks(resultSet.getString("task_name"), "Task is starting in a week");
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error checking upcoming tasks", e);
        }
    }

    public void checkOverdueTasks(String userID, AppController controller) {
        System.out.println("Checking overdue tasks");
        String query = "SELECT * FROM tasks WHERE user_id = ? AND task_status = 'Started' AND task_dueDate > CURDATE()";
        appController = controller;
        try (ResultSet resultSet = Database.getInstance().executeQuery(query, userID)) {
            while (resultSet.next()) {
                LocalDate dueDate = resultSet.getDate("task_dueDate").toLocalDate();
                long days = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
                System.out.println("Task: " + resultSet.getString("task_name") + " is overdue by " + days + " days");
                if (days == 1) {
                    appController.alertOverdueTasks(resultSet.getString("task_name"), "Task is due tomorrow");
                } else if (days == 3) {
                    appController.alertOverdueTasks(resultSet.getString("task_name"), "Task is due in 3 days");
                } else if (days == 7) {
                    appController.alertOverdueTasks(resultSet.getString("task_name"), "Task is due in a week");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error checking overdue tasks", e);
        }
    }
}
