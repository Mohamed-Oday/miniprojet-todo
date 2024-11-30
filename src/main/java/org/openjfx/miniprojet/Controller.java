package org.openjfx.miniprojet;

import com.jfoenix.controls.JFXListView;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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
    private Label categoryTitle;

    @FXML
    private JFXListView<TaskImpl> categoryTasks;

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
    private JFXListView<String> categoryListView;

    @FXML
    private JFXListView<String> categoryListView1;

    @FXML
    private JFXListView<String> categoryListView2;

    @FXML
    private JFXListView<String> categoryListView21;

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
    private Label imageLabel21;

    @FXML
    private Label userNameLabel2;

    @FXML
    private Label userNameLabel21;

    @FXML
    private Label todayLabel;

    private final ObservableList<TaskImpl> tasks = FXCollections.observableArrayList();
    private final ObservableList<String> categories = FXCollections.observableArrayList();

    private String userID;

    @FXML
    private AnchorPane editForm;

    @FXML
    private AnchorPane categoryTasksPane;

    @FXML
    private VBox mainVbox;

    @FXML
    private VBox mainVbox1;

    @FXML
    private VBox mainVbox2;

    @FXML
    private VBox mainVbox3;

    private TaskImpl selectedTask;

    public void setLatestTaskName(String latestTaskName) {
        this.latestTaskName = latestTaskName;
    }

    public void setUserName(String userName) {
        this.userID = userName;
        setupUser(userNameLabel, userNameLabel1, userNameLabel2, userNameLabel21);
        setupUserImage(imageLabel, imageLabel1, imageLabel2, imageLabel21);

        loadTasks();
        loadCategories();
    }

    private void setupUser(Label... usernameLabels){
        for (Label usernameLabel : usernameLabels) {
            usernameLabel.setText(userID);
        }
    }

    private void setupUserImage(Label... userImage){
        for (Label imageLabel : userImage) {
            imageLabel.setText(userID.substring(0, 2).toUpperCase());
        }
    }

    @FXML
    public void initialize() {
        mainPane = myDayPane;
        ObservableList<Status> statusList = FXCollections.observableArrayList(Status.values());
        TaskStatusField.setItems(statusList);

        setupCategoryListViews(categoryListView, categoryListView1, categoryListView2, categoryListView21);
        setupTaskListViews(taskListView, taskListView1, taskListView2, categoryTasks);
        setupCategoryContextMenu(categoryListView, categoryListView1, categoryListView2, categoryListView21);

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

    @SafeVarargs
    private void setupCategoryContextMenu(JFXListView<String>... listViews){
        for (JFXListView<String> listView : listViews) {
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteCategoryItem = new MenuItem("Delete Category");
            deleteCategoryItem.setOnAction(event ->  deleteSelectedCategory());

            MenuItem deleteCategoryWithTasksItem = new MenuItem("Delete Category and Tasks");
            deleteCategoryWithTasksItem.setOnAction(event -> deleteCategoryWithTasks());

            contextMenu.getItems().addAll(deleteCategoryItem, deleteCategoryWithTasksItem);

            listView.setContextMenu(contextMenu);
        }
    }

    private void deleteSelectedCategory(){
        String selectedCategory;
        if (myDayPane.isVisible()){
            selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
            System.out.println("Selected category: " + selectedCategory);
        }else if (importantPane.isVisible()){
            selectedCategory = categoryListView1.getSelectionModel().getSelectedItem();
            System.out.println("Selected category: " + selectedCategory);
        }else if (displayTasksPane.isVisible()){
            selectedCategory = categoryListView2.getSelectionModel().getSelectedItem();
            System.out.println("Selected category: " + selectedCategory);
        }else {
            selectedCategory = categoryListView21.getSelectionModel().getSelectedItem();
            System.out.println("Selected category: " + selectedCategory);
        }
        if (selectedCategory != null){
            deleteCategory(selectedCategory, false);
            loadCategories();
        }
    }

    private void deleteCategoryWithTasks(){
        String selectedCategory;
        if (myDayPane.isVisible()){
            selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        }else if (importantPane.isVisible()){
            selectedCategory = categoryListView1.getSelectionModel().getSelectedItem();
        }else if (displayTasksPane.isVisible()){
            selectedCategory = categoryListView2.getSelectionModel().getSelectedItem();
        }else {
            selectedCategory = categoryListView21.getSelectionModel().getSelectedItem();
        }
        if (selectedCategory != null){
            deleteCategory(selectedCategory, true);
            loadCategories();
        }
    }

    private void deleteCategory(String categoryName, boolean deleteTasks) {
        String deleteCategoryQuery = "DELETE FROM categories WHERE category_name = ? AND user_id = ?";
        String deleteTasksQuery = "DELETE FROM tasks WHERE category_id = (SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement categoryStmt = connection.prepareStatement(deleteCategoryQuery);
             PreparedStatement tasksStmt = connection.prepareStatement(deleteTasksQuery)) {

            if (deleteTasks) {
                tasksStmt.setString(1, categoryName);
                tasksStmt.setString(2, userID);
                int rowsAffected = tasksStmt.executeUpdate();
                System.out.println("Deleted " + rowsAffected + " tasks associated with category: " + categoryName);
            }

            categoryStmt.setString(1, categoryName);
            categoryStmt.setString(2, userID);
            int rowsAffected = categoryStmt.executeUpdate();
            System.out.println("Deleted category: " + categoryName + ", rows affected: " + rowsAffected);
            if (categoryTasksPane.isVisible()){
                categoryTasksPane.setVisible(false);
                myDayPane.setVisible(true);
            }
            loadTasks();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Refresh the category list
            loadCategories();
            // Clear selection
            categoryListView.getSelectionModel().clearSelection();
        }
    }

    @SafeVarargs
    private void setupTaskListViews(JFXListView<TaskImpl>... listViews) {
        for (JFXListView<TaskImpl> listView : listViews) {
            listView.setItems(tasks);
            listView.setOnMouseClicked(event -> {
                TaskImpl selectedTask = listView.getSelectionModel().getSelectedItem();
                if (selectedTask != null) {
                    System.out.println(selectedTask.getName());
                    handleEditTask(selectedTask);
                }
            });
        }
    }

    @SafeVarargs
    private void setupCategoryListViews(JFXListView<String>... listViews) {
        for (JFXListView<String> listView : listViews) {
            listView.setItems(categories);
            listView.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY){
                    String selectedCategory = listView.getSelectionModel().getSelectedItem();
                    if (selectedCategory != null){
                        loadTasksForCategory(selectedCategory);
                    }
                }
            });
        }
    }

    public void handleEditTask(TaskImpl task) {
        TaskStatusField.setValue(task.getStatus());
        TranslateTransition slider = new TranslateTransition();
        slider.setNode(editForm);
        slider.setToX(0);
        slider.setDuration(Duration.seconds(0));
        mainVbox.setPadding(new Insets(0, 320, 0, 0));
        mainVbox1.setPadding(new Insets(0, 320, 0, 0));
        mainVbox2.setPadding(new Insets(0, 320, 0, 0));
        mainVbox3.setPadding(new Insets(0, 320, 0, 0));

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
        this.selectedTask = task;
    }

    private void resizeMainPaneForEdit() {
        if (mainPane != null) {
            mainPane.setPrefWidth(850);
            mainPane.applyCss();
            mainPane.layout();
        }
    }

    @FXML
    public void handleSaveButton() {
        if (selectedTask != null){
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
            categoryTasks.refresh();
            mainVbox.setPadding(new Insets(0, 0, 0, 0));
            mainVbox1.setPadding(new Insets(0, 0, 0, 0));
            mainVbox2.setPadding(new Insets(0, 0, 0, 0));
            mainVbox3.setPadding(new Insets(0, 0, 0, 0));
            showNotification("Task updated successfully.", "Task", "has been updated", taskName);
        }
    }

    @FXML
    public void handleCancelButton() {
        editForm.setVisible(false);
        resetMainPaneSize();
        taskListView.refresh();
        mainVbox.setPadding(new Insets(0, 0, 0, 0));
        mainVbox1.setPadding(new Insets(0, 0, 0, 0));
        mainVbox2.setPadding(new Insets(0, 0, 0, 0));
        mainVbox3.setPadding(new Insets(0, 0, 0, 0));

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
        categoryTasks.refresh();
        mainVbox.setPadding(new Insets(0, 0, 0, 0));
        mainVbox1.setPadding(new Insets(0, 0, 0, 0));
        mainVbox2.setPadding(new Insets(0, 0, 0, 0));
        mainVbox3.setPadding(new Insets(0, 0, 0, 0));
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
        String loadTasksQuery = getLoadTasksQuery();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(loadTasksQuery)) {

            int parameterIndex = 1;
            preparedStatement.setString(parameterIndex++ , userID);

            if (categoryTasksPane.isVisible()){
                preparedStatement.setString(parameterIndex++, categoryTitle.getText());
                preparedStatement.setString(parameterIndex, userID);
            }

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

    private String getLoadTasksQuery() {
        String loadTasksQuery = "SELECT task_id, task_name, task_description, task_dueDate, task_status FROM tasks WHERE user_id = ?";

        // Filter for current day tasks if `myDayPane` is visible
        if (myDayPane.isVisible()) {
            loadTasksQuery += " AND task_startDate = CURDATE()";
        } else if (importantPane.isVisible()) {
            // Filter for important tasks if `importantPane` is visible
            loadTasksQuery += " AND is_important = 1";
        }else if (categoryTasksPane.isVisible()) {
            loadTasksQuery += " AND category_id = (SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?)";
        }
        return loadTasksQuery;
    }

    private void loadTasksForCategory(String categoryName) {
        categoryTasksPane.setVisible(true);
        myDayPane.setVisible(false);
        displayTasksPane.setVisible(false);
        importantPane.setVisible(false);
        categoryTitle.setText(categoryName);
        mainPane = categoryTasksPane;
        loadTasksByCategory(categoryName);
        categoryTasks.setItems(tasks);
        categoryTitle.setText(categoryName);
    }

    private void loadTasksByCategory(String categoryName) {
        String loadTasksQuery = "SELECT tasks.task_id, tasks.task_name, tasks.task_description, tasks.task_dueDate, tasks.task_status "
                + "FROM tasks JOIN categories ON tasks.category_id = categories.category_id "
                + "WHERE tasks.user_id = ? AND categories.category_name = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(loadTasksQuery)) {

            preparedStatement.setString(1, userID);
            preparedStatement.setString(2, categoryName);

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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddButton() throws IOException {
        // Loading the addTask fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addTask.fxml"));
        Parent root = loader.load();

        // Getting the addTask Controller
        addTaskController addTaskController = loader.getController();

        // Creating new stage for the addTask page
        Stage addTaskStage = new Stage();
        addTaskStage.setTitle("Add Task");
        addTaskStage.setScene(new Scene(root));
        addTaskStage.initStyle(StageStyle.UTILITY);
        addTaskStage.initModality(Modality.APPLICATION_MODAL);

        // Setting the main stage and addTask stage
        addTaskController.setAddTaskStage(addTaskStage);
        addTaskController.setMainController(this);
        addTaskController.setUserID(userID);

        // Display the addTask page
        addTaskStage.showAndWait();
        showNotification("Task added successfully.", "Task", "has been added", latestTaskName);
        loadTasks();
    }

    @FXML
    public void handleAddCategory() throws IOException {
        // Loading the addCategory fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addCategory.fxml"));
        Parent root = loader.load();

        // Getting the addCategory Controller
        AddCategoryController addCategoryController = loader.getController();

        // Creating new stage for the addCategory page
        Stage addCategoryStage = new Stage();
        addCategoryStage.setTitle("Add Category");
        addCategoryStage.setScene(new Scene(root));
        addCategoryStage.initStyle(StageStyle.UTILITY);
        addCategoryStage.initModality(Modality.APPLICATION_MODAL);

        // Setting the main stage and addCategory stage
        addCategoryController.setAddCategoryStage(addCategoryStage);
        addCategoryController.setMainController(this);
        addCategoryController.setUserID(userID);

        // Display the addCategory page
        addCategoryStage.showAndWait();
    }

    public void loadCategories() {
        categories.clear();
        String loadCategoriesQuery = "SELECT category_name FROM categories WHERE user_id = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(loadCategoriesQuery)) {

            preparedStatement.setString(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String categoryName = resultSet.getString("category_name");
                categories.add(categoryName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        categoryListView.setItems(categories);
        categoryListView1.setItems(categories);
        categoryListView2.setItems(categories);
        categoryListView21.setItems(categories);
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

    @FXML
    public void handleMyDayButton() {
        resetMainPaneSize();
        if (editForm.isVisible()) {
            handleCancelButton();
        }
        myDayPane.setVisible(true);
        displayTasksPane.setVisible(false);
        importantPane.setVisible(false);
        categoryTasksPane.setVisible(false);
        mainPane = myDayPane; // Ensure mainPane points to myDayPane
        loadTasks();
        loadCategories();
        taskListView.refresh();
        categoryListView.refresh();
    }

    @FXML
    public void handleImportantButton() {
        resetMainPaneSize();
        if (editForm.isVisible()) {
            handleCancelButton();
        }
        myDayPane.setVisible(false);
        displayTasksPane.setVisible(false);
        importantPane.setVisible(true);
        categoryTasksPane.setVisible(false);
        mainPane = importantPane; // Ensure mainPane points to importantPane
        loadTasks();
        loadCategories();
        taskListView.refresh();
        categoryListView.refresh();
    }

    @FXML
    public void handleDisplayTasksButton() {
        resetMainPaneSize();
        if (editForm.isVisible()) {
            handleCancelButton();
        }
        myDayPane.setVisible(false);
        displayTasksPane.setVisible(true);
        importantPane.setVisible(false);
        categoryTasksPane.setVisible(false);
        mainPane = displayTasksPane; // Ensure mainPane points to displayTasksPane
        loadTasks();
        loadCategories();
        taskListView.refresh();
        categoryListView.refresh();
    }
}