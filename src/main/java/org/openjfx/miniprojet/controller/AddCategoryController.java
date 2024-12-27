package org.openjfx.miniprojet.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.openjfx.miniprojet.dao.CategoryDAO;
import org.openjfx.miniprojet.dao.DataAccessException;


public class AddCategoryController {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @FXML private TextField categoryName;

    private Stage addCategoryStage;
    private AppController appController;
    private String userID;

    public void setAddCategoryStage(Stage addCategoryStage) {
        this.addCategoryStage = addCategoryStage;
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @FXML
    public void handleAddCategory() {
        String name = categoryName.getText();
        if (name != null && !name.isEmpty()) {
            insertCategory(name);
            categoryName.clear();
        }
    }

    @FXML
    public void handleCancel() {
        categoryName.clear();
        addCategoryStage.close();
    }

    public void insertCategory(String name) {
        try{
            categoryDAO.addCategory(name, userID);
        } catch (DataAccessException e){
            System.err.println("Error inserting category: " + e.getMessage());
        }
        categoryName.clear();
        addCategoryStage.close();
        appController.loadCategories();
    }
}
