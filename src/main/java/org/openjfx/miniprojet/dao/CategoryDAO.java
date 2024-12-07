package org.openjfx.miniprojet.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import org.openjfx.miniprojet.utiil.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryDAO {

    public void addCategory(String categoryName, String userID) {
        String query = "INSERT INTO categories (category_name, user_id) VALUES (?, ?)";
        try {
            Database.getInstance().executeUpdate(query, categoryName, userID);
        } catch (SQLException e) {
            throw new DataAccessException("Error adding category", e);
        }
    }

    public void deleteCategory(String categoryName, String userID, boolean deleteTask){
        if (deleteTask){
            String deleteTasksQuery = "DELETE FROM tasks WHERE category_id = (SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?)";
            try{
                Database.getInstance().executeUpdate(deleteTasksQuery, categoryName, userID);
            } catch (SQLException e){
                throw new DataAccessException("Error deleting tasks", e);
            }
        } else{
            String updateTasksQuery = "UPDATE tasks SET category_id = (SELECT category_id FROM categories WHERE category_name = 'General' AND user_id = ?) WHERE category_id = (SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?)";
            try{
                Database.getInstance().executeUpdate(updateTasksQuery, userID, categoryName, userID);
            } catch (SQLException e){
                throw new DataAccessException("Error updating tasks", e);
            }
        }
        String deleteCategoryQuery = "DELETE FROM categories WHERE category_name = ? AND user_id = ?";
        try{
            Database.getInstance().executeUpdate(deleteCategoryQuery, categoryName, userID);
        } catch (SQLException e){
            throw new DataAccessException("Error deleting category", e);
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
                    continue;
                }
                tempCategories.add(category);
            }
            if (!generalExists) {
                String addGeneralQuery = "INSERT INTO categories (category_name, user_id) VALUES ('General', ?)";
                Database.getInstance().executeUpdate(addGeneralQuery, userID);
            }
            categories.add("General");
            categories.addAll(tempCategories);
            return categories;
        } catch (SQLException e) {
            throw new DataAccessException("Error loading categories", e);
        }
    }
}
