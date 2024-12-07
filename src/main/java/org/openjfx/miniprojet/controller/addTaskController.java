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
import org.openjfx.miniprojet.dao.TaskDAO;
import org.openjfx.miniprojet.model.Status;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    private TaskDAO taskDAO = new TaskDAO();


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
        String loadCategoriesQuery = "SELECT category_name FROM categories WHERE user_id = ?";
        categories.clear();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(loadCategoriesQuery)) {
            System.out.println("UserID: " + userID);
            preparedStatement.setString(1, userID);
            preparedStatement.execute();
            var resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                categories.add(resultSet.getString("category_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Categories size: " + categories.size());
        if (categoryComboBox != null) {
            categoryComboBox.setItems(categories);
        } else {
            System.out.println("categoryComboBox is null");
        }
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

    private static String getString() {
        return "INSERT INTO tasks (user_id, task_name, task_description, task_dueDate, task_status, is_important, task_startDate, category_id, task_priority) VALUES (?, ?, ?, ?, ?, ?, ?, (SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?), ?)";
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