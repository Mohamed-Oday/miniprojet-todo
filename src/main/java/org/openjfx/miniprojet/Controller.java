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
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Controller{

    @FXML
    private JFXListView<TaskImpl> taskListView;

    @FXML
    private Label imageLabel;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label todayLabel;  // Ensure this is @FXML

    private ObservableList<TaskImpl> tasks = FXCollections.observableArrayList();

    private String userID;

    public void setUserName(String userName) {
        this.userID = userName;
        userNameLabel.setText(userName);
        imageLabel.setText(userName.substring(0, 2).toUpperCase());
    }

    @FXML
    public void initialize() {
        taskListView.setItems(tasks);

        // Get current date and format it
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd");
        String formattedDate = currentDate.format(formatter);

        // Set the formatted date on the todayLabel
        if (todayLabel != null) {
            todayLabel.setText(formattedDate);
        } else {
            System.out.println("todayLabel is null!");
        }
    }

    @FXML
    public void handleAddButton(ActionEvent event) throws IOException {
        // Loading the addTask fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addTask.fxml"));
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

    public void addTask(TaskImpl task) {
        tasks.add(task);
        taskListView.setItems(tasks);
    }

    @FXML
    public void handleImportantButton(ActionEvent event) throws IOException {
        setMainStage("important.fxml", event);
    }

    @FXML
    public void handleDisplayTasksButton(ActionEvent event) throws IOException {
        setMainStage("displayTasks.fxml", event);
    }

    @FXML
    public void handleMyDayButton(ActionEvent event) throws IOException {
        setMainStage("Main.fxml", event);
    }

    public void setMainStage(String path, ActionEvent event) throws IOException {
        // Loading the important fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        Parent root = loader.load();

        // Creating new stage for the important page
        Stage nextStage = new Stage();
        nextStage.setScene(new Scene(root));

        Controller controller = loader.getController();
        controller.setUserName(userID);

        // Show the important page
        nextStage.show();

        // Closing the current stage
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
}
