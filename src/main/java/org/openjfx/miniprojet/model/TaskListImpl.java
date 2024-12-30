package org.openjfx.miniprojet.model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * TaskListImpl is a concrete implementation of the TaskList class.
 * It manages a list of TaskImpl objects and provides methods to add, delete, edit, display, search, filter, sort, export, and import tasks.
 * Note: This class uses Gson for JSON serialization and deserialization
 *
 * @see TaskList
 * @see TaskImpl
 * @see Status
 *
 * @version 1.0
 * author Sellami Mohamed Odai
 */
public class TaskListImpl extends TaskList{

    private final ObservableList<TaskImpl> tasks;

    /**
     * Constructs a new TaskListImpl with an empty task list.
     */
    public TaskListImpl() {
        this.tasks = FXCollections.observableArrayList();
    }

    /**
     * Constructs a new TaskListImpl with the specified task list.
     *
     * @param tasks the initial list of tasks
     */
    public TaskListImpl(ObservableList<TaskImpl> tasks){
        this.tasks = tasks;
    }

    /**
     * Gets the list of tasks.
     *
     * @return the list of tasks
     */
    public ObservableList<TaskImpl> getTasks(){
        return tasks;
    }

    /**
     * Sets the list of tasks.
     *
     * @param tasks the new list of tasks
     */
    public void setTasks(ObservableList<TaskImpl> tasks){
        this.tasks.setAll(tasks);
    }

    /**
     * Adds a task to the task list.
     *
     * @param task the task to be added
     */
    @Override
    public void addTask(TaskImpl task) {
        tasks.add(task);
    }

    /**
     * Deletes the specified task from the task list.
     *
     * @param task the task to be deleted
     */
    @Override
    public void deleteTask(TaskImpl task) {
        tasks.remove(task);
    }

    /**
     * Edits the specified task with its current details.
     *
     * @param task the task to be edited
     */
    @Override
    public void editTask(TaskImpl task) {
        task.editTask(task.getName(), task.getDescription(), task.getDueDate(), task.getPriority(), task.getCategory(), task.getStartDate(), task.getStatus(), task.getReminder());
    }

    /**
     * Returns the list of tasks.
     *
     * @return a list of TaskImpl objects representing the tasks.
     */
    @Override
    public List<TaskImpl> displayTasks() {
        return tasks;
    }

    /**
     * Searches for tasks that contain the keyword in either the name or description.
     *
     * @param keyword the keyword to search for
     * @return a list of tasks that contain the keyword in the name or description
     */
    public List<TaskImpl> searchTasks(String keyword) {
        List<TaskImpl> matchingTasks = new ArrayList<>();
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }

        for (TaskImpl task: tasks) {
            if (task.getName() != null && task.getName().toLowerCase().contains(keyword.toLowerCase())) {
                matchingTasks.add(task);
            } else if (task.getDescription() != null && task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Filters tasks by a certain status (completed/abandoned/in\_progress).
     *
     * @param status the status to filter the tasks by
     * @return a list of tasks filtered by status
     */
    public List<TaskImpl> filterTasks(Status status) {
        List<TaskImpl> matchingTasks = new ArrayList<>();
        for (TaskImpl task: tasks) {
            if (task.getStatus().equals(status)){
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Sorts tasks by their due date and returns them.
     *
     * @param currentTasks the current list of tasks
     * @return a list of tasks sorted by due date
     */
    public ObservableList<TaskImpl> sortTaskByDueDate(ObservableList<TaskImpl> currentTasks) {
        // Create a copy of the currentTasks list
        ObservableList<TaskImpl> sortedTasks = FXCollections.observableArrayList(currentTasks);
        // Sort the copied list
        sortedTasks.sort(Comparator.comparing(TaskImpl::getDueDate));
        return sortedTasks;
    }

    /**
     * Filters tasks by a certain category.
     *
     * @param currentTasks the current list of tasks
     * @param category the category to filter the tasks by
     * @return a list of tasks filtered by category
     */
    public ObservableList<TaskImpl> categoryTasks(ObservableList<TaskImpl> currentTasks, String category) {
        ObservableList<TaskImpl> categoryTasks = FXCollections.observableArrayList();
        for (TaskImpl task : currentTasks) {
            if (task.getCategory().equals(category)) {
                categoryTasks.add(task);
            }
        }
        return categoryTasks;
    }

    /**
     * Filters tasks by a certain priority.
     *
     * @param currentTasks the current list of tasks
     * @param priority the priority to filter the tasks by
     * @return a list of tasks filtered by priority
     */
    public ObservableList<TaskImpl> priorityTasks(ObservableList<TaskImpl> currentTasks, String priority) {
        ObservableList<TaskImpl> priorityTasks = FXCollections.observableArrayList();
        for (TaskImpl task : currentTasks) {
            if (task.getPriority().equals(priority)) {
                priorityTasks.add(task);
            }
        }
        return priorityTasks;
    }

    /**
     * Filters tasks by a certain status.
     *
     * @param currentTasks the current list of tasks
     * @param status the status to filter the tasks by
     * @return a list of tasks filtered by status
     */
    public ObservableList<TaskImpl> statusTasks(ObservableList<TaskImpl> currentTasks, Status status) {
        ObservableList<TaskImpl> statusTasks = FXCollections.observableArrayList();
        for (TaskImpl task : currentTasks) {
            if (task.getStatus().equals(status)) {
                statusTasks.add(task);
            }
        }
        return statusTasks;
    }

    /**
     * Sorts tasks by their priority and returns them.
     *
     * @param currentTasks the current list of tasks
     * @return a list of tasks sorted by priority
     */
    public ObservableList<TaskImpl> sortTasksByPriority(ObservableList<TaskImpl> currentTasks) {
        ObservableList<TaskImpl> sortedTasks = FXCollections.observableArrayList();
        // Add High priority tasks
        for (TaskImpl task : currentTasks) {
            if ("High".equals(task.getPriority())) {
                sortedTasks.add(task);
            }
        }
        // Add Medium priority tasks
        for (TaskImpl task : currentTasks) {
            if ("Medium".equals(task.getPriority())) {
                sortedTasks.add(task);
            }
        }
        // Add Low priority tasks
        for (TaskImpl task : currentTasks) {
            if ("Low".equals(task.getPriority())) {
                sortedTasks.add(task);
            }
        }
        return sortedTasks;
    }

    /**
     * Exports the list of tasks to a specified file in JSON format.
     *
     * @param file the path of the file where the tasks will be exported
     */
    public void exportTasks(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try(FileWriter writer = new FileWriter(file)){
            gson.toJson(tasks, writer);
            System.out.println("Tasks exported to: "+file.getAbsolutePath());
        } catch(IOException e) {
            System.err.println("An error occurred while exporting tasks: " + e.getMessage());
        }
    }

    /**
     * Imports tasks from a JSON file specified by the given file path.
     * The tasks are read from the file and added to the existing task list.
     *
     * @param filePath the path to the JSON file containing the tasks to import
     */
    public void importTasks(String filePath) {
        Gson gson = new Gson();
        try(FileReader reader = new FileReader(filePath)){
            Type taskListType = new TypeToken<ArrayList<TaskImpl>>() {}.getType();
            List<TaskImpl> importedTasks = gson.fromJson(reader, taskListType);
            if (importedTasks != null) {
                tasks.addAll(importedTasks);
                System.out.println("Tasks imported from: "+filePath);
            }

        } catch(IOException e) {
            System.err.println("An error occurred while importing tasks: " + e.getMessage());
        }
    }
}