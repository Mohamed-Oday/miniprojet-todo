package org.openjfx.miniprojet.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.openjfx.miniprojet.model.Status;
import org.openjfx.miniprojet.model.TaskImpl;
import org.openjfx.miniprojet.utiil.Database;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class CategoryDAO {

    TaskDAO taskDAO = new TaskDAO();

    public void addCategory(String categoryName, String userID) {
        try {
            ResultSet resultSet = Database.getInstance().executeQuery("SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?", categoryName, userID);
            if (resultSet.next()) {
                throw new DataAccessException("Category already exists for this user", null);
            }
            executeUpdate("INSERT INTO categories (category_name, user_id) VALUES (?, ?)", "Error adding category", categoryName, userID);
        } catch (SQLException e) {
            throw new DataAccessException("Error adding category", e);
        }
    }

    public void deleteCategory(String categoryName, String userID, boolean deleteTask) {
        if (deleteTask) {
            executeUpdate("DELETE FROM tasks WHERE category_id = (SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?)", "Error deleting tasks", categoryName, userID);
        } else {
            executeUpdate("UPDATE tasks SET category_id = (SELECT category_id FROM categories WHERE category_name = 'General' AND user_id = ?) WHERE category_id = (SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?)", "Error updating tasks", userID, categoryName, userID);
        }
        executeUpdate("DELETE FROM categories WHERE category_name = ? AND user_id = ?", "Error deleting category", categoryName, userID);
    }

    public ObservableList<String> loadCategories(String userID) {
        ObservableList<String> categories = FXCollections.observableArrayList();
        ObservableList<String> tempCategories = FXCollections.observableArrayList();
        boolean generalExists = false;
        String query = "SELECT category_name FROM categories WHERE user_id = ?";
        try {
            ResultSet resultSet = Database.getInstance().executeQuery(query, userID);
            while (resultSet.next()) {
                String category = resultSet.getString("category_name");
                if (category.equals("General")) {
                    generalExists = true;
                } else {
                    tempCategories.add(category);
                }
            }
            if (!generalExists) {
                executeUpdate("INSERT INTO categories (category_name, user_id) VALUES ('General', ?)", "Error adding General category", userID);
            }
            categories.add("General");
            categories.addAll(tempCategories);
        } catch (SQLException e) {
            throw new DataAccessException("Error loading categories", e);
        }
        return categories;
    }

    public void generateInviteCode(String userID, String categoryName, String inviteCode) {
        int categoryID = taskDAO.getCategoryID(categoryName, userID);
        String query = "INSERT INTO invite_codes (category_id, invite_code, created_by) VALUES (?, ?, ?)";
        executeUpdate(query, "Error generating invite code", categoryID, inviteCode, userID);
        StringSelection selection = new StringSelection(inviteCode);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
        System.out.println("Invite code: " + inviteCode);
    }

    public void insertInviteCode(String inviteCode, String userID) {
        try {
            ResultSet resultSet = Database.getInstance().executeQuery("SELECT category_id, created_by FROM invite_codes WHERE invite_code = ?", inviteCode);
            if (!resultSet.next()) {
                throw new DataAccessException("Invalid invite code", null);
            }
            int categoryID = resultSet.getInt("category_id");
            String originalUserID = resultSet.getString("created_by");
            String categoryName = getCategoryName(categoryID);
            executeUpdate(
                    "INSERT INTO categories (category_name, user_id) VALUES (?, ?)",
                    "Error creating shared category",
                    categoryName, userID
            );
            executeUpdate(
                    "DELETE FROM invite_codes WHERE invite_code = ?",
                    "Error deleting invite code",
                    inviteCode
            );

            ResultSet tasks = Database.getInstance().executeQuery("SELECT * FROM tasks WHERE category_id = ? AND user_id = ?", categoryID, originalUserID);
            while (tasks.next()) {
                String name = tasks.getString("task_name");
                String description = tasks.getString("task_description");
                String priority = tasks.getString("task_priority");
                String status = tasks.getString("task_status");
                LocalDate startDate = tasks.getDate("task_startDate").toLocalDate();
                LocalDate dueDate = tasks.getDate("task_dueDate").toLocalDate();
                boolean important = tasks.getBoolean("is_important");
                taskDAO.createTasks(name, description, dueDate, startDate, Status.valueOf(status), categoryName, priority, userID, important);
            }
            loadCategories(userID);
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting invite code", e);
        }
    }

    public void updateSharedTasks(TaskImpl task, String userID) {
        try {
            ResultSet resultSet = Database.getInstance().executeQuery(
                "SELECT c.category_id, c.user_id FROM categories c " +
                "WHERE c.category_name = ? AND c.user_id != ?", 
                task.getCategory(), userID
            );

            while (resultSet.next()) {
                int sharedCategoryId = resultSet.getInt("category_id");
                String sharedUserId = resultSet.getString("user_id");
                
                // Check if task exists for the shared user
                ResultSet taskExists = Database.getInstance().executeQuery(
                    "SELECT task_id FROM tasks WHERE task_name = ? AND category_id = ?",
                    task.getName(), sharedCategoryId
                );

                if (taskExists.next()) {
                    // Update existing task
                    executeUpdate(
                        "UPDATE tasks SET task_description = ?, task_startDate = ?, " +
                        "task_dueDate = ?, task_status = ?, task_priority = ?, " +
                        "is_important = ?, status_overriden = ? " +
                        "WHERE task_name = ? AND category_id = ?",
                        "Error updating shared task",
                        task.getDescription(), task.getStartDate(), task.getDueDate(),
                        task.getStatus().toString(), task.getPriority(), task.isImportant(),
                        0, task.getName(), sharedCategoryId
                    );
                } else {
                    // Create new task for shared user
                    executeUpdate(
                        "INSERT INTO tasks (user_id, task_name, task_description, " +
                        "task_startDate, task_dueDate, task_status, task_priority, " +
                        "category_id, is_important) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        "Error creating shared task",
                        sharedUserId, task.getName(), task.getDescription(),
                        task.getStartDate(), task.getDueDate(), task.getStatus().toString(),
                        task.getPriority(), sharedCategoryId, task.isImportant()
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating shared tasks", e);
        }
    }

    public boolean isSharedCategory(String categoryName, String userID) {
        try {
            ResultSet resultSet = Database.getInstance().executeQuery(
                "SELECT COUNT(*) as count FROM categories " +
                "WHERE category_name = ? AND user_id != ?",
                categoryName, userID
            );
            if (resultSet.next()) {
                return resultSet.getInt("count") > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DataAccessException("Error checking if category is shared", e);
        }
    }

    private String getCategoryName(int categoryID) {
        try {
            ResultSet resultSet = Database.getInstance().executeQuery("SELECT category_name FROM categories WHERE category_id = ?", categoryID);
            if (!resultSet.next()) {
                throw new DataAccessException("Category not found", null);
            }
            return resultSet.getString("category_name");
        } catch (SQLException e) {
            throw new DataAccessException("Error getting category name", e);
        }
    }

    private void executeUpdate(String query, String errorMessage, Object... params) {
        try {
            Database.getInstance().executeUpdate(query, params);
        } catch (SQLException e) {
            throw new DataAccessException(errorMessage, e);
        }
    }

}
