package org.openjfx.miniprojet.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.openjfx.miniprojet.dao.CategoryDAO;
import org.openjfx.miniprojet.dao.DataAccessException;

/**
 * Controller class for adding a new category.
 * @version 1.0
 * @author Sellami Mohamed Odai
 */
public class AddCategoryController {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @FXML private TextField categoryName;

    private Stage addCategoryStage;
    private AppController appController;
    private String userID;

    /**
     * Sets the stage for adding a category.
     *
     * @param addCategoryStage the stage to set
     */
    public void setAddCategoryStage(Stage addCategoryStage) {
        this.addCategoryStage = addCategoryStage;
    }

    /**
     * Sets the main application controller.
     *
     * @param appController the application controller to set
     */
    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    /**
     * Sets the user ID.
     *
     * @param userID the user ID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Handles the action of adding a category.
     * Retrieves the category name from the text field and inserts it into the database.
     */
    @FXML
    public void handleAddCategory() {
        String name = categoryName.getText();
        if (name != null && !name.isEmpty()) {
            insertCategory(name);
            categoryName.clear();
        }
    }

    /**
     * Handles the action of canceling the add category operation.
     * Clears the text field and closes the stage.
     */
    @FXML
    public void handleCancel() {
        categoryName.clear();
        addCategoryStage.close();
    }

    /**
     * Inserts a new category into the database.
     *
     * @param name the name of the category to insert
     */
    public void insertCategory(String name) {
        try {
            categoryDAO.addCategory(name, userID);
        } catch (DataAccessException e) {
            System.err.println("Error inserting category: " + e.getMessage());
        }
        appController.setLatestTaskName(name);
        categoryName.clear();
        addCategoryStage.close();
        appController.loadCategories();
    }
}