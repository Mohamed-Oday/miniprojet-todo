package org.openjfx.miniprojet.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.openjfx.miniprojet.util.Database;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryDAO {


    private final UserDAO userDAO = new UserDAO();

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

    public void deleteCategory(String categoryName, String userID, boolean deleteTask) throws SQLException {
        if (deleteTask) {
            int taskCount = 0;
            ResultSet resultSet = Database.getInstance().executeQuery("SELECT COUNT(*) FROM tasks WHERE task_categoryID = (SELECT category_id FROM categories WHERE category_name = ? AND user_id = ? AND (task_status != 'Completed' OR task_status != 'Abandoned'))", "Error getting task count", categoryName, userID);
            if (resultSet.next()) {
                taskCount = resultSet.getInt(1);
            }
            for (int i = 0 ; i < taskCount ; i++) {
                userDAO.incrementDeletedTasks(userID);
            }
            executeUpdate("DELETE FROM tasks WHERE task_categoryID = (SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?)", "Error deleting tasks", categoryName, userID);
        } else {
            executeUpdate("UPDATE tasks SET task_categoryID = (SELECT category_id FROM categories WHERE category_name = 'General' AND user_id = ?) WHERE task_categoryID = (SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?)", "Error updating tasks", userID, categoryName, userID);
        }
        executeUpdate("DELETE FROM categories WHERE category_name = ? AND user_id = ?", "Error deleting category", categoryName, userID);
    }

    public int getTaskCount(String categoryName, String userID) {
        try {
            ResultSet resultSet = Database.getInstance().executeQuery("SELECT COUNT(*) FROM tasks WHERE task_categoryID = (SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?)", categoryName, userID);
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error getting task count", e);
        }
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

    public ObservableList<String> loadCollabCategories(String userID) {
        ObservableList<String> categories = FXCollections.observableArrayList();
        String query = "SELECT category_name, owner_name FROM category_collaboration WHERE collaborated_user = ? AND is_accepted = 1";
        try {
            ResultSet resultSet = Database.getInstance().executeQuery(query, userID);
            while (resultSet.next()) {
                String category = resultSet.getString("category_name");
                String owner = resultSet.getString("owner_name");
                categories.add(category + " (" + owner + ")");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error loading categories", e);
        }
        System.out.println(categories);
        return categories;
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
