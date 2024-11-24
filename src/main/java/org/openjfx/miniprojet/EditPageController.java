package org.openjfx.miniprojet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for the Edit Page.
 * Handles the editing of tasks.
 * @author Sellami Mohamed Odai
 */
public class EditPageController {
    @FXML
    private TextField taskNameField;

    @FXML
    private TextField taskDescriptionField;

    @FXML
    private DatePicker taskDueDate;

    @FXML
    private ComboBox<String> taskStatusComboBox;

    private TaskImpl task;
    private ObservableList<TaskImpl> taskList;

    /**
     * Initializes the controller class.
     * Sets up the ComboBox with status options.
     */
    @FXML
    public void initialize() {
        taskStatusComboBox.setItems(FXCollections.observableArrayList("In Progress", "Completed", "Abandoned"));
    }

    /**
     * Sets the task to be edited and the task list.
     * Populates the fields with the task's current data.
     *
     * @param task the task to be edited
     * @param taskList the list of tasks
     */
    public void setTask(TaskImpl task, ObservableList<TaskImpl> taskList) {
        this.task = task;
        this.taskList = taskList;
        taskNameField.setText(task.getName());
        taskDescriptionField.setText(task.getDescription());
        taskDueDate.setValue(task.getDueDate());
        taskStatusComboBox.setValue(task.getStatus().toString());
    }

    /**
     * Handles the save button action.
     * Updates the task with the new data and returns to the main page.
     *
     * @param event the action event
     * @throws IOException if an I/O error occurs
     */
    @FXML
    public void handleSaveButton(ActionEvent event) throws IOException {
        task.editTask(
                taskNameField.getText(),
                taskDescriptionField.getText(),
                taskDueDate.getValue(),
                Status.valueOf(taskStatusComboBox.getValue().replaceAll("\\s", ""))
        );
        taskList.set(taskList.indexOf(task), task);
        goBack(event);
    }

    /**
     * Handles the cancel button action.
     * Returns to the main page without saving changes.
     *
     * @param event the action event
     * @throws IOException if an I/O error occurs
     */
    @FXML
    public void handleCancelButton(ActionEvent event) throws IOException {
        goBack(event);
    }

    /**
     * Navigates back to the main page.
     * Passes the updated task list to the main controller.
     *
     * @param event the action event
     * @throws IOException if an I/O error occurs
     */
    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Controller controller = loader.getController();
        controller.updatedTaskList(taskList);

        stage.setScene(new Scene(root));
        stage.show();
    }
}