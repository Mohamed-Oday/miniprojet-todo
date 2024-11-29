package org.openjfx.miniprojet;

import com.jfoenix.controls.JFXListView;
import javafx.animation.PauseTransition;
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
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Controller {

    @FXML
    private BorderPane borderPane;

    @FXML
    private AnchorPane displayTasksPane;

    @FXML
    private AnchorPane importantPane;

    @FXML
    private Label notificationMessage;

    @FXML
    private Label notificationTaskName;

    private String latestTaskName;

    @FXML
    private AnchorPane notificationForm;

    @FXML
    private DatePicker TaskDueDateField;

    @FXML
    private ComboBox<Status> TaskStatusField;

    @FXML
    private TextField taskDescriptionField;

    @FXML
    private TextField taskNameField;

    @FXML
    private AnchorPane myDayPane;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private JFXListView<TaskImpl> taskListView;

    @FXML
    private JFXListView<TaskImpl> taskListView1;

    @FXML
    private JFXListView<TaskImpl> taskListView2;

    @FXML
    private Label imageLabel;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label imageLabel1;

    @FXML
    private Label userNameLabel1;

    @FXML
    private Label imageLabel2;

    @FXML
    private Label userNameLabel2;

    @FXML
    private Label todayLabel;  // Ensure this is @FXML

    private final ObservableList<TaskImpl> tasks = FXCollections.observableArrayList();

    private String userID;

    @FXML
    private AnchorPane editForm;

    private TaskImpl selectedTask;

    public void setLatestTaskName(String latestTaskName) {
        this.latestTaskName = latestTaskName;
    }

    public void setUserName(String userName) {
        this.userID = userName;
        userNameLabel.setText(userName);
        imageLabel.setText(userName.substring(0, 2).toUpperCase());

        userNameLabel1.setText(userName);
        imageLabel1.setText(userName.substring(0, 2).toUpperCase());

        userNameLabel2.setText(userName);
        imageLabel2.setText(userName.substring(0, 2).toUpperCase());
        loadTasks();
    }

    @FXML
    public void initialize() {
        mainPane = myDayPane;
        ObservableList<Status> statusList = FXCollections.observableArrayList(Status.values());
        TaskStatusField.setItems(statusList);

        setupTaskListView(taskListView);
        setupTaskListView(taskListView1);
        setupTaskListView(taskListView2);

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

    private void setupTaskListView(JFXListView<TaskImpl> listView) {
        listView.setItems(tasks);
        listView.setOnMouseClicked(event -> {
            TaskImpl selectedTask = listView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                this.selectedTask = selectedTask;
                handleEditTask(selectedTask);
            }
        });
    }

    public void handleEditTask(TaskImpl task) {
        TaskStatusField.setValue(task.getStatus());
        TranslateTransition slider = new TranslateTransition();
        slider.setNode(editForm);
        slider.setToX(0);
        slider.setDuration(Duration.seconds(0));

        slider.setOnFinished((ActionEvent e) -> {
            resizeMainPaneForEdit();
            editForm.setVisible(true);
            taskNameField.setText(task.getName());
            taskDescriptionField.setText(task.getDescription());
            TaskDueDateField.setValue(task.getDueDate());
            TaskStatusField.setValue(task.getStatus());
            TaskStatusField.setPromptText(task.getStatus().toString());
        });
        slider.play();
    }

    private void resizeMainPaneForEdit() {
        if (mainPane != null) {
            mainPane.setPrefWidth(850);
            mainPane.applyCss();
            mainPane.layout();
            System.out.println("Size after reset: " + mainPane.getPrefWidth() + ", Actual Width: " + mainPane.getWidth());
            System.out.println("id: " + mainPane.getId());
        }
    }

    @FXML
    public void handleSaveButton() {
        String taskName = taskNameField.getText();
        String taskDescription = taskDescriptionField.getText();
        LocalDate taskDueDate = TaskDueDateField.getValue();
        Status taskStatus = TaskStatusField.getValue();

        selectedTask.editTask(taskName, taskDescription, taskDueDate, taskStatus);
        updateTask(selectedTask);

        editForm.setVisible(false);
        resetMainPaneSize();
        taskListView.refresh();
        taskListView1.refresh();
        taskListView2.refresh();
        showNotification("Task updated successfully.", "Task", "has been updated", taskName);
        System.out.println("Task Updated: " + selectedTask.getStatus());
    }

    @FXML
    public void handleCancelButton() {
        editForm.setVisible(false);
        resetMainPaneSize();
        taskListView.refresh();
    }

    @FXML
    public void handleDeleteButton() {
        deleteTask(selectedTask);
        tasks.remove(selectedTask);
        editForm.setVisible(false);
        resetMainPaneSize();
        taskListView.refresh();
        taskListView1.refresh();
        taskListView2.refresh();
    }

    private void resetMainPaneSize() {
        if (mainPane != null) {
            mainPane.setPrefWidth(1200);
            mainPane.applyCss();
            mainPane.layout();
        }
    }

    @FXML
    public void handleSignOutButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EntryPage.fxml"));
        Parent root = loader.load();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement statement = connection.prepareStatement("DELETE FROM saveduser")) {
            statement.executeUpdate("DELETE FROM saveduser");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("ToDo App");
        stage.show();

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    public void deleteTask(TaskImpl task) {
        String deleteQuery = "DELETE FROM tasks WHERE task_id = ?";
        setLatestTaskName(task.getName());
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

            preparedStatement.setInt(1, task.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        showNotification("Task deleted successfully.", "Task", "has been deleted", latestTaskName);
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

    private void loadTasks() {
        String selectQuery = "SELECT task_id ,task_name, task_description, task_dueDate, task_status FROM tasks WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            preparedStatement.setString(1, userID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                tasks.clear();
                while (resultSet.next()) {
                    int taskID = resultSet.getInt("task_id");
                    String taskName = resultSet.getString("task_name");
                    String description = resultSet.getString("task_description");
                    LocalDate dueDate = resultSet.getDate("task_dueDate").toLocalDate();
                    Status status = Status.valueOf(resultSet.getString("task_status"));
                    TaskImpl task = new TaskImpl(taskName, description, dueDate, status);
                    task.setId(taskID);
                    tasks.add(task);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
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
        showNotification("Task added successfully.", "Task", "has been added", latestTaskName);
        loadTasks();
    }

    public void showNotification(String message, String part1, String part2, String taskName) {
        if (taskName != null && !taskName.isEmpty()) {
            notificationMessage.setText(message);
            notificationTaskName.setText(part1 + " " + "\"" + taskName + "\"" + " " + part2);
            // Reset position and visibility
            notificationForm.setVisible(true);
            notificationForm.setTranslateX(300);
            notificationForm.setTranslateY(0);

            // Slide in the notification
            TranslateTransition sliderIn = new TranslateTransition(Duration.seconds(0.5), notificationForm);
            sliderIn.setToX(0); // Bring it into view
            sliderIn.setOnFinished(e -> {
                // Wait for 3 seconds before sliding it out
                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                pause.setOnFinished(event -> {
                    // Slide out the notification
                    TranslateTransition sliderOut = new TranslateTransition(Duration.seconds(0.5), notificationForm);
                    sliderOut.setToX(400); // Move it back off-screen
                    sliderOut.setOnFinished(evt -> notificationForm.setVisible(false)); // Hide after sliding out
                    sliderOut.play();
                });
                pause.play();
            });
            sliderIn.play();
            this.latestTaskName = null;
        }
    }

    public void handleMyDayButton(){
        resetMainPaneSize();
        if (editForm.isVisible()){
            handleCancelButton();
        }
        myDayPane.setVisible(true);
        displayTasksPane.setVisible(false);
        importantPane.setVisible(false);
        mainPane = myDayPane; // Ensure mainPane points to myDayPane
    }

    @FXML
    public void handleImportantButton(){
        resetMainPaneSize();
        if (editForm.isVisible()){
            handleCancelButton();
        }
        myDayPane.setVisible(false);
        displayTasksPane.setVisible(false);
        importantPane.setVisible(true);
        mainPane = importantPane; // Ensure mainPane points to importantPane
    }

    @FXML
    public void handleDisplayTasksButton(){
        resetMainPaneSize();
        if (editForm.isVisible()){
            handleCancelButton();
        }
        myDayPane.setVisible(false);
        displayTasksPane.setVisible(true);
        importantPane.setVisible(false);
        mainPane = displayTasksPane; // Ensure mainPane points to displayTasksPane
    }
}