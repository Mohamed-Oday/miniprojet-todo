package org.openjfx.miniprojet;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class addTaskController {
    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker dueDateField;

    @FXML
    private TextField nameField;

    private String userID;

    private Stage mainStage;
    private Stage addTaskStage;
    private Controller mainController;

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setAddTaskStage(Stage addTaskStage) {
        this.addTaskStage = addTaskStage;
    }

    public void setMainController(Controller mainController) {
        this.mainController = mainController;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @FXML
    public void handleAddButton() {
        String name = getTaskName();
        String description = getTaskDescription();
        LocalDate dueDate = getTaskDueDate();

        if (name != null && !name.isEmpty() && dueDate != null) {
            insertTask(name, description, dueDate);
            mainController.setLatestTaskName(name);
            addTaskStage.close();
        } else {
            // Handle invalid input
            System.out.println("Invalid input: Name or Due Date is missing.");
        }
    }

    private void insertTask(String name, String description, LocalDate dueDate) {
        String insertQuery = "INSERT INTO tasks (user_id, task_name, task_description, task_dueDate, task_status) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)){
            preparedStatement.setString(1, userID);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, description);
            preparedStatement.setDate(4, java.sql.Date.valueOf(dueDate));
            preparedStatement.setString(5, Status.Started.toString());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0){
                System.out.println("Task added successfully.");
            }else{
                System.out.println("Task not added.");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
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