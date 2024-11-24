package org.openjfx.miniprojet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class Controller {
    @FXML
    private TextField taskNameField;

    @FXML
    private TextField taskDescriptionField;

    @FXML
    private DatePicker taskDueDate;

    @FXML
    private ComboBox<String> taskStatusComboBox;

    @FXML
    private TableView<TaskImpl> taskTableView;

    @FXML
    private TableColumn<TaskImpl, String> nameColumn;

    @FXML
    private TableColumn<TaskImpl, String> descriptionColumn;

    @FXML
    private TableColumn<TaskImpl, LocalDate> dueDateColumn;

    @FXML
    private TableColumn<TaskImpl, String> statusColumn;

    private ObservableList<TaskImpl> taskList;

    @FXML
    public void initialize() {
        // Initialize task list
        taskList = FXCollections.observableArrayList();
        taskTableView.setItems(taskList);

        // Initialize columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Initialize ComboBox
        taskStatusComboBox.setItems(FXCollections.observableArrayList("In Progress", "Completed", "Abandoned"));
    }

    @FXML
    public void handleAddTask() {
        String name = taskNameField.getText();
        String description = taskDescriptionField.getText();
        LocalDate dueDate = taskDueDate.getValue();
        String status = taskStatusComboBox.getValue();

        if (name != null && !name.isEmpty() && dueDate != null && status != null) {
            // Remove spaces from status
            status = status.replaceAll("\\s", "");

            // Add new task to the list
            TaskImpl task = new TaskImpl(name, description, dueDate, Status.valueOf(status));
            taskList.add(task);
            clearFields();
        } else {
            // Show error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Please fill all the fields");
            alert.showAndWait();
        }
    }

    @FXML
    public void handleRemoveTask(){
        TaskImpl task = taskTableView.getSelectionModel().getSelectedItem();
        if (task != null){
            taskList.remove(task);
        }else{
            // Show error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Please select a task to remove");
            alert.showAndWait();
        }
    }

    private void clearFields() {
        taskNameField.clear();
        taskDescriptionField.clear();
        taskDueDate.setValue(null);
        taskStatusComboBox.setValue(null); // Set the value to null
        System.out.println(taskStatusComboBox.getPromptText());
    }
}