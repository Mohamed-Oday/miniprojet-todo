package org.openjfx.miniprojet.controller;

import com.jfoenix.controls.JFXRadioButton;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import com.jfoenix.controls.JFXListView;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.openjfx.miniprojet.model.Status;
import org.openjfx.miniprojet.model.TaskImpl;
import org.openjfx.miniprojet.model.TaskListImpl;
import org.openjfx.miniprojet.utiil.Database;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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
    private TextField searchField;

    @FXML
    private TextField searchField1;

    @FXML
    private TextField searchField2;

    @FXML
    private TextField searchField3;

    @FXML
    private Label todayLabel;

    private final TaskListImpl tasks = new TaskListImpl(FXCollections.observableArrayList());
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

    @FXML
    private JFXRadioButton high;

    @FXML
    private JFXRadioButton medium;

    @FXML
    private JFXRadioButton low;

    @FXML
    private ToggleGroup priorityGroup;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private MenuButton sortingMenu;

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
        String initials = userID.substring(0, 2).toUpperCase();
        for (Label imageLabel : userImage) {
            imageLabel.setText(initials);
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
        setupSearchField(searchField, searchField1, searchField2, searchField3);
        setupSortingMenu();
        priorityGroup = new ToggleGroup();
        high.setToggleGroup(priorityGroup);
        medium.setToggleGroup(priorityGroup);
        low.setToggleGroup(priorityGroup);

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

    private void setupSortingMenu(){
        MenuItem originalSort = getMenuItem(tasks, "Original Order");
        MenuItem sortByPriority = getMenuItem(tasks, "Sort by Priority");
        sortingMenu.getItems().addAll(originalSort ,sortByPriority);
    }

    private MenuItem getMenuItem(TaskListImpl tempTasks, String sortMethod) {
        MenuItem sortType = new MenuItem(sortMethod);
        sortType.setOnAction(event -> {
            sortingMenu.setText(sortMethod);
            if (tempTasks == null) {
                System.out.println("Task list is null");
                return;
            }
            ObservableList<TaskImpl> sortedTasks = null;
            if (sortMethod.equals("Sort by Priority")){
                sortedTasks = tempTasks.sortTasksByPriority();
            }

            if (sortMethod.equals("Original Order")){
                sortedTasks = tempTasks.getTasks();
            }

            taskListView.setItems(sortedTasks);
            taskListView1.setItems(sortedTasks);
            taskListView2.setItems(sortedTasks);
            categoryTasks.setItems(sortedTasks);

            taskListView.refresh();
            taskListView1.refresh();
            taskListView2.refresh();
            categoryTasks.refresh();
        });
        return sortType;
    }

    private void setupSearchField(TextField... searchFields) {
        FilteredList<TaskImpl> filteredList = new FilteredList<>(tasks.getTasks(), task -> true);
        for (TextField searchField : searchFields) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(task -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return task.getName().toLowerCase().contains(lowerCaseFilter)
                            || task.getDescription().toLowerCase().contains(lowerCaseFilter);
                });
            });
        }
        taskListView.setItems(filteredList);
        taskListView.refresh();
        taskListView1.setItems(filteredList);
        taskListView2.setItems(filteredList);
        categoryTasks.setItems(filteredList);
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
        if (selectedCategory.equals("General")){
            alert("General category cannot be deleted.");
            return;
        }
        deleteCategory(selectedCategory, false);
        loadCategories();
    }

    private void alert(String s)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
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
        if (selectedCategory.equals("General")){
            alert("General category cannot be deleted.");
            return;
        }
        deleteCategory(selectedCategory, true);
        loadCategories();
    }

    private void deleteCategory(String categoryName, boolean deleteTasks) {
        String deleteCategoryQuery = "DELETE FROM categories WHERE category_name = ? AND user_id = ?";
        String deleteTasksQuery = "DELETE FROM tasks WHERE category_id = (SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?)";

        try{
            if (deleteTasks){
                int rowAffected = Database.getInstance().executeUpdate(deleteTasksQuery, categoryName, userID);
                System.out.println(rowAffected + " tasks deleted");
            }
            int rowAffected = Database.getInstance().executeUpdate(deleteCategoryQuery, categoryName, userID);
            System.out.println(rowAffected + " category deleted");

            if (categoryTasksPane.isVisible()){
                categoryTasksPane.setVisible(false);
                myDayPane.setVisible(true);
            }
            loadTasks();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            loadCategories();
            categoryListView.getSelectionModel().clearSelection();
        }
    }

    @SafeVarargs
    private void setupTaskListViews(JFXListView<TaskImpl>... listViews) {
        for (JFXListView<TaskImpl> listView : listViews) {
            // Create the placeholder label
            Label placeholderLabel = new Label("Add your first task!");
            placeholderLabel.setId("noTaskLabel");

            Image image = new Image(Objects.requireNonNull(getClass().getResource("/org/openjfx/miniprojet/assets/images/AddNotes-pana-2x.png")).toString());
            ImageView placeholderImage = new ImageView(image);

            VBox placeholderContainer = new VBox(placeholderLabel, placeholderImage);
            placeholderContainer.setAlignment(Pos.CENTER); // Centers the items

            listView.setPlaceholder(placeholderContainer);

            listView.setItems(tasks.getTasks());

            setupTasksButtons(listView);

            listView.setOnMouseClicked(event -> {
                TaskImpl selectedTask = listView.getSelectionModel().getSelectedItem();
                if (selectedTask != null) {
                    System.out.println(selectedTask.getName());
                    handleEditTask(selectedTask);
                }
            });
        }
    }

    private void setupTasksButtons(ListView<TaskImpl> listView) {
        listView.setCellFactory(param -> {
            ListCell<TaskImpl> cell = new ListCell<>() {
                @Override
                protected void updateItem(TaskImpl task, boolean empty) {
                    super.updateItem(task, empty);
                    if (empty || task == null) {
                        setGraphic(null);
                        setText(null);
                        setVisible(false);
                        setStyle("-fx-background-color: transparent;");
                    } else {
                        setText(task.getName());
                        Button deleteButton = new Button("Delete");
                        deleteButton.setOnAction(event -> {
                            deleteTask(task);
                            tasks.deleteTask(task);
                            taskListView.refresh();
                        });
                        deleteButton.setId("deleteButton");
                        HBox container = new HBox(5, deleteButton);
                        setGraphic(container);
                        setPrefHeight(Region.USE_COMPUTED_SIZE);
                        setVisible(true);
                        setStyle("-fx-background-color: #37393a");

                        setOnMouseEntered(event -> setStyle("-fx-background-color: #4a4a4a"));
                        setOnMouseExited(event -> setStyle("-fx-background-color: #303030"));
                    }
                }
            };
            return cell;
        });
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
        showEditFormSearchField(false ,searchField, searchField1, searchField2, searchField3);
        slider.setOnFinished((ActionEvent e) -> {
            resizeMainPaneForEdit();
            editForm.setVisible(true);
            taskNameField.setText(task.getName());
            taskDescriptionField.setText(task.getDescription());
            TaskDueDateField.setValue(task.getDueDate());
            TaskStatusField.setValue(task.getStatus());
            TaskStatusField.setPromptText(task.getStatus().toString());
            priorityGroup.selectToggle(getPriorityToggle(task.getPriority()));
            categoryComboBox.setValue(task.getCategory());

        });
        slider.play();
        this.selectedTask = task;
    }

    private JFXRadioButton getPriorityToggle(String priority){
        return switch (priority) {
            case "High" -> high;
            case "Medium" -> medium;
            case "Low" -> low;
            default -> null;
        };
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
        String selectedCategory = categoryComboBox.getValue();
        if (selectedCategory == null){
            selectedCategory = "General";
        }
        if (selectedTask != null){
            String taskName = taskNameField.getText();
            String taskDescription = taskDescriptionField.getText();
            LocalDate taskDueDate = TaskDueDateField.getValue();
            Status taskStatus = TaskStatusField.getValue();
            String taskPriority = ((JFXRadioButton) priorityGroup.getSelectedToggle()).getText();

            selectedTask.editTask(taskName, taskDescription, taskDueDate, taskStatus, taskPriority, selectedCategory);
            updateTask(selectedTask);

            editForm.setVisible(false);
            resetMainPaneSize();
            taskListView.refresh();
            taskListView1.refresh();
            taskListView2.refresh();
            if (categoryTasksPane.isVisible()){
                loadTasksForCategory(categoryTitle.getText());
            }
            categoryTasks.refresh();
            hideEditFormVBox(mainVbox, mainVbox1, mainVbox2, mainVbox3);
            showEditFormSearchField(true ,searchField, searchField1, searchField2, searchField3);
            showNotification("Task updated successfully.", "Task", "has been updated", taskName);
            taskListView.getSelectionModel().clearSelection();
            taskListView1.getSelectionModel().clearSelection();
            taskListView2.getSelectionModel().clearSelection();
            categoryTasks.getSelectionModel().clearSelection();
        }
    }

    @FXML
    public void handleCancelButton() {
        editForm.setVisible(false);
        resetMainPaneSize();
        taskListView.refresh();
        hideEditFormVBox(mainVbox, mainVbox1, mainVbox2, mainVbox3);
        showEditFormSearchField(true ,searchField, searchField1, searchField2, searchField3);
        taskListView.getSelectionModel().clearSelection();
        taskListView1.getSelectionModel().clearSelection();
        taskListView2.getSelectionModel().clearSelection();
        categoryTasks.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleDeleteButton() {
        deleteTask(selectedTask);
        tasks.deleteTask(selectedTask);
        editForm.setVisible(false);
        resetMainPaneSize();
        taskListView.refresh();
        taskListView1.refresh();
        taskListView2.refresh();
        categoryTasks.refresh();
        hideEditFormVBox(mainVbox, mainVbox1, mainVbox2, mainVbox3);
        showEditFormSearchField(true, searchField1, searchField2, searchField3, searchField);
    }

    private void hideEditFormVBox(VBox... mainVbox){
        for (VBox vbox : mainVbox){
            vbox.setPadding(new Insets(0, 0, 0, 0));
            vbox.applyCss();
            vbox.layout();
        }
    }

    private void showEditFormSearchField(boolean show, TextField... searchFields) {
        for (TextField searchField : searchFields) {
            searchField.setVisible(show);
        }
        // Adjust padding based on search field visibility
        if (!show) {
            mainVbox.setPadding(new Insets(0, 320, 0, 0));
        } else {
            mainVbox.setPadding(new Insets(0, 0, 0, 0));
        }
        mainVbox.applyCss();
        mainVbox.layout();
    }

    private void resetMainPaneSize() {
        if (mainPane != null) {
            mainPane.setPrefWidth(1200);
            mainPane.setPadding(new Insets(0, 0, 0, 0));
            mainPane.applyCss();
            mainPane.layout();
        }
    }

    @FXML
    public void handleSignOutButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/EntryPage.fxml"));
        Parent root = loader.load();

        try {
            Database.getInstance().deleteSavedUser();
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

    public void deleteTask(TaskImpl task) {
        String deleteQuery = "DELETE FROM tasks WHERE task_id = ?";
        Object[] params = {task.getId()};
        try {
            Database.getInstance().executeUpdate(deleteQuery, params);
            setLatestTaskName(task.getName());
            showNotification("Task deleted successfully.", "Task", "has been deleted", latestTaskName);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateTask(TaskImpl task) {
        String updateQuery = "UPDATE tasks SET task_name = ?, task_description = ?, task_dueDate = ?, task_status = ?, task_priority = ?, category_id = ? WHERE task_id = ?";

        int categoryID = getCategoryID(task.getCategory());

        Object[] params = {
                task.getName(),
                task.getDescription(),
                Date.valueOf(task.getDueDate()),
                task.getStatus().toString(),
                task.getPriority(),
                categoryID,
                task.getId(),
        };

        try {
            Database.getInstance().executeUpdate(updateQuery, params);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private int getCategoryID(String categoryName){
        String query = "SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?";
        try (PreparedStatement preparedStatement = Database.getInstance().getConnection().prepareStatement(query)){
            preparedStatement.setString(1, categoryName);
            preparedStatement.setString(2, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt("category_id");
            }
        } catch (SQLException e){
            System.out.println("Category not found");
        }
        return -1;
    }

    private void loadTasks() {
        String loadTasksQuery = getLoadTasksQuery();
        Object[] params = getQueryParameters();
        try {
            ResultSet resultSet = Database.getInstance().executeQuery(loadTasksQuery, params);
            if (categoryTasksPane.isVisible()){
                loadTasksByCategory(categoryTitle.getText());
                return;
            }
            tasks.getTasks().clear();
            while (resultSet.next()){
                int taskID = resultSet.getInt("task_id");
                String taskName = resultSet.getString("task_name");
                String taskDescription = resultSet.getString("task_description");
                LocalDate taskDueDate = resultSet.getDate("task_dueDate").toLocalDate();
                Status taskStatus = Status.valueOf(resultSet.getString("task_status"));
                String taskPriority = resultSet.getString("task_priority");
                String taskCategory = resultSet.getString("category_name");
                if (taskCategory == null){
                    taskCategory = "General";
                }
                TaskImpl task = new TaskImpl(taskName, taskDescription, taskDueDate, taskStatus, taskPriority, taskCategory);
                task.setId(taskID);
                tasks.addTask(task);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private String getLoadTasksQuery() {
        String loadTasksQuery = "SELECT tasks.task_id, tasks.task_name, tasks.task_description, tasks.task_dueDate, tasks.task_status, tasks.task_priority, categories.category_name "
                + "FROM tasks LEFT JOIN categories ON tasks.category_id = categories.category_id "
                + "WHERE tasks.user_id = ?";

        if (myDayPane.isVisible()) {
            loadTasksQuery += " AND task_startDate = CURDATE()";
        } else if (importantPane.isVisible()) {
            loadTasksQuery += " AND is_important = 1";
        } else if (categoryTasksPane.isVisible()) {
            loadTasksQuery += " AND tasks.category_id = (SELECT categories.category_id FROM categories WHERE categories.category_name = ? AND categories.user_id = ?)";
        }
        return loadTasksQuery;
    }

    private Object[] getQueryParameters() {
        if (categoryTasksPane.isVisible()) {
            String categoryName = categoryTitle.getText();
            if (categoryName == null || categoryName.isEmpty()) {
                throw new IllegalArgumentException("Category name cannot be null or empty.");
            }
            return new Object[]{userID, categoryName, userID};
        } else {
            return new Object[]{userID};
        }
    }

    private void loadTasksForCategory(String categoryName) {
        clearSearch(searchField, searchField1, searchField2, searchField3);
        if (editForm.isVisible()) {
            handleCancelButton();
        }
        categoryTasksPane.setVisible(true);
        myDayPane.setVisible(false);
        displayTasksPane.setVisible(false);
        importantPane.setVisible(false);
        categoryTitle.setText(categoryName);
        mainPane = categoryTasksPane;
        loadTasksByCategory(categoryName);
    }

    private void loadTasksByCategory(String categoryName) {
        String loadTasksQuery = "SELECT tasks.task_id, tasks.task_name, tasks.task_description, tasks.task_dueDate, tasks.task_status, tasks.task_priority "
                + "FROM tasks JOIN categories ON tasks.category_id = categories.category_id "
                + "WHERE tasks.user_id = ? AND categories.category_name = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(loadTasksQuery)) {

            preparedStatement.setString(1, userID);
            preparedStatement.setString(2, categoryName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                tasks.getTasks().clear();
                if (resultSet.next()) {
                    do {
                        int taskID = resultSet.getInt("task_id");
                        String taskName = resultSet.getString("task_name");
                        String description = resultSet.getString("task_description");
                        LocalDate dueDate = resultSet.getDate("task_dueDate").toLocalDate();
                        Status status = Status.valueOf(resultSet.getString("task_status"));
                        String priority = resultSet.getString("task_priority");
                        // Set the category name
                        TaskImpl task = new TaskImpl(taskName, description, dueDate, status, priority, categoryName);
                        task.setId(taskID);
                        tasks.addTask(task);
                    } while (resultSet.next());
                } else {
                    System.out.println("No tasks found for category: " + categoryName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddButton() throws IOException {
        // Loading the addTask fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/addTask.fxml"));
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/miniprojet/assets/fxml/addCategory.fxml"));
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
        ObservableList<String> tempCategories = FXCollections.observableArrayList();
        boolean flag = false;
        String loadCategoriesQuery = "SELECT category_name FROM categories WHERE user_id = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(loadCategoriesQuery)) {

            preparedStatement.setString(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString("category_name").equals("General")) {
                    flag = true;
                    continue;
                }
                String categoryName = resultSet.getString("category_name");
                tempCategories.add(categoryName);
            }
            if (!tempCategories.contains("General") && !flag) {
                String insertQuery = "INSERT INTO categories (category_name, user_id) VALUES ('General', ?)";
                Database.getInstance().executeUpdate(insertQuery, userID);
            }
            categories.add("General");
            categories.addAll(tempCategories);
            System.out.println(categories);
            categoryComboBox.setItems(categories);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setCategoryViewItems(categories, categoryListView, categoryListView1, categoryListView2, categoryListView21);
    }

    @SafeVarargs
    @FXML
    private void setCategoryViewItems(ObservableList<String> categories, ListView<String>... listView) {
        for (ListView<String> list : listView) {
            list.setItems(categories);
        }
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
        handleChangePane(myDayPane, taskListView);
    }

    @FXML
    public void handleImportantButton() {
        handleChangePane(importantPane, taskListView1);
    }

    @FXML
    public void handleDisplayTasksButton() {
        handleChangePane(displayTasksPane, taskListView2);
    }

    private void handleChangePane(AnchorPane pane, ListView<TaskImpl> listView){
        clearSearch(searchField, searchField1, searchField2, searchField3);
        resetMainPaneSize();
        if (editForm.isVisible()) {
            handleCancelButton();
        }
        myDayPane.setVisible(false);
        displayTasksPane.setVisible(false);
        importantPane.setVisible(false);
        categoryTasksPane.setVisible(false);
        pane.setVisible(true);
        mainPane = pane;
        loadTasks();
        loadCategories();
        taskListView.refresh();
        categoryListView.refresh();
    }

    private void clearSearch(TextField... searchFields){
        for (TextField searchField : searchFields){
            searchField.clear();
        }
    }
}