package org.openjfx.miniprojet.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class AddCategoryController {

    private Stage addCategoryStage;
    private Controller mainController;
    private String userID;

    @FXML
    private TextField categoryName;


    public void setAddCategoryStage(Stage addCategoryStage) {
        this.addCategoryStage = addCategoryStage;
    }

    public void setMainController(Controller mainController) {
        this.mainController = mainController;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @FXML
    public void handleAddCategory(){
        String name = categoryName.getText();
        if (name != null && !name.isEmpty()){
            insertCategory(name);
            categoryName.clear();
        }
    }

    public void insertCategory(String name) {
        String insert = "INSERT INTO categories (user_id, category_name) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(insert)) {
            preparedStatement.setString(1, userID);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        categoryName.clear();
        addCategoryStage.close();
        mainController.loadCategories();
    }
}
