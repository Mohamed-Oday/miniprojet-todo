package org.openjfx.miniprojet.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.openjfx.miniprojet.util.Database;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) class for managing categories.
 * This class provides methods to add, delete, and retrieve categories from the database.
 */
public class CategoryDAO {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Adds a new category for the specified user.
     *
     * @param categoryName the name of the category to add
     * @param userID the ID of the user
     * @throws DataAccessException if an error occurs while adding the category
     */
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

    /**
     * Deletes a category for the specified user.
     * If deleteTask is true, all tasks under the category will also be deleted.
     * Otherwise, tasks will be moved to the "General" category.
     *
     * @param categoryName the name of the category to delete
     * @param userID the ID of the user
     * @param deleteTask whether to delete tasks under the category
     * @throws SQLException if a database access error occurs
     * @throws DataAccessException if an error occurs while deleting the category
     */
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

    /**
     * Gets the count of tasks under a specific category for the specified user.
     *
     * @param categoryName the name of the category
     * @param userID the ID of the user
     * @return the count of tasks under the category
     * @throws DataAccessException if an error occurs while getting the task count
     */
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

    /**
     * Loads the categories for the specified user.
     * Ensures that the "General" category exists and is always the first in the list.
     *
     * @param userID the ID of the user
     * @return an observable list of category names
     * @throws DataAccessException if an error occurs while loading the categories
     */
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

    /**
     * Loads the categories that the specified user is collaborating on.
     *
     * @param userID the ID of the user
     * @return an observable list of collaborated category names with their owners
     * @throws DataAccessException if an error occurs while loading the collaborated categories
     */
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

    /**
     * Gets the name of a category by its ID.
     *
     * @param categoryID the ID of the category
     * @return the name of the category
     * @throws DataAccessException if an error occurs while getting the category name
     */
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

    /**
     * Executes an update query with the specified parameters.
     *
     * @param query the SQL query to execute
     * @param errorMessage the error message to throw if an error occurs
     * @param params the parameters for the query
     * @throws DataAccessException if an error occurs while executing the update
     */
    private void executeUpdate(String query, String errorMessage, Object... params) {
        try {
            Database.getInstance().executeUpdate(query, params);
        } catch (SQLException e) {
            throw new DataAccessException(errorMessage, e);
        }
    }

}