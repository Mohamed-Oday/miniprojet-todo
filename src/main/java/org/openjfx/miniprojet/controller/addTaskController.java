package org.openjfx.miniprojet.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.openjfx.miniprojet.dao.CategoryDAO;
import org.openjfx.miniprojet.dao.TaskDAO;
import org.openjfx.miniprojet.model.Status;
import java.time.LocalDate;

public class addTaskController {
    @FXML
    private JFXCheckBox importantCheck;

    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker dueDateField;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private ToggleGroup priorityGroup;

    @FXML
    private JFXRadioButton high;

    @FXML
    private JFXRadioButton medium;

    @FXML
    private JFXRadioButton low;

    private final ObservableList<String> categories = FXCollections.observableArrayList();

    private final TaskDAO taskDAO = new TaskDAO();

    private final CategoryDAO categoryDAO = new CategoryDAO();


    public void initialize() {
        priorityGroup = new ToggleGroup();
        high.setToggleGroup(priorityGroup);
        medium.setToggleGroup(priorityGroup);
        low.setToggleGroup(priorityGroup);
        low.setSelected(true);
        categoryComboBox.setValue("General");
    }

    private String userID;
    private Stage addTaskStage;
    private Controller mainController;

    public void setAddTaskStage(Stage addTaskStage) {
        this.addTaskStage = addTaskStage;
    }

    public void setMainController(Controller mainController) {
        this.mainController = mainController;
    }

    public void setUserID(String userID) {
        this.userID = userID;
        loadCategories();
    }

    private void loadCategories() {
        categories.clear();
        categories.addAll(categoryDAO.loadCategories(userID));
        categoryComboBox.setItems(categories);
    }

    @FXML
    public void handleAddButton() {
        String name = getTaskName();
        String description = getTaskDescription();
        LocalDate dueDate = getTaskDueDate();
        String selectedCategory = categoryComboBox.getValue();
        String priority = ((JFXRadioButton) priorityGroup.getSelectedToggle()).getText();

        if (name != null && !name.isEmpty() && dueDate != null) {
            insertTask(name, description, dueDate, selectedCategory, priority);
            mainController.setLatestTaskName(name);
            addTaskStage.close();
        } else {
            // Handle invalid input
            System.out.println("Invalid input: Name or Due Date is missing.");
        }
    }

    private void insertTask(String name, String description, LocalDate dueDate, String category, String priority) {
        taskDAO.createTasks(name, description, dueDate, Status.Started, category, priority, userID, importantCheck.isSelected());
    }

    @FXML
    public void handleCancelButton() {
        addTaskStage.close();
    }

    @FXML
    public String getTaskName() {
        return nameField.getText();
    }

    @FXML
    public String getTaskDescription() {
        return descriptionField.getText();
    }

    @FXML
    public LocalDate getTaskDueDate() {
        return dueDateField.getValue();
    }
}