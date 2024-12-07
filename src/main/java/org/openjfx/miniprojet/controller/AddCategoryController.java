package org.openjfx.miniprojet.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.openjfx.miniprojet.dao.CategoryDAO;
public class AddCategoryController {

    private Stage addCategoryStage;
    private Controller mainController;
    private String userID;

    @FXML
    private TextField categoryName;

    private final CategoryDAO categoryDAO = new CategoryDAO();


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
        categoryDAO.addCategory(name, userID);
        categoryName.clear();
        addCategoryStage.close();
        mainController.loadCategories();
    }
}
