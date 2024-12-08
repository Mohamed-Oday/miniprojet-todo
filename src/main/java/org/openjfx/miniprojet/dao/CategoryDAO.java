package org.openjfx.miniprojet.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.openjfx.miniprojet.utiil.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryDAO {

    public void addCategory(String categoryName, String userID) {
        executeUpdate("INSERT INTO categories (category_name, user_id) VALUES (?, ?)", "Error adding category", categoryName, userID);
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

    private void executeUpdate(String query, String errorMessage, String... params) {
        try {
            Database.getInstance().executeUpdate(query, params);
        } catch (SQLException e) {
            throw new DataAccessException(errorMessage, e);
        }
    }
}
