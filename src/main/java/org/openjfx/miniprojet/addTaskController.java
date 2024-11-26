package org.openjfx.miniprojet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class addTaskController {
    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker dueDateField;

    @FXML
    private TextField nameField;

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

    @FXML
    public void handleAddButton(ActionEvent event) {
        String name = getTaskName();
        String description = getTaskDescription();
        LocalDate dueDate = getTaskDueDate();

        if (name != null && !name.isEmpty() && dueDate != null) {
            TaskImpl task = new TaskImpl(name, description, dueDate, Status.InProgress  ); // Assuming PENDING is a default status
            mainController.addTask(task);
            addTaskStage.close();
        } else {
            // Handle invalid input
            System.out.println("Invalid input: Name or Due Date is missing.");
        }
    }

    @FXML
    public void handleCancelButton(ActionEvent event) {
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