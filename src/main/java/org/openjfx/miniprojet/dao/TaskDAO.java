package org.openjfx.miniprojet.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import org.openjfx.miniprojet.model.Status;
import org.openjfx.miniprojet.model.TaskImpl;
import org.openjfx.miniprojet.model.TaskListImpl;
import org.openjfx.miniprojet.utiil.Database;

import java.sql.*;
import java.time.LocalDate;

public class TaskDAO {

    public void deleteTask(TaskImpl task) {
        String deleteQuery = "DELETE FROM tasks WHERE task_id = ?";
        try{
            Database.getInstance().executeUpdate(deleteQuery, task.getId());
        } catch (SQLException e){
            throw new DataAccessException("Error deleting task", e);
        }
    }

    public void updateTask(TaskImpl task, String userID) throws DataAccessException{
        String updateQuery = "UPDATE tasks SET task_name = ?, task_description = ?, task_dueDate = ?, task_status = ?, task_priority = ?, category_id = ? WHERE task_id = ?";
        int categoryID = getCategoryID(task.getCategory(), userID);
        Object[] params = {
                task.getName(),
                task.getDescription(),
                Date.valueOf(task.getDueDate()),
                task.getStatus().toString(),
                task.getPriority(),
                categoryID,
                task.getId(),
        };
        try {
            Database.getInstance().executeUpdate(updateQuery, params);
        } catch (SQLException e){
            throw new DataAccessException("Error updating task", e);
        }
    }

    private int getCategoryID(String categoryName, String userID){
        String query = "SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?";
        try (PreparedStatement preparedStatement = Database.getInstance().getConnection().prepareStatement(query)){
            preparedStatement.setString(1, categoryName);
            preparedStatement.setString(2, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt("category_id");
            }
        } catch (SQLException e){
            System.out.println("Category not found");
        }
        return -1;
    }

    public ObservableList<TaskImpl> loadTasks(String userID, AnchorPane visiblePane, String categoryName) throws DataAccessException{
        String query = getTasksQuery(userID, visiblePane);
        Object[] params = getQueryParams(userID, visiblePane, categoryName);
        TaskListImpl tasks = new TaskListImpl(FXCollections.observableArrayList());

        try (ResultSet resultSet  = Database.getInstance().executeQuery(query, params)){
            if (visiblePane.getId().equals("categoryTasksPane")){
                return loadTasksByCategory(categoryName, userID);
            }
            while (resultSet.next()){
                int taskID = resultSet.getInt("task_id");
                String taskName = resultSet.getString("task_name");
                String taskDescription = resultSet.getString("task_description");
                LocalDate taskDueDate = resultSet.getDate("task_dueDate").toLocalDate();
                Status taskStatus = Status.valueOf(resultSet.getString("task_status"));
                String taskPriority = resultSet.getString("task_priority");
                String taskCategory = resultSet.getString("category_name");
                if (taskCategory == null){
                    taskCategory = "General";
                }
                TaskImpl task = new TaskImpl(taskName, taskDescription, taskDueDate, taskStatus, taskPriority, taskCategory);
                task.setId(taskID);
                tasks.addTask(task);
            }
            return tasks.getTasks();
        } catch (SQLException e){
            throw new DataAccessException("Error loading tasks", e);
        }
    }

    public ObservableList<TaskImpl> loadTasksByCategory(String categoryName, String userID){
        String loadTasksQuery = "SELECT tasks.task_id, tasks.task_name, tasks.task_description, tasks.task_dueDate, tasks.task_status, tasks.task_priority "
                + "FROM tasks JOIN categories ON tasks.category_id = categories.category_id "
                + "WHERE tasks.user_id = ? AND categories.category_name = ? AND tasks.task_status NOT IN ('Completed', 'Abandoned')";
        TaskListImpl tasks = new TaskListImpl(FXCollections.observableArrayList());
        try (ResultSet resultSet = Database.getInstance().executeQuery(loadTasksQuery, userID, categoryName)){
            while (resultSet.next()){
                int taskID = resultSet.getInt("task_id");
                String taskName = resultSet.getString("task_name");
                String taskDescription = resultSet.getString("task_description");
                LocalDate taskDueDate = resultSet.getDate("task_dueDate").toLocalDate();
                Status taskStatus = Status.valueOf(resultSet.getString("task_status"));
                String taskPriority = resultSet.getString("task_priority");
                TaskImpl task = new TaskImpl(taskName, taskDescription, taskDueDate, taskStatus, taskPriority, categoryName);
                task.setId(taskID);
                tasks.addTask(task);
            }
            return tasks.getTasks();
        } catch (SQLException e){
            throw new DataAccessException("Error loading tasks", e);
        }
    }

    public void createTasks(String taskName, String description, LocalDate dueDate, Status status, String categoryName, String priority, String userID, boolean isImportant) {
        String insertQuery = "INSERT INTO tasks (user_id, task_name, task_description, task_dueDate, task_status, is_important, task_startDate, category_id, task_priority) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int categoryID = getCategoryID(categoryName, userID);
        try{
            Database.getInstance().executeUpdate(insertQuery, userID, taskName, description, Date.valueOf(dueDate), status.toString(), isImportant ? "1" : "0", Date.valueOf(LocalDate.now()), categoryID, priority);
        } catch (SQLException e){
            throw new DataAccessException("Error creating task", e);
        }
    }

    private Object[] getQueryParams(String userID, AnchorPane visiblePane, String categoryName){
        if (visiblePane.getId().equals("categoryTasksPane")){
            return new Object[]{userID ,categoryName, userID};
        }
        return new Object[]{userID};
    }

    private String getTasksQuery(String userID, AnchorPane visiblePane){
        String loadTasksQuery = "SELECT tasks.task_id, tasks.task_name, tasks.task_description, tasks.task_dueDate, tasks.task_status, tasks.task_priority, categories.category_name "
                + "FROM tasks LEFT JOIN categories ON tasks.category_id = categories.category_id"
                + " WHERE tasks.user_id = ?";

        if (visiblePane.getId().equals("myDayPane")) {
            loadTasksQuery += " AND task_startDate = CURDATE() AND tasks.task_status NOT IN ('Completed', 'Abandoned')";
        } else if (visiblePane.getId().equals("importantPane")) {
            loadTasksQuery += " AND is_important = 1 AND tasks.task_status NOT IN ('Completed', 'Abandoned')";
        } else if (visiblePane.getId().equals("categoryTasksPane")) {
            loadTasksQuery += " AND tasks.category_id = (SELECT categories.category_id FROM categories WHERE categories.category_name = ? AND categories.user_id = ?)";
        }
        return loadTasksQuery;
    }
}
