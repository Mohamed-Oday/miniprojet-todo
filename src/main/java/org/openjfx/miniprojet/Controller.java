package org.openjfx.miniprojet;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

public class Controller {

    @FXML
    private JFXListView<TaskImpl> taskListView;

    private ObservableList<TaskImpl> tasks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        taskListView.setItems(tasks);
    }

    @FXML
    public void handleAddButton(ActionEvent event) throws IOException{
        // Loading the addTask fxml
        FXMLLoader loader= new FXMLLoader(getClass().getResource("addTask.fxml"));
        Parent root = loader.load();

        // Getting the addTask Controller
        addTaskController addTaskController = loader.getController();

        // Getting main stage
        Stage mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Creating new stage for the addTask page
        Stage addTaskStage = new Stage();
        addTaskStage.setTitle("Add Task");
        addTaskStage.setScene(new Scene(root));
        addTaskStage.initStyle(StageStyle.UTILITY);
        addTaskStage.initModality(Modality.APPLICATION_MODAL);

        // Setting the main stage and addTask stage
        addTaskController.setMainStage(mainStage);
        addTaskController.setAddTaskStage(addTaskStage);
        addTaskController.setMainController(this);

        // Display the addTask page
        addTaskStage.showAndWait();
    }

    public void addTask(TaskImpl task){
        tasks.add(task);
        taskListView.getItems().add(task);
    }

    @FXML
    public void handleImportantButton(ActionEvent event) throws IOException{
        // Loading the important fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("important.fxml"));
        Parent root = loader.load();

        // Creating new stage for the important page
        Stage importantStage = new Stage();
        importantStage.setScene(new Scene(root));

        // Show the important page
        importantStage.show();

        // Closing the current stage
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    @FXML
    public void handleDisplayTasksButton(ActionEvent event) throws IOException{
        // Loading the important fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("displayTasks.fxml"));
        Parent root = loader.load();

        // Creating new stage for the important page
        Stage importantStage = new Stage();
        importantStage.setScene(new Scene(root));

        // Show the important page
        importantStage.show();

        // Closing the current stage
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    @FXML
    public void handleMyDayButton(ActionEvent event) throws IOException{
        // Loading the important fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Parent root = loader.load();

        // Creating new stage for the important page
        Stage importantStage = new Stage();
        importantStage.setScene(new Scene(root));

        // Show the important page
        importantStage.show();

        // Closing the current stage
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
}