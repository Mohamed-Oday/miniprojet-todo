package org.openjfx.miniprojet;

import com.jfoenix.controls.JFXListView;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Controller{

    @FXML
    private DatePicker TaskDueDateField;

    @FXML
    private ComboBox<Status> TaskStatusField;

    @FXML
    private TextField taskDescriptionField;

    @FXML
    private TextField taskNameField;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private JFXListView<TaskImpl> taskListView;

    @FXML
    private Label imageLabel;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label todayLabel;  // Ensure this is @FXML

    private final ObservableList<TaskImpl> tasks = FXCollections.observableArrayList();

    private String userID;

    @FXML
    private AnchorPane editForm;

    private TaskImpl selectedTask;

    public void setUserName(String userName) {
        this.userID = userName;
        userNameLabel.setText(userName);
        imageLabel.setText(userName.substring(0, 2).toUpperCase());
        loadTasks();
    }

    @FXML
    public void initialize() {
        ObservableList<Status> statusList = FXCollections.observableArrayList(Status.values());
        TaskStatusField.setItems(statusList);
        taskListView.setItems(tasks);
        taskListView.setOnMouseClicked(event -> {
            TaskImpl selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null){
                this.selectedTask = selectedTask;
                handleEditTask(selectedTask);
            }
        });
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

    public void handleEditTask(TaskImpl task){
        TaskStatusField.setValue(task.getStatus());
        TranslateTransition slider = new TranslateTransition();
        slider.setNode(editForm);
        slider.setToX(0);
        slider.setDuration(Duration.seconds(0));

        slider.setOnFinished((ActionEvent e) -> {
            editForm.setVisible(true);
            mainPane.setPrefWidth(850);
            taskNameField.setText(task.getName());
            taskDescriptionField.setText(task.getDescription());
            TaskDueDateField.setValue(task.getDueDate());
            TaskStatusField.setValue(task.getStatus());
            TaskStatusField.setPromptText(task.getStatus().toString());
        });
        slider.play();
    }

    @FXML
    public void handleSaveButton(){
        String taskName = taskNameField.getText();
        String taskDescription = taskDescriptionField.getText();
        LocalDate taskDueDate = TaskDueDateField.getValue();
        Status taskStatus = TaskStatusField.getValue();

        selectedTask.editTask(taskName, taskDescription, taskDueDate, taskStatus);
        updateTask(selectedTask);

        editForm.setVisible(false);
        mainPane.setPrefWidth(1200);
        taskListView.refresh();
        System.out.println("Task Updated: " + selectedTask.getStatus());
    }

    @FXML
    public void handleCancelButton(){
        editForm.setVisible(false);
        mainPane.setPrefWidth(1200);
        taskListView.refresh();
    }

    @FXML
    public void handleDeleteButton(){
        deleteTask(selectedTask);
        tasks.remove(selectedTask);
        editForm.setVisible(false);
        mainPane.setPrefWidth(1200);
        taskListView.refresh();
    }

    @FXML
    public void handleSignOutButton(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EntryPage.fxml"));
        Parent root = loader.load();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement statement = connection.prepareStatement("DELETE FROM saveduser")){
            statement.executeUpdate("DELETE FROM saveduser");
        } catch (SQLException e){
            e.printStackTrace();
        }

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("ToDo App");
        stage.show();

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    public void deleteTask(TaskImpl task){
        String deleteQuery = "DELETE FROM tasks WHERE task_id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

            preparedStatement.setInt(1, task.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTask(TaskImpl task) {
        String updateQuery = "UPDATE tasks SET task_name = ?, task_description = ?, task_dueDate = ?, task_status = ? WHERE task_id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, task.getName());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setDate(3, Date.valueOf(task.getDueDate()));
            preparedStatement.setString(4, task.getStatus().toString());
            preparedStatement.setInt(5, task.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTasks(){
        String selectQuery = "SELECT task_id ,task_name, task_description, task_dueDate, task_status FROM tasks WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)){

            preparedStatement.setString(1, userID);

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                tasks.clear();
                while (resultSet.next()){
                    int taskID = resultSet.getInt("task_id");
                    String taskName = resultSet.getString("task_name");
                    String description = resultSet.getString("task_description");
                    LocalDate dueDate = resultSet.getDate("task_dueDate").toLocalDate();
                    Status status = Status.valueOf(resultSet.getString("task_status"));
                    TaskImpl task = new TaskImpl(taskName, description, dueDate, status);
                    task.setId(taskID);
                    tasks.add(task);
                }
            } catch (SQLException e){
                e.printStackTrace();
            }

        } catch (SQLException e){
            e.printStackTrace();
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
        addTaskController.setUserID(userID);

        // Display the addTask page
        addTaskStage.showAndWait();

        loadTasks();
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
